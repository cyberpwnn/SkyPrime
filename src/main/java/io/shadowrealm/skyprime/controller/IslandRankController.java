package io.shadowrealm.skyprime.controller;

import lombok.Getter;
import lombok.Setter;
import mortar.api.sched.J;
import mortar.bukkit.plugin.Controller;
import mortar.lang.collection.GMap;
import mortar.lang.json.JSONObject;
import mortar.logic.io.VIO;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class IslandRankController extends Controller
{
	@Getter
	@Setter
	private File storage;

	@Getter
	private int highestRank = 0;

	private GMap<UUID, Integer> islandRanks = new GMap<>();

	@Getter
	private Long lastCalculation = 0L;

	@Getter
	@Setter
	private Long interval = 1000L * 60 * 5;

	@Override
	public void start()
	{
		this.islandRanks.clear();
	}

	@Override
	public void stop()
	{
	}

	@Override
	public void tick()
	{
		if (this.lastCalculation != 0 && (this.lastCalculation + this.interval) > System.currentTimeMillis())
			return;
		J.a(() -> recalculateRanks());
	}

	public int getIslandRank(UUID uuid)
	{
		if (islandRanks.containsKey(uuid)) {
			return islandRanks.get(uuid);
		}
		this.highestRank++;
		this.islandRanks.put(uuid, this.highestRank);
		return this.highestRank;
	}

	public void putIslandRank(UUID uuid, int rank)
	{
		if (rank > this.highestRank) this.highestRank = rank;
		this.islandRanks.put(uuid, rank);
	}

	private GMap<UUID, JSONObject> loadIslandData()
	{
		final GMap<UUID, JSONObject> is = new GMap<>();

		for (File f : this.storage.listFiles()) {
			try {
				if (!(f.exists() && f.isFile() && f.getName().startsWith("data-"))) continue;

				JSONObject o = new JSONObject(VIO.readAll(f));
				o.put("_rankIndex", 0);
				is.put(UUID.fromString(o.getString("id")), o);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return is;
	}

	public void recalculateRanks()
	{
		System.out.println("Recalculating ranks");

		final GMap<UUID, Integer> r = new GMap<>();
		final GMap<UUID, JSONObject> is = this.loadIslandData();
		int[] i = {0};

		List<JSONObject> j = is.v();

		j.sort(
			(a, b) -> (int) Math.round(b.getDouble("value") - a.getDouble("value"))
		);
		for (JSONObject o : j) {
			++i[0];
			o.put("_rankIndex", i[0]);
		}

		for (Map.Entry<UUID, JSONObject> o : is.entrySet()) {
			r.put(o.getKey(), o.getValue().getInt("_rankIndex"));
		}

		this.islandRanks = r;
		this.highestRank = i[0];

		System.out.println(r);
	}
}
