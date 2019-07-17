package io.shadowrealm.skyprime.controller;

import io.shadowrealm.skyprime.SkyMaster;
import io.shadowrealm.skyprime.VirtualIsland;
import io.shadowrealm.skyprime.dependencies.PlaceholderAPI;
import mortar.bukkit.plugin.Controller;
import mortar.lang.collection.GList;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class IslandChatController extends Controller
{
	private GList<OfflinePlayer> subscribers;

	public void subscribe(Player player)
	{
		this.subscribers.add(player);
	}

	public void unsubscribe(OfflinePlayer player)
	{
		this.subscribers.remove(player);
	}

	public boolean isSubscribed(Player player)
	{
		return this.subscribers.contains(player);
	}

	@Override
	public void start()
	{
		subscribers = new GList();
	}

	@Override
	public void stop()
	{
		subscribers.clear();
	}

	@Override
	public void tick()
	{
		for (OfflinePlayer p : subscribers.copy()) {
			if (!p.isOnline()) this.unsubscribe(p);
		}
	}

	public boolean doChat(Player player, String message)
	{
		final VirtualIsland vi = SkyMaster.getPlayerActiveIsland(player);
		if (null == vi) return false;
		message = ChatColor.GOLD + "" + player.getDisplayName() + "" + ChatColor.AQUA + " \u00BB " + ChatColor.YELLOW + "" + message;
		vi.sendMessage(message);
		return true;
	}
}
