package com.volmit.skyprime.api;

import java.util.UUID;

import org.bukkit.entity.Player;

public class SkyPlayer
{
	private UUID id;

	public SkyPlayer(UUID id)
	{
		this.id = id;
	}

	public SkyPlayer(Player player)
	{
		this(player.getUniqueId());
	}

	public UUID getId()
	{
		return id;
	}
}
