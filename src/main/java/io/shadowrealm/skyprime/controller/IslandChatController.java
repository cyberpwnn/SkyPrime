package io.shadowrealm.skyprime.controller;

import io.shadowrealm.skyprime.Config;
import io.shadowrealm.skyprime.SkyMaster;
import io.shadowrealm.skyprime.VirtualIsland;
import me.clip.placeholderapi.PlaceholderAPI;
import mortar.bukkit.plugin.Controller;
import mortar.lang.collection.GList;
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
		return Config.CHAT_ENABLE && this.subscribers.contains(player);
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
		if (!Config.CHAT_ENABLE) return false;

		final VirtualIsland vi = SkyMaster.getPlayerActiveIsland(player);
		if (null == vi) return false;
		vi.sendMessage(player, PlaceholderAPI.setPlaceholders(player, Config.CHAT_FORMAT_PREFIX) + message);
		return true;
	}
}
