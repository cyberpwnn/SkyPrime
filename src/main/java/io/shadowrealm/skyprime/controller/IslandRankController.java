package io.shadowrealm.skyprime.controller;

import io.shadowrealm.skyprime.storage.Island;
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
	private boolean locked = false;

	private File getCache()
	{
		return new File(this.storage, "cache.json");
	}

	@Getter
	private int highestRank = 0;

	@Getter
	private GMap<Integer, IslandDetails> islandRanks = new GMap<>();

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
		if (this.lastCalculation != 0 && (this.lastCalculation + this.interval) > System.currentTimeMillis() || this.isLocked())
			return;
		J.a(() -> recalculateRanks());
	}

	protected void loadFromCache(File f)
	{
		if (!f.exists()) return;

		try {
			final JSONObject o = new JSONObject(VIO.readAll(f));
			this.islandRanks = (GMap<Integer, IslandDetails>) o.get("data");
			this.highestRank = o.getInt("highestRank");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void saveToFile(File f)
	{
		try {
			GMap<String, Object> g = new GMap<>();
			for (Map.Entry<Integer, IslandDetails> l : this.islandRanks.entrySet()) {
				g.put(l.getKey().toString(), l.getValue());
			}

			final JSONObject o = new JSONObject();
			o.put("calculated", this.lastCalculation);
			o.put("highestRank", this.highestRank);
			o.put("data", g);

			VIO.writeAll(f, o);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public int getIslandRank(Island island)
	{
		for (IslandDetails id : islandRanks.values()) {
			if (id.getUUID().equals(island.getId())) return id.getRank();
		}
		this.highestRank++;
		this.islandRanks.put(this.highestRank, new IslandDetails(island, this.highestRank));
		return this.highestRank;
	}

	public void createIslandRank(Island island)
	{
		this.highestRank++;
		this.islandRanks.put(this.highestRank, new IslandDetails(island, this.highestRank));
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
		this.locked = true;

		final GMap<Integer, IslandDetails> r = new GMap<>();
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
			final IslandDetails id = new IslandDetails(o.getKey(), o.getValue().getString("name"), o.getValue().getInt("_rankIndex"));
			r.put(id.getRank(), id);
		}

		this.islandRanks = r;
		this.highestRank = i[0];
		this.lastCalculation = System.currentTimeMillis();
		this.saveToFile(this.getCache());
		this.locked = false;
	}

	public static class IslandDetails extends GMap<String, Object>
	{

		public IslandDetails(Island i, int rank)
		{
			this(i.getId(), i.getName(), rank);
		}

		public IslandDetails(UUID uuid, String name, int rank)
		{
			this.put("uuid", uuid);
			this.put("name", name);
			this.put("rank", rank);
		}

		@Override
		public String toString()
		{
			return "{name=" + this.getName() + ", UUID=" + this.getUUID() + ", rank=" + this.getRank() + "}";
		}

		public UUID getUUID()
		{
			return (UUID) this.get("uuid");
		}

		public String getName()
		{
			return this.get("name").toString();
		}

		public int getRank()
		{
			return (int) this.get("rank");
		}
	}
}
