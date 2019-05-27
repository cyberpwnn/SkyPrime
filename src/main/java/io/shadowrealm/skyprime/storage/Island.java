package com.volmit.skyprime.storage;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;

import com.volmit.skyprime.Config;

import mortar.compute.math.M;
import mortar.lang.collection.GList;
import mortar.lang.json.JSONArray;
import mortar.lang.json.JSONObject;

public class Island
{
	private UUID owner;
	private UUID id;
	private long started;
	private double value;
	private double level;
	private double minsize;
	private double weightEntities;
	private double weightTiles;
	private boolean needsRescan;
	private long lastValueCalculation;
	private int cDespawnArrow;
	private int cDespawnItem;
	private double cMergeItem;
	private boolean cPublicPickup;
	private double cMergeXp;
	private int cHopperRate;
	private int cHopperAmount;
	private long lastSave;
	private Visibility visibility;
	private double warpx;
	private double warpy;
	private double warpz;
	private double warpyy;
	private double warppp;
	private double spawnx;
	private double spawny;
	private double spawnz;
	private double spawnyy;
	private double spawnpp;
	private int maxSize;
	private boolean competitive;
	private GList<UUID> admins;
	private GList<UUID> members;

	public Island(UUID id, UUID owner)
	{
		spawny = -10;
		warpy = -10;
		competitive = false;
		cDespawnArrow = 20;
		cDespawnItem = 1200;
		cMergeItem = 1.5;
		cMergeXp = 2.5;
		minsize = 3D;
		cHopperAmount = 16;
		visibility = Visibility.PRIVATE;
		cHopperRate = 5;
		this.owner = owner;
		this.id = id;
		started = M.ms();
		cPublicPickup = false;
		value = 0D;
		weightEntities = 55;
		weightTiles = 45;
		needsRescan = true;
		lastValueCalculation = M.ms();
		lastSave = M.ms();
		maxSize = Config.SIZE_DEFAULT_BARRIER;
		admins = new GList<>();
		members = new GList<>();
		level = 0D;
	}

	public Island(JSONObject o)
	{
		owner = UUID.fromString(o.getString("owner"));
		id = UUID.fromString(o.getString("id"));
		lastSave = o.has("last-save") ? o.getLong("last-save") : M.ms();
		lastValueCalculation = o.has("last-value") ? o.getLong("last-value") : M.ms();
		weightEntities = o.has("weight-entities") ? o.getDouble("weight-entities") : 70D;
		weightTiles = o.has("weight-tiles") ? o.getDouble("weight-tiles") : 30D;
		minsize = o.has("min-size") ? o.getDouble("min-size") : 3D;
		needsRescan = o.has("needs-rescan") ? o.getBoolean("needs-rescan") : true;
		value = o.getDouble("value");
		value = o.has("level") ? o.getDouble("level") : 0D;
		started = o.getLong("started");
		cHopperRate = o.has("config-hopper-rate") ? o.getInt("config-hopper-rate") : 5;
		cHopperAmount = o.has("config-hopper-amount") ? o.getInt("config-hopper-amount") : 4;
		cDespawnArrow = o.has("config-despawn-arrow") ? o.getInt("config-despawn-arrow") : 20;
		cDespawnItem = o.has("config-despawn-item") ? o.getInt("config-despawn-item") : 1200;
		cMergeItem = o.has("config-merge-item") ? o.getInt("config-merge-item") : 1.5;
		cMergeXp = o.has("config-merge-xp") ? o.getInt("config-merge-xp") : 2.5;
		cPublicPickup = o.has("config-pickup") ? o.getBoolean("config-pickup") : false;
		competitive = o.has("competitive") ? o.getBoolean("competitive") : false;
		visibility = o.has("visibility") ? Visibility.values()[o.getInt("visibility")] : Visibility.PRIVATE;
		maxSize = o.has("maxsize") ? o.getInt("maxsize") : Config.SIZE_DEFAULT_BARRIER;
		spawnx = o.has("sx") ? o.getDouble("sx") : 0;
		spawny = o.has("sy") ? o.getDouble("sy") : -10;
		spawnz = o.has("sz") ? o.getDouble("sz") : 0;
		spawnyy = o.has("syy") ? o.getDouble("syy") : 0;
		spawnpp = o.has("spp") ? o.getDouble("spp") : 0;
		warpx = o.has("wx") ? o.getDouble("wx") : 0;
		warpy = o.has("wy") ? o.getDouble("wy") : -10;
		warpz = o.has("wz") ? o.getDouble("wz") : 0;
		warpyy = o.has("wyy") ? o.getDouble("wyy") : 0;
		warppp = o.has("wpp") ? o.getDouble("wpp") : 0;
		admins = o.has("admins") ? idf(o.getJSONArray("admins")) : new GList<>();
		members = o.has("members") ? idf(o.getJSONArray("members")) : new GList<>();
	}

	public JSONObject toJSON()
	{
		JSONObject j = new JSONObject();

		j.put("id", getId().toString());
		j.put("owner", getOwner().toString());
		j.put("level", level);
		j.put("value", value);
		j.put("started", started);
		j.put("needs-rescan", needsRescan);
		j.put("last-value", lastValueCalculation);
		j.put("weight-entities", weightEntities);
		j.put("weight-tiles", weightTiles);
		j.put("config-hopper-rate", cHopperRate);
		j.put("config-hopper-amount", cHopperAmount);
		j.put("config-despawn-arrow", cDespawnArrow);
		j.put("config-despawn-item", cDespawnItem);
		j.put("config-merge-item", cMergeItem);
		j.put("config-merge-xp", cMergeXp);
		j.put("config-pickup", cPublicPickup);
		j.put("last-save", lastSave);
		j.put("visibility", visibility.ordinal());
		j.put("maxsize", maxSize);
		j.put("sx", spawnx);
		j.put("sy", spawny);
		j.put("sz", spawnz);
		j.put("syy", spawnyy);
		j.put("spp", spawnpp);
		j.put("wx", warpx);
		j.put("wy", warpy);
		j.put("wz", warpz);
		j.put("wyy", warpyy);
		j.put("wpp", warppp);
		j.put("members", idx(members));
		j.put("admins", idx(admins));
		j.put("competitive", competitive);
		j.put("min-size", minsize);

		return j;
	}

	public double getMinsize()
	{
		return minsize;
	}

	public boolean isCompetitive()
	{
		return competitive;
	}

	public void setCompetitive(boolean competitive)
	{
		this.competitive = competitive;
	}

	public void setAdmins(GList<UUID> admins)
	{
		this.admins = admins;
	}

	public void setMembers(GList<UUID> members)
	{
		this.members = members;
	}

	public GList<UUID> getAdmins()
	{
		return admins;
	}

	public GList<UUID> getMembers()
	{
		return members;
	}

	public JSONArray idx(GList<UUID> g)
	{
		JSONArray a = new JSONArray();

		for(UUID i : g)
		{
			a.put(i.toString());
		}

		return a;
	}

	public GList<UUID> idf(JSONArray a)
	{
		GList<UUID> v = new GList<>();

		for(String i : GList.from(a))
		{
			v.add(UUID.fromString(i));
		}

		return v;
	}

	public double getWarpx()
	{
		return warpx;
	}

	public void setWarpx(double warpx)
	{
		this.warpx = warpx;
	}

	public double getWarpy()
	{
		return warpy;
	}

	public void setWarpy(double warpy)
	{
		this.warpy = warpy;
	}

	public double getWarpz()
	{
		return warpz;
	}

	public void setWarpz(double warpz)
	{
		this.warpz = warpz;
	}

	public double getWarpyy()
	{
		return warpyy;
	}

	public void setWarpyy(double warpyy)
	{
		this.warpyy = warpyy;
	}

	public double getWarppp()
	{
		return warppp;
	}

	public void setWarppp(double warppp)
	{
		this.warppp = warppp;
	}

	public double getSpawnx()
	{
		return spawnx;
	}

	public void setSpawnx(double spawnx)
	{
		this.spawnx = spawnx;
	}

	public double getSpawny()
	{
		return spawny;
	}

	public void setSpawny(double spawny)
	{
		this.spawny = spawny;
	}

	public double getSpawnz()
	{
		return spawnz;
	}

	public void setSpawnz(double spawnz)
	{
		this.spawnz = spawnz;
	}

	public double getSpawnyy()
	{
		return spawnyy;
	}

	public void setSpawnyy(double spawnyy)
	{
		this.spawnyy = spawnyy;
	}

	public double getSpawnpp()
	{
		return spawnpp;
	}

	public void setSpawnpp(double spawnpp)
	{
		this.spawnpp = spawnpp;
	}

	public int getMaxSize()
	{
		return Math.max(Config.SIZE_DEFAULT_BARRIER, maxSize);
	}

	public void setMaxSize(int maxSize)
	{
		this.maxSize = maxSize;
	}

	public Location getWarp(World w)
	{
		if(warpy <= 0)
		{
			return w.getSpawnLocation();
		}

		return new Location(w, warpx, warpy, warpz, (float) warpyy, (float) warppp);
	}

	public Location getSpawn(World w)
	{
		if(spawny <= 0)
		{
			return w.getSpawnLocation();
		}

		return new Location(w, spawnx, spawny, spawnz, (float) spawnyy, (float) spawnpp);
	}

	public void setSpawn(Location l)
	{
		spawnx = l.getX();
		spawny = l.getY();
		spawnz = l.getZ();
		spawnyy = l.getYaw();
		spawnpp = l.getPitch();
	}

	public void setWarp(Location l)
	{
		warpx = l.getX();
		warpy = l.getY();
		warpz = l.getZ();
		warpyy = l.getYaw();
		warppp = l.getPitch();
	}

	public Visibility getVisibility()
	{
		return visibility;
	}

	public void setVisibility(Visibility visibility)
	{
		this.visibility = visibility;
	}

	public long getLastValueCalculation()
	{
		return lastValueCalculation;
	}

	public boolean isNeedsRescan()
	{
		return needsRescan;
	}

	public void setNeedsRescan(boolean needsRescan)
	{
		this.needsRescan = needsRescan;
	}

	public void setLastValueCalculation(long lastValueCalculation)
	{
		this.lastValueCalculation = lastValueCalculation;
	}

	public long getStarted()
	{
		return started;
	}

	public void setStarted(long started)
	{
		this.started = started;
	}

	public double getValue()
	{
		return value;
	}

	public void setValue(double value)
	{
		this.value = value;
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

	public double getPercentEntities()
	{
		return getWeightEntities() / (getWeightEntities() + getWeightTiles());
	}

	public double getPercentTiles()
	{
		return getWeightTiles() / (getWeightEntities() + getWeightTiles());
	}

	public double getWeightEntities()
	{
		return weightEntities;
	}

	public void setWeightEntities(double weightEntities)
	{
		this.weightEntities = weightEntities;
	}

	public double getWeightTiles()
	{
		return weightTiles;
	}

	public void setWeightTiles(double weightTiles)
	{
		this.weightTiles = weightTiles;
	}

	public long getLastSave()
	{
		return lastSave;
	}

	public void setLastSave(long lastSave)
	{
		this.lastSave = lastSave;
	}

	public int getcDespawnArrow()
	{
		return cDespawnArrow;
	}

	public void setcDespawnArrow(int cDespawnArrow)
	{
		this.cDespawnArrow = cDespawnArrow;
	}

	public int getcDespawnItem()
	{
		return cDespawnItem;
	}

	public void setcDespawnItem(int cDespawnItem)
	{
		this.cDespawnItem = cDespawnItem;
	}

	public double getcMergeItem()
	{
		return cMergeItem;
	}

	public void setcPublicPickup(boolean cPublicPickup)
	{
		this.cPublicPickup = cPublicPickup;
	}

	public void setcMergeItem(double cMergeItem)
	{
		this.cMergeItem = cMergeItem;
	}

	public double getcMergeXp()
	{
		return cMergeXp;
	}

	public void setcMergeXp(double cMergeXp)
	{
		this.cMergeXp = cMergeXp;
	}

	public int getcHopperRate()
	{
		return cHopperRate;
	}

	public void setcHopperRate(int cHopperRate)
	{
		this.cHopperRate = cHopperRate;
	}

	public boolean iscPublicPickup()
	{
		return cPublicPickup;
	}

	public int getcHopperAmount()
	{
		return cHopperAmount;
	}

	public void setcHopperAmount(int cHopperAmount)
	{
		this.cHopperAmount = cHopperAmount;
	}

	public double getLevel()
	{
		return level;
	}

	public void setLevel(double level)
	{
		this.level = level;
	}

	public void setMinsize(double minsize)
	{
		this.minsize = minsize;
	}
}
