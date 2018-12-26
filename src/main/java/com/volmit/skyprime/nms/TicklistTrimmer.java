package com.volmit.skyprime.nms;

import java.util.Iterator;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.util.HashTreeSet;

import com.volmit.volume.lang.collections.GList;
import com.volmit.volume.lang.collections.GMap;
import com.volmit.volume.math.M;
import com.volmit.volume.reflect.V;

import net.minecraft.server.v1_12_R1.NextTickListEntry;
import net.minecraft.server.v1_12_R1.WorldServer;

public class TicklistTrimmer
{
	private World world;
	private int delay;
	private GMap<NextTickListEntry, Integer> delayedEntries;
	private HashTreeSet<NextTickListEntry> t;

	public TicklistTrimmer(World world)
	{
		this.world = world;
		delay = 0;
		delayedEntries = new GMap<>();
		t = getTickList();
	}

	public int getDelay()
	{
		return delay;
	}

	public void setDelay(int delay)
	{
		this.delay = (int) M.clip(delay, 0, 35);
	}

	public void dumpTicklist()
	{
		GMap<Integer, GList<NextTickListEntry>> v = delayedEntries.flip();
		delayedEntries.clear();

		for(GList<NextTickListEntry> i : v.sortV().reverse())
		{
			t.addAll(i);
		}
	}

	public void tick()
	{
		if(delay > 0)
		{
			Iterator<NextTickListEntry> it = t.iterator();

			while(it.hasNext())
			{
				NextTickListEntry e = it.next();
				it.remove();
				delayedEntries.put(e, delay);
			}
		}

		if(delayedEntries.size() > 0)
		{
			for(NextTickListEntry i : delayedEntries.k())
			{
				if(delayedEntries.get(i) <= 0)
				{
					t.add(i);
					delayedEntries.remove(i);
				}

				else
				{
					delayedEntries.put(i, delayedEntries.get(i) - 1);
				}
			}
		}
	}

	public HashTreeSet<NextTickListEntry> getTickList()
	{
		CraftWorld w = (CraftWorld) world;
		WorldServer ws = new V(w).get("world");
		HashTreeSet<NextTickListEntry> set = new V(ws).get("nextTickList");

		return set;
	}
}
