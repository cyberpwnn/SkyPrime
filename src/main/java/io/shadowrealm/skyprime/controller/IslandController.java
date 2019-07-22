package io.shadowrealm.skyprime.controller;

import io.shadowrealm.skyprime.SkyMaster;
import io.shadowrealm.skyprime.VirtualIsland;
import io.shadowrealm.skyprime.events.IslandUnloadEvent;
import mortar.bukkit.plugin.Controller;
import mortar.lang.collection.GList;
import mortar.lang.collection.GMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.UUID;

public class IslandController extends Controller implements Listener
{

	private GList<Player> doRespawn = new GList<>();

	private GMap<UUID, UUID> islandPlayers = new GMap<>();

	@Override
	public void start()
	{
		doRespawn = new GList<>();
	}

	@Override
	public void stop()
	{
	}

	@Override
	public void tick()
	{
		if (doRespawn.isEmpty()) return;
		for (Player p : doRespawn) {
			p.teleport(getSpawnLocation());
		}
		doRespawn.clear();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		if (this.shouldRespawn(e.getPlayer())) {
			this.doRespawn.add(e.getPlayer());
		}
	}

	@EventHandler
	public void onPlayerSpawn(PlayerSpawnLocationEvent e)
	{
		if (this.shouldRespawn(e.getPlayer())) {
			this.doRespawn.add(e.getPlayer());
			e.setSpawnLocation(getSpawnLocation());
		}
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e)
	{
		final VirtualIsland vi = SkyMaster.getIsland(e.getPlayer().getWorld());
		if (null == vi) return;
		this.islandPlayers.put(vi.getIsland().getId(), e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onIslandUnload(IslandUnloadEvent e)
	{
	}

	public boolean shouldRespawn(Player player)
	{
		final Location l = player.getLocation();
		return l == null || l.getWorld() == null || islandPlayers.containsValue(player.getUniqueId());
	}

	private Location getSpawnLocation()
	{
		return Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
	}

}
