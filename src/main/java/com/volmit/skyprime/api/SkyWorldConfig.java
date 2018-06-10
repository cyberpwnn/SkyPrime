package com.volmit.skyprime.api;

import java.io.File;

import com.volmit.volume.bukkit.pawn.Comment;
import com.volmit.volume.bukkit.pawn.IPawn;
import com.volmit.volume.bukkit.pawn.Node;

public class SkyWorldConfig implements IPawn
{
	@Node("skyworld.display-name")
	@Comment("The display name of this skyworld")
	public String displayName = "&bSky &7World";

	public void save(File file)
	{
		// TODO Auto-generated method stub

	}

	public void load(File file)
	{
		// TODO Auto-generated method stub

	}
}
