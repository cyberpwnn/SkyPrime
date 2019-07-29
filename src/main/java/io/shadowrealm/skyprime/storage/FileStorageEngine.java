package io.shadowrealm.skyprime.storage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import mortar.lang.json.JSONException;
import mortar.lang.json.JSONObject;
import mortar.logic.io.VIO;

public class FileStorageEngine implements StorageEngine
{
	private HashMap<UUID, UUID> playerIslandCache = new HashMap<>();
	private HashMap<UUID, Island> islandCache = new HashMap<>();

	private File base;

	public FileStorageEngine(File base)
	{
		this.base = base;
		base.mkdirs();

		this.playerIslandCache.clear();
		this.islandCache.clear();
	}

	@Override
	public void cleanup()
	{
	}

	@Override
	public void shutdown()
	{
		this.playerIslandCache.clear();
		this.islandCache.clear();
	}

	@Override
	public Island getIslandByOwner(UUID player)
	{
		return getIslandById(getIslandIdByOwner(player));
	}

	@Override
	public Island getIslandById(UUID id)
	{
		if (islandCache.containsKey(id) && islandCache.get(id) != null) {
			return islandCache.get(id);
		}

		String s;

		try
		{
			s = VIO.readAll(new File(base, "data-" + id.toString()));
			System.out.println(s);
			final Island is = new Island(new JSONObject(s));
			islandCache.put(id, is);
			return is;
		}

		catch(JSONException | IOException e)
		{
			System.out.println("Cannot load island data: " + id.toString());
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public boolean hasIslandByOwner(UUID player)
	{
		return playerIslandCache.containsKey(player) || new File(base, "owner-" + player.toString()).exists();
	}

	@Override
	public boolean hasIslandById(UUID id)
	{
		return islandCache.containsKey(id) || new File(base, "data-" + id.toString()).exists();
	}

	@Override
	public void setIsland(Island island)
	{
		File fa = new File(base, "data-" + island.getId().toString());
		File fb = new File(base, "owner-" + island.getOwner().toString());

		try
		{
			fa.getParentFile().mkdirs();
			VIO.writeAll(fa, island.toJSON().toString(4));
			VIO.writeAll(fb, island.getId().toString());
		}

		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void removeIsland(Island island)
	{
		this.islandCache.remove(island.getId());
		new File(base, "data-" + island.getId().toString()).delete();
		deleteOwnerIsland(island.getOwner());
	}

	@Override
	public void deleteOwnerIsland(UUID uuid)
	{
		new File(base, "owner-" + uuid.toString()).delete();
		this.playerIslandCache.remove(uuid);
	}

	@Override
	public UUID getIslandIdByOwner(UUID player)
	{
		if (playerIslandCache.containsKey(player)) {
			return playerIslandCache.get(player);
		}

		try
		{
			return UUID.fromString(VIO.readAll(new File(base, "owner-" + player.toString())).trim());
		}

		catch(IOException e)
		{
			e.printStackTrace();
		}

		return null;
	}
}
