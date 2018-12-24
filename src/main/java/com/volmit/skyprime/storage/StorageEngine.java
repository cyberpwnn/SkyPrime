package com.volmit.skyprime.storage;

import java.util.UUID;

public interface StorageEngine
{
	public Island getPersonalIsland(UUID player);
	
	public boolean hasPersonalIsland(UUID player);
	
	public void setPersonalIsland(UUID player, Island island);
	
	public void removePersonalIsland(UUID player);
}
