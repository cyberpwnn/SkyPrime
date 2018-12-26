package com.volmit.skyprime;

public class Voltage
{
	public static double maxMilliseconds = 45D;
	public static double maxMillisecondsPerIsland = 1.5D;
	public static double voltsPerMillisecond = 50D;
	public static double baseVoltage = 50D;
	public static double maxValueVoltage = 20000D;
	public static double baseVoltageMin = 35D;

	public static double getIslandBaseVoltage(double value)
	{
		return baseVoltageMin + ((Math.min(value / 100D, maxValueVoltage) / maxValueVoltage) * (baseVoltage - baseVoltageMin));
	}

	public static double getIslandBaseVoltage()
	{
		return baseVoltage;
	}

	public static double getMilliseconds(double voltage)
	{
		return voltage / voltsPerMillisecond;
	}

	public static double getVolts(double ms)
	{
		return ms * voltsPerMillisecond;
	}

	public static double getIslandBonusVoltage()
	{
		return Math.max(0, (getVolts(Math.min(maxMillisecondsPerIsland, maxMilliseconds)) - (getIslandBaseVoltage() * (double) getTotalIslands())) / (double) getTotalIslands());
	}

	private static double getTotalIslands()
	{
		return SkyMaster.getIslandsCount();
	}

	public static double getTotalIslandVoltage(double value)
	{
		return getIslandBaseVoltage(value) + getIslandBonusVoltage();
	}

	public static double getTotalIslandVoltage()
	{
		return getIslandBaseVoltage() + getIslandBonusVoltage();
	}
}
