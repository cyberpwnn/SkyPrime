package com.volmit.skyprime.command;

import com.volmit.skyprime.SkyMaster;
import com.volmit.skyprime.VirtualIsland;

import mortar.bukkit.command.Command;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;
import mortar.util.text.C;

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

	public CommandSkyPrime()
	{
		super("skyprime", "sky", "sp", "sprime", "is", "island");
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		if(sender.isPlayer())
		{
			if(SkyMaster.hasIsland(sender.player()))
			{
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
					sender.sendMessage("/sky setwarp - Set your island public warp");
					sender.sendMessage("/sky spawn - Teleport to island spawn");
					sender.sendMessage("/sky setspawn - Set your island spawn");
					sender.sendMessage("/sky members - View and modify members");
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
