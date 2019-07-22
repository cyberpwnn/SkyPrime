package io.shadowrealm.skyprime.events;

import io.shadowrealm.skyprime.VirtualIsland;
import io.shadowrealm.skyprime.storage.Island;
import org.bukkit.event.HandlerList;

public class IslandUnloadEvent extends IslandEvent
{
	public static HandlerList l = new HandlerList();

	public IslandUnloadEvent(VirtualIsland vi, Island is)
	{
		super(vi, is);
	}

	@Override
	public HandlerList getHandlers()
	{
		return l;
	}

	public static HandlerList getHandlerList()
	{
		return l;
	}
}
