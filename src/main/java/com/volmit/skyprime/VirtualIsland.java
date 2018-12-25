package com.volmit.skyprime;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.SpigotWorldConfig;
import org.spigotmc.TickLimiter;

import com.volmit.skyprime.nms.NMSX;
import com.volmit.skyprime.nms.SpecializedTickLimiter;
import com.volmit.skyprime.storage.Island;
import com.volmit.volume.bukkit.command.VolumeSender;
import com.volmit.volume.bukkit.task.A;
import com.volmit.volume.bukkit.task.S;
import com.volmit.volume.bukkit.util.world.Cuboid;
import com.volmit.volume.bukkit.util.world.Cuboid.CuboidDirection;
import com.volmit.volume.lang.collections.FinalDouble;
import com.volmit.volume.lang.collections.GList;
import com.volmit.volume.lang.format.F;
import com.volmit.volume.lang.io.VIO;
import com.volmit.volume.math.M;
import com.volmit.volume.math.Profiler;

public class VirtualIsland implements Listener
{
	private World world;
	private SpecializedTickLimiter eTick;
	private SpecializedTickLimiter tTick;
	private Island island;

	public VirtualIsland(World world, Island island)
	{
		this.world = world;
		this.island = island;
		inject();
	}

	@EventHandler
	public void on(BlockBreakEvent e)
	{
		if(!e.getBlock().getWorld().equals(world))
		{
			return;
		}

		modified();
	}

	@EventHandler
	public void on(BlockPlaceEvent e)
	{
		if(!e.getBlock().getWorld().equals(world))
		{
			return;
		}

		modified();
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

			modified();
		}
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
		return getUsedEntityVolts() + getUsedTileVolts();
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

	public void tick()
	{
		if(world.getPlayers().isEmpty())
		{
			unload();
		}

		else
		{
			long timeAlive = M.ms() - island.getStarted();
			double ib = island.getValue() / 27.7D;
			double bonus = (((double) Math.pow((double) timeAlive, 0.65)) / (10000D)) + ib;
			world.getWorldBorder().setSize(Math.min(27 + bonus, SkyMaster.maxSize));

			if(M.ms() - island.getLastValueCalculation() > TimeUnit.SECONDS.toMillis(37) && M.r(0.25) && island.isNeedsRescan())
			{
				island.setLastValueCalculation(M.ms());
				island.setNeedsRescan(false);
				calculateValue();
			}

			updateVoltages();
		}
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
		p.teleport(world.getSpawnLocation());
	}

	public void setSpawn(Player p)
	{
		world.setSpawnLocation(p.getLocation());
	}

	public void saveConfig(VolumeSender s)
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

		if(is.getcHopperAmount() > 32)
		{
			is.setcHopperAmount(32);
			s.sendMessage("hopper.amount reduced to 32");
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

		if(is.getcHopperRate() < 1)
		{
			is.setcHopperRate(1);
			s.sendMessage("hopper.rate increased to 1");
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
	}

	public void unload()
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
	}

	private void updateVoltages()
	{
		double totalAllowedVoltage = Voltage.getTotalIslandVoltage(island.getValue());
		double voltageForEntities = island.getPercentEntities() * totalAllowedVoltage;
		double voltageForTiles = island.getPercentTiles() * totalAllowedVoltage;
		eTick.setrMaxTime(Math.max(0.01, Voltage.getMilliseconds(voltageForEntities)));
		tTick.setrMaxTime(Math.max(0.01, Voltage.getMilliseconds(voltageForTiles)));
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
		FinalDouble d = new FinalDouble(0);
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
				}
			};
		}

		new S((m / 8) + 1)
		{
			@Override
			public void run()
			{
				pb.end();
				new A()
				{
					@SuppressWarnings("deprecation")
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
				};
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
		double totalAllowedVoltage = Voltage.getTotalIslandVoltage(island.getValue());
		return island.getPercentEntities() * totalAllowedVoltage;
	}

	public double getAllowedTileVoltage()
	{
		double totalAllowedVoltage = Voltage.getTotalIslandVoltage(island.getValue());
		return island.getPercentTiles() * totalAllowedVoltage;
	}

	public void inject()
	{
		try
		{
			tweakIsland(island, world);
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
		Bukkit.getPluginManager().registerEvents(this, SkyPrime.vpi);
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
		HandlerList.unregisterAll(this);
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
