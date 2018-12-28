package com.volmit.skyprime;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.volmit.volume.lang.collections.GList;

public class Config
{
	@Key("virtual-islands.voltage.max-server-time")
	public static double VOLTAGE_MAX_MILLISECONDS = 45D;

	@Key("virtual-islands.size.default")
	public static int SIZE_DEFAULT = 80;

	@Key("virtual-islands.size.value-propagation.exponent")
	public static double FRACTAL_VALUE = 0.65;

	@Key("virtual-islands.size.value-propagation.divisor")
	public static double DIVISOR_VALUE = 3.5D;

	@Key("virtual-islands.size.animation-time")
	public static int ANIMATION_SIZE = 6;

	@Key("virtual-islands.size.ranks")
	public static GList<String> SIZE_RANKS = new GList<String>().qadd("a=128").qadd("b=192").qadd("c=256").qadd("d=320").qadd("e=384");

	@Key("virtual-islands.sequences.shutdown.idle-threshold-ticks")
	public static int IDLE_TICKS = 600;

	@Key("virtual-islands.sequences.startup.spinup-threshold-ticks")
	public static int SPINUP_TIME = 70;

	@Key("virtual-islands.sequences.startup.voltage-draw")
	public static double STARTUP_VOLTAGE = 250D;

	@Key("virtual-islands.engine.max-physics-throttle")
	public static int PHYSICS_THROTTLE = 50;

	@Key("virtual-islands.engine.sideload-mode")
	public static boolean PHYSICS_SIDELOAD = true;

	@Key("virtual-islands.engine.sideload-entry-treshold")
	public static int PHYSICS_SIDELOAD_THRESHOLD = 15;

	@Key("virtual-islands.config.hopper.max-amount")
	public static int HOPPER_MAX_AMT = 16;

	@Key("virtual-islands.config.hopper.min-interval")
	public static int HOPPER_MIN_INTERVAL = 5;

	@Key("virtual-islands.engine.max-physics-gear-ratio")
	public static double PHYSICS_GEAR_RATIO = 0.75;

	@Key("virtual-islands.voltage.island.max-time")
	public static double VOLTAGE_MAX_MILLISECONDS_PER_ISLAND = 10D;

	@Key("virtual-islands.voltage.island.max-base-voltage")
	public static double VOLTAGE_BASE_VOLTAGE_MAX = 50D;

	@Key("virtual-islands.voltage.island.min-base-voltage")
	public static double VOLTAGE_BASE_VOLTAGE_MIN = 10D;

	@Key("virtual-islands.voltage.volts-per-millisecond")
	public static double VOLTAGE_VOLTS_PER_MILLISECOND = 50D;

	@Key("virtual-islands.voltage.island.base-value-threshold")
	public static double VOLTAGE_MAXOUT_VALUE = 20000D;

	public static void save() throws IllegalArgumentException, IllegalAccessException, IOException
	{
		peel().save(SkyPrime.vpi.getDataFile("config.yml"));
	}

	public static void load() throws IllegalArgumentException, IllegalAccessException, IOException
	{
		FileConfiguration fc = peel();
		FileConfiguration dc = new YamlConfiguration();

		try
		{
			dc.load(SkyPrime.vpi.getDataFile("config.yml"));
		}

		catch(Throwable e)
		{

		}

		for(String i : dc.getKeys(true))
		{
			fc.set(i, dc.get(i));
		}

		stick(fc);
		save();
	}

	public static FileConfiguration peel() throws IllegalArgumentException, IllegalAccessException
	{
		FileConfiguration fc = new YamlConfiguration();

		for(Field i : Config.class.getDeclaredFields())
		{
			Key k = i.getAnnotation(Key.class);

			if(k == null)
			{
				continue;
			}

			Object v = i.get(null);
			fc.set(k.value(), v);
		}

		return fc;
	}

	public static void stick(FileConfiguration fc) throws IllegalArgumentException, IllegalAccessException
	{
		for(Field i : Config.class.getDeclaredFields())
		{
			Key k = i.getAnnotation(Key.class);

			if(k == null)
			{
				continue;
			}

			Object v = fc.get(k.value());

			if(v instanceof List<?>)
			{
				GList<String> z = new GList<String>();

				for(Object j : (List<?>) v)
				{
					z.add(j.toString());
				}

				i.set(null, z);
			}

			else
			{
				i.set(null, v);
			}
		}
	}
}
