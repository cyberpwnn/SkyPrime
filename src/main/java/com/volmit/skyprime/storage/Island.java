package com.volmit.skyprime.storage;

import java.util.UUID;

import com.volmit.volume.lang.json.JSONObject;

public class Island
{
	private UUID owner;
	private UUID id;
	
	public Island(JSONObject o)
	{
		owner = UUID.fromString(o.getString("owner"));
		id = UUID.fromString(o.getString("id"));
	}
	
	public Island(UUID id, UUID owner)
	{
		this.owner = owner;
		this.id = id;
	}

	public UUID getOwner()
	{
		return owner;
	}

	public void setOwner(UUID owner)
	{
		this.owner = owner;
	}

	public UUID getId()
	{
		return id;
	}

	public void setId(UUID id)
	{
		this.id = id;
	}
	
	public JSONObject toJSON()
	{
		JSONObject j = new JSONObject();
		
		j.put("id", getId().toString());
		j.put("owner", getOwner().toString());
		
		return j;
	}
}
