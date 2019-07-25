package io.shadowrealm.skyprime.command;

import io.shadowrealm.skyprime.SkyMaster;
import io.shadowrealm.skyprime.SkyPrime;
import lombok.Getter;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarPermission;
import mortar.bukkit.command.MortarSender;
import mortar.lang.collection.GList;
import mortar.util.text.C;

public class CommandHelp extends MortarCommand
{
	private GList<PluginCommand> commands = new GList<PluginCommand>() {{
		add(new PluginCommand("/sky top", "Views the top player islands", false));
		add(new PluginCommand("/sky create", "Creates an island", false));
		add(new PluginCommand("/sky visit <player>", "Visits a player's island", false));
		add(new PluginCommand("/sky chat", "Toggles IslandChat or sends a single message"));

		add(new PluginCommand("/sky add <player>", "Add a player", true, SkyPrime.perm.members.add));
		add(new PluginCommand("/sky invite <player>", "Invites a player"));
		add(new PluginCommand("/sky kick <player>", "Removes an island player"));
		add(new PluginCommand("/sky members", "Manage and view island members"));

		add(new PluginCommand("/sky warp", "Teleports to public warp"));
		add(new PluginCommand("/sky setwarp", "Changes the public warp"));
		add(new PluginCommand("/sky spawn", "Teleports to island's spawn"));
		add(new PluginCommand("/sky setspawn", "Changes the island's spawn"));

		add(new PluginCommand("/sky voltage", "Indepth island voltage usage"));
		add(new PluginCommand("/sky help", "Views additional help (this command)"));
		add(new PluginCommand("/sky config", "View and change island settings"));

		add(new PluginCommand("/sky save", "Saves your island"));
		add(new PluginCommand("&e/sky reboot", "&eRestarts your island"));
		add(new PluginCommand("&c/sky transfer <player>", "&cTransfers your island to another player"));
		add(new PluginCommand("&c/sky recreate", "&cRecreates your island"));
		add(new PluginCommand("&c/sky delete", "&cPermanently delete your island"));
	}};

	public CommandHelp()
	{
		super("help", "?");
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		int perPage = 10, pages = (int) Math.ceil((double) commands.size() / (double) perPage), page = 0;

		try {
			page = args.length == 1 ? Integer.parseInt(args[0]) : 0;
			if (page < 1) page = 1;
			else if (page > pages) page = pages;
		} catch (Exception e) {
			page = 1;
		}

		--page;

		sender.sendMessage("Available SkyPrime commands:");
		sender.sendMessage(C.translateAlternateColorCodes('&', " Page &f" + (page+1) + " &7of &f" + pages + "  &b/sky " + sender.getCommand() + " [page]"));
		sender.setTag(" ");

		boolean hasIsland = SkyMaster.hasIsland(sender.player()) && SkyMaster.hasIslandLoaded(sender.player());
		PluginCommand cmd;

		for (int i = 0; i < perPage; ++i) {
			if (!commands.hasIndex(i + page * perPage)) break;

			cmd = commands.get(i + page*perPage);
			if (cmd.requireIsland && !hasIsland || (cmd.getPerm() != null && !cmd.getPerm().has(sender.getS()))) continue;
			sender.sendMessage(C.AQUA + C.translateAlternateColorCodes('&', cmd.getCommand()) + C.GRAY + " - " + C.translateAlternateColorCodes('&', cmd.getDesc()));
		}
		return true;
	}

	private static class PluginCommand
	{
		@Getter
		private String command;

		@Getter
		private String desc;

		@Getter
		private boolean requireIsland;

		@Getter
		private MortarPermission perm;

		public PluginCommand(String c, String desc)
		{
			this(c, desc, true);
		}

		public PluginCommand(String command, String desc, boolean r)
		{
			this(command, desc, r, null);
		}

		public PluginCommand(String command, String desc, boolean requireIsland, MortarPermission perm)
		{
			this.command = command;
			this.requireIsland = requireIsland;
			this.perm = perm;
			this.desc = desc;
		}
	}
}
