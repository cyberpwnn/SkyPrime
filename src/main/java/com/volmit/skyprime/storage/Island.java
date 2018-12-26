package com.volmit.skyprime.storage;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;

import com.volmit.skyprime.SkyMaster;
import com.volmit.volume.lang.json.JSONObject;
import com.volmit.volume.math.M;

public class Island
{
	private UUID owner;
	private UUID id;
	private long started;
	private double value;
	private double weightEntities;
	private double weightTiles;
	private boolean needsRescan;
	private long lastValueCalculation;
	private int cDespawnArrow;
	private int cDespawnItem;
	private double cMergeItem;
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

	public Island(UUID id, UUID owner)
	{
		cDespawnArrow = 20;
		cDespawnItem = 1200;
		cMergeItem = 1.5;
		cMergeXp = 2.5;
		cHopperAmount = 16;
		visibility = Visibility.PRIVATE;
		cHopperRate = 5;
		this.owner = owner;
		this.id = id;
		started = M.ms();
		value = 0D;
		weightEntities = 55;
		weightTiles = 45;
		needsRescan = true;
		lastValueCalculation = M.ms();
		lastSave = M.ms();
		maxSize = SkyMaster.maxSize;
	}

	public Island(JSONObject o)
	{
		owner = UUID.fromString(o.getString("owner"));
		id = UUID.fromString(o.getString("id"));
		lastSave = o.has("last-save") ? o.getLong("last-save") : M.ms();
		lastValueCalculation = o.has("last-value") ? o.getLong("last-value") : M.ms();
		weightEntities = o.has("weight-entities") ? o.getDouble("weight-entities") : 70D;
		weightTiles = o.has("weight-tiles") ? o.getDouble("weight-tiles") : 30D;
		needsRescan = o.has("needs-rescan") ? o.getBoolean("needs-rescan") : true;
		value = o.getDouble("value");
		started = o.getLong("started");
		cHopperRate = o.has("config-hopper-rate") ? o.getInt("config-hopper-rate") : 5;
		cHopperAmount = o.has("config-hopper-amount") ? o.getInt("config-hopper-amount") : 4;
		cDespawnArrow = o.has("config-despawn-arrow") ? o.getInt("config-despawn-arrow") : 20;
		cDespawnItem = o.has("config-despawn-item") ? o.getInt("config-despawn-item") : 1200;
		cMergeItem = o.has("config-merge-item") ? o.getInt("config-merge-item") : 1.5;
		cMergeXp = o.has("config-merge-xp") ? o.getInt("config-merge-xp") : 2.5;
		visibility = o.has("visibility") ? Visibility.values()[o.getInt("visibility")] : Visibility.PRIVATE;
		maxSize = o.has("maxsize") ? o.getInt("maxsize") : SkyMaster.maxSize;
	}

	public JSONObject toJSON()
	{
		JSONObject j = new JSONObject();

		j.put("id", getId().toString());
		j.put("owner", getOwner().toString());
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
		j.put("last-save", lastSave);
		j.put("visibility", visibility.ordinal());
		j.put("maxsize", maxSize);

		return j;
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
		return Math.max(SkyMaster.maxSize, maxSize);
	}

	public void setMaxSize(int maxSize)
	{
		this.maxSize = maxSize;
	}

	public Location getWarp(World w)
	{
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

	public int getcHopperAmount()
	{
		return cHopperAmount;
	}

	public void setcHopperAmount(int cHopperAmount)
	{
		this.cHopperAmount = cHopperAmount;
	}

}
