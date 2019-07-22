package io.shadowrealm.skyprime.events;

import io.shadowrealm.skyprime.VirtualIsland;
import io.shadowrealm.skyprime.storage.Island;

public class IslandUnloadEvent extends IslandEvent
{
	public IslandUnloadEvent(VirtualIsland vi, Island is)
	{
		super(vi, is);
	}
}
