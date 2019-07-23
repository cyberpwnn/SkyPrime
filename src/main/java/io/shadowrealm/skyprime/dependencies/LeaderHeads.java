package io.shadowrealm.skyprime.dependencies;

import io.shadowrealm.skyprime.SkyMaster;
import io.shadowrealm.skyprime.SkyPrime;
import lombok.AccessLevel;
import lombok.Getter;
import me.robin.leaderheads.datacollectors.OnlineDataCollector;
import me.robin.leaderheads.objects.BoardType;
import mortar.lang.collection.GList;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class LeaderHeads
{

	@Getter(AccessLevel.PROTECTED)
	private SkyPrime ploog;

	private GList<OnlineDataCollector> aids = new GList<>();

	public LeaderHeads(SkyPrime ploog)
	{
		this.ploog = ploog;
		this.aids.add(new TopValue(this));
		this.aids.add(new WorldSize(this));
	}

	private static class TopValue extends OnlineDataCollector
	{

		public TopValue(LeaderHeads lh)
		{
			super(
				"skyprime-value",
				lh.getPloog().getDescription().getName(),
				BoardType.DEFAULT,
				"&bTop Island Value",
				"openmenu",
				Arrays.asList(null, null, "&evalue: {amount}", null)
			);
		}

		@Override
		public Double getScore(Player p)
		{
			return SkyMaster.hasIsland(p) ? SkyMaster.getIslandConfig(p).getValue() : null;
		}
	}

	private static class WorldSize extends OnlineDataCollector
	{

		public WorldSize(LeaderHeads lh)
		{
			super(
					"skyprime-size",
					lh.getPloog().getDescription().getName(),
					BoardType.DEFAULT,
					"&bTop Island Size",
					"openmenu",
					Arrays.asList(null, null, "&e{amount} blocks", null)
			);
		}

		@Override
		public Double getScore(Player p)
		{
			return SkyMaster.hasIsland(p) ? SkyMaster.getIslandConfig(p).getWorldSize() : null;
		}
	}

}
