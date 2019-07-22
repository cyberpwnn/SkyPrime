package io.shadowrealm.skyprime.events;

import io.shadowrealm.skyprime.VirtualIsland;
import io.shadowrealm.skyprime.storage.Island;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class IslandEvent extends Event implements Cancellable
{
	public static HandlerList l = new HandlerList();

	@Getter
	@Setter
	private boolean cancelled = false;

	@Getter
	@Setter
	private VirtualIsland virtualIsland;

	@Getter
	@Setter
	private Island island;

	public IslandEvent(VirtualIsland vi, Island is)
	{
		setVirtualIsland(vi);
		setIsland(is);
	}

	@Override
	public HandlerList getHandlers()
	{
		return l;
	}
}
