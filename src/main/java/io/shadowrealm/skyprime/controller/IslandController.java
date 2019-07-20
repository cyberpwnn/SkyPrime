package io.shadowrealm.skyprime.controller;

import mortar.bukkit.plugin.Controller;
import mortar.lang.collection.GList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

public class IslandController extends Controller implements Listener
{

	private GList<Player> doRespawn = new GList<>();

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

	public boolean shouldRespawn(Player player)
	{
		System.out.println(player.getLocation());
		final Location l = player.getLocation();
		return l == null || l.getWorld() == null;
	}

	private Location getSpawnLocation()
	{
		return Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
	}

}
