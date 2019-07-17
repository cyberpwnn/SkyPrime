package io.shadowrealm.skyprime;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import mortar.lang.collection.GMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import mortar.lang.collection.GList;

public class Config
{
	@Key("virtual-islands.voltage.max-server-time")
	public static double VOLTAGE_MAX_MILLISECONDS = 45D;

	@Key("virtual-islands.size.default")
	public static int SIZE_DEFAULT_BARRIER = 80;

	@Key("virtual-islands.size.value-propagation.exponent")
	public static double FRACTAL_VALUE = 0.65;

	@Key("virtual-islands.size.value-propagation.divisor")
	public static double DIVISOR_VALUE = 3.5D;

	@Key("virtual-islands.size.animation-time")
	public static int ANIMATION_SIZE = 1;

	@Key("virtual-islands.size.ranks")
	public static GList<String> SIZE_RANKS = new GList<String>().qadd("a=128").qadd("b=192").qadd("c=256").qadd("d=320").qadd("e=384");

	@Key("virtual-islands.sequences.shutdown.idle-threshold-ticks")
	public static int IDLE_TICKS = 600;

	@Key("virtual-islands.sequences.startup.spinup-threshold-ticks")
	public static int SPINUP_TIME = 5;

	@Key("virtual-islands.generation.island-size-default")
	public static int ISLAND_SIZE = 8;

	@Key("virtual-islands.generation.island-size-small")
	public static int ISLAND_SIZE_SMALL = 5;

	@Key("virtual-islands.generation.island-size-big")
	public static int ISLAND_SIZE_BIG = 24;

	@Key("competitive.generation.island-size")
	public static int ISLAND_COMP_SIZE = 3;

	@Key("competitive.seasons.season-length-days")
	public static int SEASON_DAYS = 60;

	@Key("competitive.seasons.season-first-day")
	public static int SEASON_DAYS_EPOCH = 17897;

	@Key("competitive.voltage.overvoltage")
	public static double COMP_OVERVOLT = 15D;

	@Key("competitive.voltage.bonus-voltage-cap-boost")
	public static double BONUS_CAP_BOOST = 0.5D;

	@Key("competitive.seasons.barriers.start-size")
	public static double SEASON_BARRIER_START = 48;

	@Key("competitive.seasons.barriers.end-size")
	public static double SEASON_BARRIER_END = 256;

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
	public static double VOLTAGE_MAX_MILLISECONDS_PER_ISLAND = 1D;

	@Key("virtual-islands.voltage.island.max-base-voltage")
	public static double VOLTAGE_BASE_VOLTAGE_MAX = 30D;

	@Key("virtual-islands.voltage.island.min-base-voltage")
	public static double VOLTAGE_BASE_VOLTAGE_MIN = 10D;

	@Key("virtual-islands.voltage.volts-per-millisecond")
	public static double VOLTAGE_VOLTS_PER_MILLISECOND = 50D;

	@Key("virtual-islands.voltage.island.base-value-threshold")
	public static double VOLTAGE_MAXOUT_VALUE = 20000D;

	@Key("virtual-islands.chat.enable")
	public static boolean CHAT_ENABLE = true;

	@Key("virtual-islands.chat.prefix")
	public static String CHAT_FORMAT_PREFIX = "&6%skyprime_name_self% %player_displayname% &b\u00BB &e";

	@Key("virtual-islands.chat.same-world")
	public static boolean CHAT_SAME_WORLD = true;

	@Key("virtual-islands.max-members")
	@Comment("Allows a player a specific amount of players to their island using: skyprime.sky.members.<ID>")
	public static HashMap<String, Integer> MAX_MEMBERS = new HashMap() {{
		put("default", 3);
		put("donor", 5);
		put("staff", 5);
		put("admins", 15);
	}};

	public static void save() throws IllegalArgumentException, IllegalAccessException, IOException
	{
		peel().save(SkyPrime.instance.getDataFile("config.yml"));
	}

	public static void load() throws IllegalArgumentException, IllegalAccessException, IOException
	{
		FileConfiguration fc = peel();
		FileConfiguration dc = new YamlConfiguration();

		try
		{
			dc.load(SkyPrime.instance.getDataFile("config.yml"));
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
