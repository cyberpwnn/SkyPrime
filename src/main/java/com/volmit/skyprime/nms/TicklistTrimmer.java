package com.volmit.skyprime.nms;

import java.util.Iterator;

import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.util.HashTreeSet;

import com.volmit.phantom.lang.GList;
import com.volmit.phantom.lang.GMap;
import com.volmit.phantom.lang.V;
import com.volmit.phantom.math.M;
import com.volmit.skyprime.Config;
import com.volmit.skyprime.VirtualIsland;

import net.minecraft.server.v1_12_R1.NextTickListEntry;
import net.minecraft.server.v1_12_R1.WorldServer;

public class TicklistTrimmer
{
	private World world;
	private int delay;
	private int reschedule;
	private VirtualIsland v;
	private GMap<NextTickListEntry, Integer> delayedEntries;
	private GList<NextTickListEntry> delayedOrder;
	private HashTreeSet<NextTickListEntry> t;

	public TicklistTrimmer(VirtualIsland v)
	{
		this.world = v.getWorld();
		this.setV(v);
		delay = 0;
		reschedule = 0;
		delayedEntries = new GMap<>();
		delayedOrder = new GList<>();
		t = getTickList();
	}

	public int getDelay()
	{
		return delay;
	}

	public void setDelay(int delay)
	{
		this.delay = (int) M.clip(delay, 0, Config.PHYSICS_THROTTLE);
	}

	public void dumpTicklist()
	{
		GMap<Integer, GList<NextTickListEntry>> v = delayedEntries.flip();
		delayedEntries.clear();
		delayedOrder.clear();

		for(GList<NextTickListEntry> i : v.sortV().reverse())
		{
			t.addAll(i);
		}
	}

	public void tick()
	{
		if(delay > 0 && reschedule <= 0)
		{
			Iterator<NextTickListEntry> it = t.iterator();
			int c = 0;

			while(it.hasNext())
			{
				NextTickListEntry e = it.next();
				it.remove();
				int withold = delay;

				if(Config.PHYSICS_SIDELOAD && delay > 3)
				{
					withold = delay + (c / Config.PHYSICS_SIDELOAD_THRESHOLD);
				}

				delayedEntries.put(e, withold);
				delayedOrder.add(e);
				c++;
			}
		}

		if(delayedEntries.size() > 0)
		{
			for(NextTickListEntry i : delayedOrder.copy())
			{
				try
				{
					if(delayedEntries.get(i) <= 0)
					{
						t.add(i);
						delayedEntries.remove(i);
						delayedOrder.remove(i);
					}

					else
					{
						delayedEntries.put(i, delayedEntries.get(i) - 1);
					}
				}

				catch(Throwable e)
				{
					delayedEntries.remove(i);
					delayedOrder.remove(i);
				}
			}
		}

		reschedule--;
	}

	public HashTreeSet<NextTickListEntry> getTickList()
	{
		CraftWorld w = (CraftWorld) world;
		WorldServer ws = new V(w).get("world");
		HashTreeSet<NextTickListEntry> set = new V(ws).get("nextTickList");

		return set;
	}

	public VirtualIsland getV()
	{
		return v;
	}

	public void setV(VirtualIsland v)
	{
		this.v = v;
	}
}
