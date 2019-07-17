package io.shadowrealm.skyprime.command;

import io.shadowrealm.skyprime.SkyMaster;
import io.shadowrealm.skyprime.VirtualIsland;
import mortar.bukkit.command.Command;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;
import mortar.util.text.C;
import org.apache.commons.lang.StringUtils;

public class CommandSkyPrime extends MortarCommand
{
	@Command
	private CommandReload reload;

	@Command
	private CommandGen gen;

	@Command
	private CommandSetSpawn setspawn;

	@Command
	private CommandSpawn spawn;

	@Command
	private CommandWarp warp;

	@Command
	private CommandSetWarp setwarp;

	@Command
	private CommandVoltage voltage;

	@Command
	private CommandValue value;

	@Command
	private CommandSave save;

	@Command
	private CommandReboot reboot;

	@Command
	private CommandConfig config;

	@Command
	private CommandDelete delete;

	@Command
	private CommandCreate create;

	@Command
	private CommandRecreate recreate;

	@Command
	private CommandVisit visit;

	@Command
	private CommandMembers members;

	@Command
	private CommandStatus stats;

	@Command
	private CommandSay say;

	public CommandSkyPrime()
	{
		super("skyprime", "sky", "sp", "sprime", "is", "island", "invite", "kick");
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		if(sender.isPlayer())
		{
			if(SkyMaster.hasIsland(sender.player()))
			{
				if (args.length != 0 && (args[0].equalsIgnoreCase("invite") || args[0].equalsIgnoreCase("kick"))) {
					if (args.length > 1) {
						String[] args2 = new String[] { args[1] };
						if (args[0].equalsIgnoreCase("invite")) return this.members.add.handle(sender, args2);
						else if (args[0].equalsIgnoreCase("kick")) return this.members.del.handle(sender, args2);
					} else {
						sender.sendMessage("Please specify a player name: /sky " + args[0] + " <player>");
						return true;
					}
				}

				SkyMaster.ensureIslandLoaded(sender.player());
				VirtualIsland is = SkyMaster.getIsland(sender.player());

				if(!is.getWorld().equals(sender.player().getWorld()))
				{
					sender.sendMessage("Loading your island.");
					SkyMaster.getIsland(sender.player()).spawn(sender.player());
				}

				else
				{
					sender.sendMessage("/sky value - Check your island value");
					sender.sendMessage("/sky voltage - Check your island voltage");
					sender.sendMessage("/sky config - Configure your island");
					sender.sendMessage("/sky save - Save your island");
					sender.sendMessage("/sky warp - Teleport to public warp");
					sender.sendMessage("/sky chat - Toggle or send messages to IslandChat");
					sender.sendMessage("/sky setwarp - Set your island public warp");
					sender.sendMessage("/sky spawn - Teleport to island spawn");
					sender.sendMessage("/sky setspawn - Set your island spawn");
					sender.sendMessage("/sky members - View and modify members");
					sender.sendMessage("/sky invite <player> - Invites a player to your island");
					sender.sendMessage("/sky kick <player> - Removes a player from your island");
					sender.sendMessage(C.YELLOW + "/sky reboot - Reboot your island");
					sender.sendMessage(C.RED + "/sky delete - Delete your island");
					sender.sendMessage(C.RED + "/sky recreate - Delete and create a new island");
					sender.sendMessage("/sky visit <player> - Visit another player's island");
				}
			}

			else
			{
				sender.sendMessage("Create your own island with /sky create");
			}
		}

		return true;
	}
}
