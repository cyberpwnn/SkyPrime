package io.shadowrealm.skyprime.command;

import io.shadowrealm.skyprime.*;
import io.shadowrealm.skyprime.storage.Island;
import mortar.api.config.Comment;
import mortar.bukkit.command.Command;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;
import mortar.bukkit.plugin.Control;
import mortar.logic.format.F;
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

	@Command
	private CommandPrivacy privacy;

	@Command
	private CommandTransfer transfer;

	@Command
	private CommandDelayed delayed;

	@Command
	private CommandTopIslands topIslands;

	@Command
	private CommandHelp help;

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
				final Island i = is.getIsland();

				if(!is.getWorld().equals(sender.player().getWorld()))
				{
					sender.sendMessage("Loading your island.");
					SkyMaster.getIsland(sender.player()).spawn(sender.player());
					return true;
				}

				int width = 50 - is.getIsland().getName().length();
				final String div = C.DARK_AQUA + F.repeat("=", width/2);

				sender.setTag("");
				String speed = (is.getPhysicsSpeed() > 0.75 ? C.GREEN : (is.getPhysicsSpeed() > 0.30 ? C.YELLOW : C.RED)) + F.pc(is.getPhysicsSpeed());

				sender.sendMessage(new String[] {
					div + "[ " + C.AQUA + i.getName() + " " + C.DARK_AQUA + "]" + div,
					" &3Members: &f" + (i.totalMembers()) + " / " + i.getMaximumMembers() +
							"  &3Size: &f" + Math.round(i.getWorldSize()) + " / " + i.getMaxSize() +
							"  &3Level: &f" + Math.round(i.getLevel() / Config.LEVEL_DIVISOR) +
							"  &3Value: &f" + Math.round(i.getValue() / Config.VALUE_DIVISOR) + "  " +
							(!i.getProtection().isPublicVisibility() ? C.GREEN + "(PRIVATE)" : C.RED + "(PUBLIC)"),
					" &3Public Rules: " +
							(i.getProtection().isPublicPickup() ? C.RED + "Pickup  " : "") +
							(i.getProtection().isPublicPVP() ? C.RED + "PVP  " : "") +
							(i.getProtection().isPublicKill() ? C.RED + "Kill  " : "") +
							(i.getProtection().isPublicBuild() ? C.RED + "Build  " : "") +
							(i.getProtection().isPublicUseBlock() ? C.RED + "Use  " : "") +
							(i.getProtection().isPublicInteractBlock() ? C.RED + "Interact  " : "") +
							(i.getProtection().isPublicInteractEntity() ? C.RED + "Mob Interact  " : "")
						,
						" &3Physics Speed: " + speed +  "  &3Voltage &f"  +
								F.pc(is.getUsedVolts() / Voltage.getTotalIslandVoltage(i.getValue()), 0) + " &b-> " +
								"&f" + F.f(is.getUsedVolts(), 0) + "V &bof &f" + F.f(is.getTotalVolts(), 0) + "V &b(+" + F.f(is.getBonusVolts(), 0) + "V bonus)",
						" &7Change Settings: &b/sky config",
						" &7IslandChat: &b/sky chat [optional message]",
						" &7Top Islands: &b/sky top",
						" &7SkyPrime Commands: &b/sky help"
				});
			}

			else
			{
				sender.sendMessage("Create your own island with /sky create");
			}
		}

		return true;
	}
}
