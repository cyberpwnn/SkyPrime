package io.shadowrealm.skyprime;

public class Voltage
{
	public static double getIslandBaseVoltage(double value)
	{
		return Config.VOLTAGE_BASE_VOLTAGE_MIN + ((Math.min(value / 100D, Config.VOLTAGE_BASE_VOLTAGE_MAX) / Config.VOLTAGE_MAXOUT_VALUE) * (Config.VOLTAGE_BASE_VOLTAGE_MAX - Config.VOLTAGE_BASE_VOLTAGE_MIN));
	}

	public static double getIslandBaseVoltage()
	{
		return Config.VOLTAGE_BASE_VOLTAGE_MAX;
	}

	public static double getMilliseconds(double voltage)
	{
		return voltage / Config.VOLTAGE_VOLTS_PER_MILLISECOND;
	}

	public static double getVolts(double ms)
	{
		return ms * Config.VOLTAGE_VOLTS_PER_MILLISECOND;
	}

	public static double getIslandBonusVoltage()
	{
		return Math.max(0, (getVolts(Math.min(Config.VOLTAGE_MAX_MILLISECONDS_PER_ISLAND, Config.VOLTAGE_MAX_MILLISECONDS)) - (getIslandBaseVoltage() * (double) getTotalIslands())) / (double) getTotalIslands());
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
