package com.volmit.skyprime.storage;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import com.volmit.volume.lang.io.VIO;
import com.volmit.volume.lang.json.JSONException;
import com.volmit.volume.lang.json.JSONObject;

public class FileStorageEngine implements StorageEngine
{
	private File base;
	
	public FileStorageEngine(File base)
	{
		this.base = base;
		base.mkdirs();
	}
	
	@Override
	public Island getPersonalIsland(UUID player)
	{
		if(hasPersonalIsland(player))
		{
			try
			{
				return new Island(new JSONObject(VIO.readAll(new File(base, player + ".json"))));
			}
			
			catch(JSONException | IOException e)
			{
				e.printStackTrace();
			}
		}
		
		return null;
	}

	@Override
	public boolean hasPersonalIsland(UUID player)
	{
		return new File(base, player + ".json").exists();
	}

	@Override
	public void setPersonalIsland(UUID player, Island island)
	{
		try
		{
			VIO.writeAll(new File(base, player + ".json"), island.toJSON().toString(4));
		}
		
		catch(JSONException | IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void removePersonalIsland(UUID player)
	{
		new File(base, player + ".json").delete();
	}
}
