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

	@Command("Island")
	private CommandSetSpawn setspawn;

	@Command("Island")
	private CommandSpawn spawn;

	@Command("Island")
	private CommandWarp warp;

	@Command("Island")
	private CommandSetWarp setwarp;

	@Command("Island")
	private CommandVoltage voltage;

	@Command("Island")
	private CommandValue value;

	@Command("Island")
	private CommandSave save;

	@Command("Island")
	private CommandReboot reboot;

	@Command("Island")
	private CommandConfig config;

	@Command("Island")
	private CommandDelete delete;

	@Command("Island")
	private CommandCreate create;

	@Command("Island")
	private CommandRecreate recreate;

	@Command("Island")
	private CommandVisit visit;

	@Command("Members")
	private CommandMembers members;

	@Command("Admin")
	private CommandStatus stats;

	public CommandSkyPrime()
	{
		super("skyprime", "sky", "sp", "sprime");
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
					sender.sendMessage(C.YELLOW + "/sky reboot - Reboot your island");
					sender.sendMessage(C.RED + "/sky delete - Delete your island");
					sender.sendMessage(C.RED + "/sky recreate - Delete and create a new island");
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
