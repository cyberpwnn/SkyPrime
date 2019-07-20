package io.shadowrealm.skyprime.storage;

import java.util.UUID;

public interface StorageEngine
{
	public Island getIslandByOwner(UUID player);

	public Island getIslandById(UUID id);

	public UUID getIslandIdByOwner(UUID player);

	public boolean hasIslandByOwner(UUID player);

	public boolean hasIslandById(UUID id);

	public void setIsland(Island island);

	public void removeIsland(Island island);

	public void deleteOwnerIsland(UUID uuid);
}
