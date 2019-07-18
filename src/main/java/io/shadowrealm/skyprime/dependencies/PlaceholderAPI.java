package io.shadowrealm.skyprime.dependencies;

import io.shadowrealm.skyprime.SkyMaster;
import io.shadowrealm.skyprime.SkyPrime;
import io.shadowrealm.skyprime.VirtualIsland;
import io.shadowrealm.skyprime.storage.Island;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PlaceholderAPI extends PlaceholderExpansion
{

	private SkyPrime plugin;

	public PlaceholderAPI(SkyPrime plugin)
	{
		this.plugin = plugin;
	}

	/**
	 * Because this is an internal class,
	 * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
	 * PlaceholderAPI is reloaded
	 *
	 * @return true to persist through reloads
	 */
	@Override
	public boolean persist()
	{
		return true;
	}

	/**
	 * Because this is a internal class, this check is not needed
	 * and we can simply return {@code true}
	 *
	 * @return Always true since it's an internal class.
	 */
	@Override
	public boolean canRegister()
	{
		return true;
	}

	/**
	 * The name of the person who created this expansion should go here.
	 * <br>For convienience do we return the author from the plugin.yml
	 *
	 * @return The name of the author as a String.
	 */
	@Override
	public String getAuthor()
	{
		return plugin.getDescription().getAuthors().toString();
	}

	/**
	 * The placeholder identifier should go here.
	 * <br>This is what tells PlaceholderAPI to call our onRequest
	 * method to obtain a value if a placeholder starts with our
	 * identifier.
	 * <br>This must be unique and can not contain % or _
	 *
	 * @return The identifier in {@code %<identifier>_<value>%} as String.
	 */
	@Override
	public String getIdentifier()
	{
		return plugin.getDescription().getName().toLowerCase();
	}

	@Override
	public String getPlugin()
	{
		return null;
	}

	/**
	 * This is the version of the expansion.
	 * <br>You don't have to use numbers, since it is set as a String.
	 *
	 * For convienience do we return the version from the plugin.yml
	 *
	 * @return The version as a String.
	 */
	@Override
	public String getVersion()
	{
		return plugin.getDescription().getVersion();
	}

	private Island getIsland(Player player, String tag)
	{
		if (!tag.endsWith("_current")) {
			return SkyMaster.hasIslandLoaded(player) ? SkyMaster.getIsland(player).getIsland() : SkyMaster.getIslandConfig(player);
		}
		final VirtualIsland is = SkyMaster.getPlayerActiveIsland(player);
		return is == null ? SkyMaster.getIslandConfig(player) : is.getIsland();
	}

	/**
	 * This is the method called when a placeholder with our identifier
	 * is found and needs a value.
	 * <br>We specify the value identifier in this method.
	 * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
	 *
	 * @param  player
	 *         A {@link org.bukkit.Player Player}.
	 * @param  s
	 *         A String containing the identifier/value.
	 *
	 * @return possibly-null String of the requested identifier.
	 */
	@Override
	public String onPlaceholderRequest(Player player, String s)
	{
		final Island i = this.getIsland(player, s);
		if (null == i) return null;

		if (s.startsWith("level")) {
			return formatDecimal(i.getLevel() / 20D);
		} else if (s.startsWith("size_max")) {
			return formatInt(i.getMaxSize());
		} else if (s.startsWith("size")) {
			return formatDecimal(i.getWorldSize());
		} else if (s.startsWith("members")) {
			return formatInt(i.getMembers().size() + i.getAdmins().size());
		} else if (s.startsWith("value")) {
			return formatDecimal(i.getValue() / 20D);
		} else if (s.startsWith("id")) {
			return i.getId().toString();
		}

		return null;
	}

	private String formatDecimal(double d)
	{
		return String.format("%,.2f", d);
	}

	private String formatInt(int i)
	{
		return String.format("%,d", i);
	}
}
