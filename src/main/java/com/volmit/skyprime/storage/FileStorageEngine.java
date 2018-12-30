package com.volmit.skyprime.storage;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import com.volmit.phantom.json.JSONException;
import com.volmit.phantom.json.JSONObject;
import com.volmit.phantom.lang.VIO;

public class FileStorageEngine implements StorageEngine
{
	private File base;

	public FileStorageEngine(File base)
	{
		this.base = base;
		base.mkdirs();
	}

	@Override
	public Island getIslandByOwner(UUID player)
	{
		return getIslandById(getIslandIdByOwner(player));
	}

	@Override
	public Island getIslandById(UUID id)
	{
		try
		{
			return new Island(new JSONObject(VIO.readAll(new File(base, "data-" + id.toString()))));
		}

		catch(JSONException | IOException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public boolean hasIslandByOwner(UUID player)
	{
		return new File(base, "owner-" + player.toString()).exists();
	}

	@Override
	public boolean hasIslandById(UUID id)
	{
		return new File(base, "data-" + id.toString()).exists();
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
		new File(base, "data-" + island.getId().toString()).delete();
		new File(base, "owner-" + island.getOwner().toString()).delete();
	}

	@Override
	public UUID getIslandIdByOwner(UUID player)
	{
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
