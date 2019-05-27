package io.shadowrealm.skyprime;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.SpigotWorldConfig;
import org.spigotmc.TickLimiter;

import io.shadowrealm.skyprime.nms.NMSX;
import io.shadowrealm.skyprime.nms.SkyThread;
import io.shadowrealm.skyprime.nms.SpecializedTickLimiter;
import io.shadowrealm.skyprime.nms.TicklistTrimmer;
import io.shadowrealm.skyprime.storage.Island;
import io.shadowrealm.skyprime.storage.Visibility;
import mortar.api.particle.ParticleEffect;
import mortar.api.sched.S;
import mortar.api.world.Cuboid;
import mortar.api.world.Cuboid.CuboidDirection;
import mortar.api.world.W;
import mortar.bukkit.command.MortarSender;
import mortar.bukkit.plugin.Mortar;
import mortar.compute.math.M;
import mortar.compute.math.Profiler;
import mortar.lang.collection.FinalDouble;
import mortar.lang.collection.GList;
import mortar.lang.collection.GMap;
import mortar.logic.format.F;
import mortar.logic.io.VIO;
import mortar.util.text.D;

public class VirtualIsland implements Listener
{
	private World world;
	private Boolean readyToFlush;
	private SpecializedTickLimiter eTick;
	private SpecializedTickLimiter tTick;
	private TicklistTrimmer trimmer;
	private Island island;
	private SkyThread t;
	private GMap<String, Double> draw;
	private double lastUse;
	private double voltageForEntities;
	private double voltageForTiles;
	private long ticks;
	private int startup;
	private int idlecount;

	public VirtualIsland(World world, Island island)
	{
		this.world = world;
		ticks = 0;
		t = new SkyThread(island.getId());
		t.start();
		readyToFlush = true;
		draw = new GMap<>();
		this.island = island;
		drawPower("startup", Config.STARTUP_VOLTAGE);
		voltageForEntities = 1;
		voltageForTiles = 1;
		lastUse = 0;
		idlecount = 0;
		startup = Config.SPINUP_TIME;
		inject();
	}

	public void preUnload()
	{
		trimmer.dumpTicklist();
		trimmer.setDelay(0);
	}

	public void tick()
	{
		if(startup > 0)
		{
			startup--;
		}

		if(ticks % 5 == 0)
		{
			if(startup <= 0)
			{
				modified();
			}

			if(startup <= 0 && world.getPlayers().isEmpty())
			{
				idlecount += 5;

				if(idlecount > Config.IDLE_TICKS)
				{
					preUnload();

					new S(1)
					{
						@Override
						public void run()
						{
							unload();

							Player p = Bukkit.getPlayer(island.getOwner());

							if(p != null)
							{
								p.sendMessage("Your island was saved & unloaded.");
							}
						}
					};
				}
			}

			else
			{
				idlecount -= 5;
				updateValue();
				updateSize();
				updateVoltages();
				updateVisitors();
			}

			idlecount = (int) M.clip(idlecount, 0, 10000);
		}

		if(ticks % 20 == 0)
		{
			for(Player i : world.getPlayers())
			{
				if(i.getLocation().getY() < 0)
				{
					if(i.getUniqueId().equals(island.getOwner()))
					{
						spawn(i);
					}

					else
					{
						warp(i);
					}
				}
			}
		}

		if(ticks % 4 == 0)
		{
			if(draw.containsKey("physics") && draw.get("physics") > getTotalVolts() * Config.PHYSICS_GEAR_RATIO)
			{
				trimmer.setDelay(trimmer.getDelay() + 1);
			}

			else
			{
				trimmer.setDelay(trimmer.getDelay() - 1);
			}

			trimmer.setDelay(trimmer.getDelay() < 0 ? 0 : trimmer.getDelay() > Config.PHYSICS_THROTTLE ? Config.PHYSICS_THROTTLE : trimmer.getDelay());
		}

		trimmer.tick();
		ticks++;
	}

	private void updateVisitors()
	{
		if(startup > 5)
		{
			World safe = Bukkit.getWorld("world");

			if(safe == null)
			{
				System.out.println("Cannot unload a null world (or safe world 'world' is null)");
				return;
			}

			for(Player i : world.getPlayers())
			{
				i.sendMessage("Island is still booting up.");
				i.teleport(safe.getSpawnLocation());
			}
		}

		if(island.getVisibility().equals(Visibility.PRIVATE))
		{
			World safe = Bukkit.getWorld("world");

			if(safe == null)
			{
				System.out.println("Cannot unload a null world (or safe world 'world' is null)");
				return;
			}

			for(Player i : world.getPlayers())
			{
				if(!i.getUniqueId().equals(island.getOwner()) && !island.getMembers().contains(i.getUniqueId()))
				{
					i.sendMessage("The island you were in is private.");
					i.teleport(safe.getSpawnLocation());
				}
			}
		}
	}

	public double getPhysicsSpeed()
	{
		return 1D - ((double) trimmer.getDelay() / 36D);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void on(BlockFormEvent e)
	{
		if(e.getBlock().getWorld().equals(world) && e.getNewState().getType().equals(Material.COBBLESTONE))
		{
			e.getNewState().setType(generatesCobble());
			island.setLevel(island.getLevel() + getValue(e.getNewState().getType(), e.getNewState().getData().getData()));
		}
	}

	@EventHandler
	public void on(EntityPickupItemEvent e)
	{
		if(!e.getEntity().getWorld().equals(world))
		{
			return;
		}

		if(e.getEntity() instanceof Player)
		{
			Player p = (Player) e.getEntity();

			if(island.getMembers().contains(p.getUniqueId()) || island.getOwner().equals(p.getUniqueId()))
			{
				return;
			}

			if(!island.iscPublicPickup())
			{
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void on(ItemMergeEvent e)
	{
		if(!e.getEntity().getWorld().equals(world))
		{
			return;
		}

		if(island.getcMergeItem() <= 0)
		{
			e.setCancelled(true);
		}

		if(e.getEntity().getLocation().distanceSquared(e.getTarget().getLocation()) >= Math.pow(island.getcMergeItem(), 2))
		{
			e.setCancelled(true);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void on(BlockBreakEvent e)
	{
		if(!e.getBlock().getWorld().equals(world))
		{
			return;
		}

		if(!canBuild(e.getPlayer()))
		{
			denyBuild(e.getBlock().getLocation().clone().add(0.5, 0.5, 0.5));
			e.setCancelled(true);
			return;
		}

		island.setLevel(island.getLevel() - getValue(e.getBlock().getType(), e.getBlock().getData()));

		modified();
	}

	private boolean canBuild(Player player)
	{
		if(player.getUniqueId().equals(island.getOwner()) || isMember(player))
		{
			return true;
		}

		return false;
	}

	private void denyBuild(Location add)
	{
		ParticleEffect.SWEEP_ATTACK.display(1f, 1, add, 32);
	}

	@EventHandler
	public void on(BlockPhysicsEvent e)
	{
		if(!e.getBlock().getWorld().equals(world))
		{
			return;
		}

		drawPower("physics", 0.005D);
	}

	@EventHandler
	public void on(BlockFromToEvent e)
	{
		if(!e.getBlock().getWorld().equals(world))
		{
			return;
		}

		drawPower("physics", 0.009D);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void on(BlockPlaceEvent e)
	{
		if(!e.getBlock().getWorld().equals(world))
		{
			return;
		}

		if(!canBuild(e.getPlayer()))
		{
			e.setCancelled(true);
			denyBuild(e.getBlock().getLocation().clone().add(0.5, 0.5, 0.5));
			return;
		}

		island.setLevel(island.getLevel() + getValue(e.getBlock().getType(), e.getBlock().getData()));
		drawPower("physics", 1D);
		modified();
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onlp(BlockBreakEvent e)
	{
		if(e.isCancelled())
		{
			return;
		}

		if(!e.getBlock().getWorld().equals(world))
		{
			return;
		}

		if(!canBuild(e.getPlayer()))
		{
			e.setCancelled(true);
			denyBuild(e.getBlock().getLocation().clone().add(0.5, 0.5, 0.5));
			return;
		}

		drawPower("physics", 1D);

		if((e.getBlock().getType().equals(Material.STONE) || e.getBlock().getType().equals(Material.DIAMOND_ORE) || e.getBlock().getType().equals(Material.COAL_ORE) || e.getBlock().getType().equals(Material.IRON_ORE) || e.getBlock().getType().equals(Material.EMERALD_ORE) || e.getBlock().getType().equals(Material.LAPIS_ORE) || e.getBlock().getType().equals(Material.REDSTONE_ORE) || e.getBlock().getType().equals(Material.GLOWING_REDSTONE_ORE) || e.getBlock().getType().equals(Material.GOLD_ORE) || e.getBlock().getType().equals(Material.QUARTZ_ORE)))
		{
			boolean w = false;
			boolean l = false;

			for(Block i : W.blockFaces(e.getBlock()))
			{
				if(i.getType().equals(Material.LAVA) || i.getType().equals(Material.STATIONARY_LAVA))
				{
					l = true;
				}

				if(i.getType().equals(Material.WATER) || i.getType().equals(Material.STATIONARY_WATER))
				{
					w = true;
				}
			}

			if(w && l)
			{
				e.setDropItems(false);

				if(e.getExpToDrop() > 0)
				{
					e.getPlayer().giveExp(e.getExpToDrop());
					e.setExpToDrop(0);
					e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.3f, 1.54f);
				}

				new S(0)
				{
					@Override
					public void run()
					{
						e.getBlock().setType(generatesCobble());
						island.setLevel(island.getLevel() + getValue(e.getBlock().getType(), e.getBlock().getData()));
					}
				};

				for(ItemStack i : e.getBlock().getDrops(e.getPlayer().getItemInHand() != null ? e.getPlayer().getItemInHand() : new ItemStack(Material.AIR)))
				{
					world.dropItemNaturally(e.getBlock().getLocation().clone().add(0.5, 0.5, 0.5), i).setPickupDelay(0);
				}
			}
		}
	}

	@EventHandler
	public void on(InventoryOpenEvent e)
	{
		if(e.getInventory().getHolder() instanceof BlockState)
		{
			if(!((BlockState) e.getInventory().getHolder()).getWorld().equals(world))
			{
				return;
			}

			if(e.getPlayer() instanceof Player)
			{
				if(!canBuild((Player) e.getView().getPlayer()))
				{
					e.setCancelled(true);
					denyBuild(((BlockState) e.getInventory().getHolder()).getLocation().getBlock().getLocation().clone().add(0.5, 0.5, 0.5));
					return;
				}
			}

			modified();
		}
	}

	private Material generatesCobble()
	{
		double div = 470;
		drawPower("physics", 3D);

		if(M.r(0.82D / div))
		{
			return Material.DIAMOND_ORE;
		}

		else if(M.r(2.89D / div))
		{
			return Material.GOLD_ORE;
		}

		else if(M.r(0.74D / div))
		{
			return Material.EMERALD_ORE;
		}

		else if(M.r(4.59D / div))
		{
			return Material.IRON_ORE;
		}

		else if(M.r(1.56D / div))
		{
			return Material.LAPIS_ORE;
		}

		else if(M.r(2.32D / div))
		{
			return Material.QUARTZ_ORE;
		}

		else if(M.r(0.98D / div))
		{
			return Material.REDSTONE_ORE;
		}

		else if(M.r(6.99D / div))
		{
			return Material.COAL_ORE;
		}

		return Material.STONE;
	}

	public double getTotalVolts()
	{
		return Voltage.getTotalIslandVoltage(island.getValue());
	}

	public double getBonusVolts()
	{
		return Voltage.getIslandBonusVoltage();
	}

	public double getBaseVolts()
	{
		return Voltage.getIslandBaseVoltage(island.getValue());
	}

	public double getUsedVolts()
	{
		return getUsedEntityVolts() + getUsedTileVolts() + lastUse;
	}

	public double getUsedEntityVolts()
	{
		return Voltage.getVolts(eTick.getAtimes().getAverage());
	}

	public double getUsedTileVolts()
	{
		return Voltage.getVolts(tTick.getAtimes().getAverage());
	}

	public World getWorld()
	{
		return world;
	}

	public void setWorld(World world)
	{
		this.world = world;
	}

	public Island getIsland()
	{
		return island;
	}

	public void setIsland(Island island)
	{
		this.island = island;
	}

	private void updateValue()
	{
		if(M.ms() - island.getLastValueCalculation() > TimeUnit.SECONDS.toMillis(30) && M.r(0.25) && island.isNeedsRescan())
		{
			island.setLastValueCalculation(M.ms());
			island.setNeedsRescan(false);
			calculateValue();
		}
	}

	private int getMaxIslandSize()
	{
		return island.getMaxSize();
	}

	private void updateSize()
	{
		double ib = Math.pow(Math.max(island.getLevel(), island.getValue()), Config.FRACTAL_VALUE) / 5 / Config.DIVISOR_VALUE;
		double bonus = ib;
		world.getWorldBorder().setCenter(0, 0);
		world.getWorldBorder().setWarningDistance(10);
		world.getWorldBorder().setWarningTime(30);
		world.getWorldBorder().setDamageAmount(0.5);
		world.getWorldBorder().setSize(Math.min((2 * island.getMinsize()) + bonus, getMaxIslandSize()), Config.ANIMATION_SIZE);
	}

	public void delete()
	{
		new File("skydata/deletions/" + island.getId().toString()).mkdirs();
		unload();
		SkyMaster.getStorageEngine().removeIsland(island);
		VIO.delete(new File(SkyMaster.worldName(island.getId())));
	}

	public void spawn(Player p)
	{
		new S(startup + 5)
		{
			@Override
			public void run()
			{
				p.teleport(island.getSpawn(world));
				flushTracker();
				if(island.getOwner().equals(p.getUniqueId()))
				{
					int s = SkyMaster.getSizeFor(p);
					if(island.getMaxSize() != s)
					{
						island.setMaxSize(s);
						saveIsland();
					}
				}
			}
		};
	}

	private void flushTracker()
	{
		if(readyToFlush)
		{
			SkyMaster.flushTracker(world);
			readyToFlush = false;
		}
	}

	public void warp(Player p)
	{
		new S(startup + 5)
		{
			@Override
			public void run()
			{
				p.teleport(island.getWarp(world));
				flushTracker();
			}
		};
	}

	public void setSpawn(Player p)
	{
		island.setSpawn(p.getLocation());
		world.setSpawnLocation(p.getLocation());
		saveAll();
	}

	public void setWarp(Player p)
	{
		island.setWarp(p.getLocation());
		saveAll();
	}

	public void saveConfig(MortarSender s)
	{
		Island is = island;
		if(is.getcMergeItem() > 8)
		{
			is.setcMergeItem(8);
			s.sendMessage("merge.item reduced to 8");
		}

		if(is.getcMergeItem() < 0)
		{
			is.setcMergeItem(0);
			s.sendMessage("merge.item increased to 0");
		}

		if(is.getcMergeXp() > 8)
		{
			is.setcMergeXp(8);
			s.sendMessage("merge.xp reduced to 8");
		}

		if(is.getcMergeXp() < 0)
		{
			is.setcMergeXp(0);
			s.sendMessage("merge.xp increased to 0");
		}

		if(is.getcHopperAmount() > Config.HOPPER_MAX_AMT)
		{
			is.setcHopperAmount(Config.HOPPER_MAX_AMT);
			s.sendMessage("hopper.amount reduced to " + Config.HOPPER_MAX_AMT);
		}

		if(is.getcHopperAmount() < 1)
		{
			is.setcHopperAmount(1);
			s.sendMessage("hopper.amount increased to 1");
		}

		if(is.getcHopperRate() > 100)
		{
			is.setcHopperRate(100);
			s.sendMessage("hopper.rate reduced to 100");
		}

		if(is.getcHopperRate() < Config.HOPPER_MIN_INTERVAL)
		{
			is.setcHopperRate(Config.HOPPER_MIN_INTERVAL);
			s.sendMessage("hopper.rate increased to " + Config.HOPPER_MIN_INTERVAL);
		}

		if(is.getcDespawnArrow() < 1)
		{
			is.setcDespawnArrow(1);
			s.sendMessage("despawn.arrow increased to 1");
		}

		if(is.getcDespawnItem() < 1)
		{
			is.setcDespawnItem(1);
			s.sendMessage("despawn.item increased to 1");
		}

		saveIsland();
	}

	public void save()
	{
		world.save();
		drawPower("save", 700D);
	}

	public void unload()
	{
		if(Mortar.isMainThread())
		{
			try
			{
				releaseWorld(world);
			}

			catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException e)
			{
				e.printStackTrace();
			}

			SkyMaster.unloadWorld(world, true);
			SkyMaster.remove(island);
			t.interrupt();
		}

		else
		{
			D.as("SkyPrime").w("Not unloading " + world.getName() + ". Cant while async.");
		}
	}

	public void unloadFuckingNow()
	{
		try
		{
			releaseWorld(world);
		}

		catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException e)
		{
			e.printStackTrace();
		}

		SkyMaster.unloadWorldRightFuckingNow(world, true);
		SkyMaster.remove(island);
		t.interrupt();
	}

	public void q(Runnable r)
	{
		t.q(r);
	}

	public void drawPower(String reason, double volts)
	{
		if(draw.containsKey(reason))
		{
			draw.put(reason, volts + draw.get(reason));
		}

		else
		{
			draw.put(reason, volts);
		}
	}

	public GMap<String, Double> getDraw()
	{
		return draw;
	}

	private void updateVoltages()
	{
		double totalAllowedVoltage = Voltage.getTotalIslandVoltage(island.getValue());
		double consumedVoltage = computeConsumedVoltage(totalAllowedVoltage);
		totalAllowedVoltage -= consumedVoltage;
		lastUse = consumedVoltage;
		voltageForEntities = island.getPercentEntities() * totalAllowedVoltage;
		voltageForTiles = island.getPercentTiles() * totalAllowedVoltage;
		eTick.setrMaxTime(Math.max(0.01, Voltage.getMilliseconds(voltageForEntities)));
		tTick.setrMaxTime(Math.max(0.01, Voltage.getMilliseconds(voltageForTiles)));
	}

	private double computeConsumedVoltage(double max)
	{
		double use = 0;

		for(String i : draw.k())
		{
			use += draw.get(i);
			draw.put(i, draw.get(i) / 1.45D);

			if(draw.get(i) < 1)
			{
				draw.remove(i);
			}
		}

		return Math.min(use, max);
	}

	private void calculateValue()
	{
		Profiler pa = new Profiler();
		Profiler pb = new Profiler();
		Profiler pc = new Profiler();
		Profiler pd = new Profiler();
		pa.begin();
		Cuboid c = new Cuboid(new Location(world, 0, 0, 0));
		c = c.expand(CuboidDirection.North, (int) (world.getWorldBorder().getSize() / 2) + 5);
		c = c.expand(CuboidDirection.South, (int) (world.getWorldBorder().getSize() / 2) + 5);
		c = c.expand(CuboidDirection.East, (int) (world.getWorldBorder().getSize() / 2) + 5);
		c = c.expand(CuboidDirection.West, (int) (world.getWorldBorder().getSize() / 2) + 5);
		GList<ChunkSnapshot> snaps = new GList<>();
		FinalDouble d = new FinalDouble(0D);
		GList<Location> inventories = new GList<>();
		int m = 0;

		pb.begin();
		for(Chunk i : c.getChunks())
		{
			m++;
			new S(m / 8)
			{
				@Override
				public void run()
				{
					snaps.add(i.getChunkSnapshot(false, false, false));
					drawPower("value-calculation", 50D);
				}
			};
		}

		new S((m / 8) + 1)
		{
			@Override
			public void run()
			{
				pb.end();

				q(new Runnable()
				{
					@Override
					public void run()
					{
						pc.begin();
						for(ChunkSnapshot s : snaps)
						{
							for(int i = 0; i < 16; i++)
							{
								for(int j = 0; j < 256; j++)
								{
									for(int k = 0; k < 16; k++)
									{
										Material m = s.getBlockType(i, j, k);
										@SuppressWarnings("deprecation")
										byte b = (byte) s.getBlockData(i, j, k);
										d.add(getValue(m, b));
										boolean inv = false;
										switch(m)
										{
											case CHEST:
												inv = true;
												break;
											case TRAPPED_CHEST:
												inv = true;
												break;
											case BLACK_SHULKER_BOX:
												inv = true;
												break;
											case BROWN_SHULKER_BOX:
												inv = true;
												break;
											case BLUE_SHULKER_BOX:
												inv = true;
												break;
											case CYAN_SHULKER_BOX:
												inv = true;
												break;
											case GRAY_SHULKER_BOX:
												inv = true;
												break;
											case GREEN_SHULKER_BOX:
												inv = true;
												break;
											case LIGHT_BLUE_SHULKER_BOX:
												inv = true;
												break;
											case LIME_SHULKER_BOX:
												inv = true;
												break;
											case MAGENTA_SHULKER_BOX:
												inv = true;
												break;
											case ORANGE_SHULKER_BOX:
												inv = true;
												break;
											case PINK_SHULKER_BOX:
												inv = true;
												break;
											case PURPLE_SHULKER_BOX:
												inv = true;
												break;
											case RED_SHULKER_BOX:
												inv = true;
												break;
											case SILVER_SHULKER_BOX:
												inv = true;
												break;
											case WHITE_SHULKER_BOX:
												inv = true;
												break;
											case YELLOW_SHULKER_BOX:
												inv = true;
												break;
											default:
												break;
										}

										if(inv)
										{
											inventories.add(new Location(world, (s.getX() << 4) + i, j, (s.getZ() << 4) + k));
										}
									}
								}
							}
						}

						pc.end();
						pd.begin();

						int k = 0;

						for(Location i : inventories)
						{
							k++;

							new S(k / 8)
							{
								@SuppressWarnings("deprecation")
								@Override
								public void run()
								{
									Block b = i.getBlock();

									if(b.getState() instanceof InventoryHolder)
									{
										Inventory inv = ((InventoryHolder) b.getState()).getInventory();
										ItemStack[] isx = inv.getContents();
										ItemStack[] isxx = inv.getStorageContents();

										for(int i = 0; i < isx.length; i++)
										{
											ItemStack c = isx[i];

											if(c != null)
											{
												d.add(getValue(c.getType(), c.getData().getData()) * c.getAmount());
											}
										}

										for(int i = 0; i < isxx.length; i++)
										{
											ItemStack c = isxx[i];

											if(c != null)
											{
												d.add(getValue(c.getType(), c.getData().getData()) * c.getAmount());
											}
										}
									}
								}
							};
						}

						new S((k + 1) / 8)
						{
							@Override
							public void run()
							{
								pd.end();
								double newValue = d.get();
								island.setValue(newValue);
								island.setLastValueCalculation(M.ms());
								island.setNeedsRescan(false);
								saveIsland();
								pa.end();
								System.out.println("Island Scan of " + island.getId() + " completed in " + F.time(pa.getMilliseconds(), 1) + " Snapshot Time: " + F.time(pb.getMilliseconds(), 0) + " Async Scan Time: " + F.time(pc.getMilliseconds(), 0) + " Inventory Scan Time: " + F.time(pd.getMilliseconds(), 0));
							}
						};
					}
				});
			}
		};
	}

	public void modified()
	{
		island.setNeedsRescan(true);
	}

	public void saveAll()
	{
		save();
		saveIsland();
	}

	public void saveIsland()
	{
		SkyMaster.getStorageEngine().setIsland(island);
	}

	private double getValue(Material m, byte b)
	{
		if(m.equals(Material.AIR))
		{
			return 0;
		}

		double v = 0.00001782;
		String k = "worth." + m.name().toLowerCase().replaceAll("_", "") + "." + String.valueOf((int) b);
		if(SkyMaster.getWorthConfig().contains(k))
		{
			v += (SkyMaster.getWorthConfig().getDouble(k) / 117.5D);
		}

		return v;
	}

	public double getAllowedEntityVoltage()
	{
		return voltageForEntities;
	}

	public double getAllowedTileVoltage()
	{
		return voltageForTiles;
	}

	public void inject()
	{
		try
		{
			tweakIsland(island, world);
			trimmer = new TicklistTrimmer(this);
			trimmer.setDelay(5);
		}

		catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException e)
		{
			e.printStackTrace();
		}
	}

	private void forceSet(SpigotWorldConfig v, String key, Object value, String name) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException
	{
		Field f = v.getClass().getDeclaredField("config"); //$NON-NLS-1$
		f.setAccessible(true);
		YamlConfiguration fc = (YamlConfiguration) f.get(v);
		fc.set("world-settings." + name + "." + key, value); //$NON-NLS-1$
	}

	private SpigotWorldConfig getSpigotConfig(World world) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException
	{
		Class<?> cworldclass = NMSX.getCBClass("CraftWorld"); //$NON-NLS-1$
		Object theWorld = cworldclass.getMethod("getHandle").invoke(world); //$NON-NLS-1$
		SpigotWorldConfig wc = (SpigotWorldConfig) theWorld.getClass().getField("spigotConfig").get(theWorld);

		return wc;
	}

	public void tweakEntityTickMax(World world, int tt) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException
	{
		SpigotWorldConfig wc = getSpigotConfig(world);
		wc.entityMaxTickTime = tt;
		forceSet(wc, "max-tick-time.entity", tt, world.getName()); //$NON-NLS-1$
		Class<?> cworldclass = NMSX.getCBClass("CraftWorld"); //$NON-NLS-1$
		Object theWorld = cworldclass.getMethod("getHandle").invoke(world); //$NON-NLS-1$
		Field f = deepFindField(theWorld, "entityLimiter"); //$NON-NLS-1$

		if(f != null)
		{
			f.setAccessible(true);
			f.set(theWorld, new TickLimiter(tt));
		}
	}

	public void tweakTileTickMax(World world, int tt) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException
	{
		SpigotWorldConfig wc = getSpigotConfig(world);
		wc.entityMaxTickTime = tt;
		forceSet(wc, "max-tick-time.tile", tt, world.getName()); //$NON-NLS-1$
		Class<?> cworldclass = NMSX.getCBClass("CraftWorld"); //$NON-NLS-1$
		Object theWorld = cworldclass.getMethod("getHandle").invoke(world); //$NON-NLS-1$
		Field f = deepFindField(theWorld, "entityLimiter"); //$NON-NLS-1$

		if(f != null)
		{
			f.setAccessible(true);
			f.set(theWorld, new TickLimiter(tt));
		}
	}

	public boolean isMember(Player p)
	{
		return island.getMembers().contains(p.getUniqueId());
	}

	private void tweakIsland(Island is, World world) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException
	{
		SpigotWorldConfig wc = getSpigotConfig(world);
		wc.tileMaxTickTime = 1;
		wc.entityMaxTickTime = 1;
		wc.maxTntTicksPerTick = 1;
		wc.saveStructureInfo = false;
		wc.animalActivationRange = 7;
		wc.tickInactiveVillagers = false;
		wc.arrowDespawnRate = is.getcDespawnArrow();
		wc.expMerge = is.getcMergeXp();
		wc.itemMerge = is.getcMergeItem();
		wc.itemDespawnRate = is.getcDespawnItem();
		wc.viewDistance = 4;
		wc.hopperTransfer = is.getcHopperRate();
		wc.hopperAmount = is.getcHopperAmount();
		wc.hangingTickFrequency = 200;
		witholdWorld(world);
	}

	private void witholdWorld(World w) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		Class<?> cworldclass = NMSX.getCBClass("CraftWorld");
		Object theWorld = cworldclass.getMethod("getHandle").invoke(w);
		Field fe = deepFindField(theWorld, "entityLimiter");
		Field ft = deepFindField(theWorld, "tileLimiter");
		eTick = new SpecializedTickLimiter(0.3D);
		tTick = new SpecializedTickLimiter(0.7D);
		fe.setAccessible(true);
		ft.setAccessible(true);
		fe.set(theWorld, eTick);
		ft.set(theWorld, tTick);
		SkyPrime.instance.registerListener(this);
	}

	private void releaseWorld(World w) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException
	{
		Class<?> cworldclass = NMSX.getCBClass("CraftWorld");
		Object theWorld = cworldclass.getMethod("getHandle").invoke(w);
		Field fe = deepFindField(theWorld, "entityLimiter");
		Field ft = deepFindField(theWorld, "tileLimiter");
		TickLimiter ste = new TickLimiter(5);
		TickLimiter stt = new TickLimiter(5);
		fe.setAccessible(true);
		ft.setAccessible(true);
		fe.set(theWorld, ste);
		ft.set(theWorld, stt);
		SkyPrime.instance.unregisterListener(this);
	}

	private Field deepFindField(Object obj, String fieldName)
	{
		Class<?> cls = obj.getClass();

		for(Class<?> acls = cls; acls != null; acls = acls.getSuperclass())
		{
			try
			{
				Field field = acls.getDeclaredField(fieldName);

				return field;
			}

			catch(NoSuchFieldException ex)
			{

			}
		}

		return null;
	}
}
