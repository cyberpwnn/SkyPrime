package io.shadowrealm.skyprime.command;

import io.shadowrealm.skyprime.SkyMaster;
import io.shadowrealm.skyprime.VirtualIsland;
import io.shadowrealm.skyprime.Voltage;
import io.shadowrealm.skyprime.storage.Island;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;
import mortar.logic.format.F;
import mortar.util.text.C;

public class CommandVoltage extends MortarCommand
{
	public CommandVoltage()
	{
		super("voltage", "volt", "v", "volts");
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		if(!SkyMaster.hasIsland(sender.player()))
		{
			sender.sendMessage("You cant check voltage on an island you dont have. Use /sky create");
			return true;
		}

		if(!SkyMaster.hasIslandLoaded(sender.player()))
		{
			sender.sendMessage("You cant check voltage on your island. It isnt loaded. Use /sky.");
			return true;
		}

		VirtualIsland v = SkyMaster.getIsland(sender.player());
		Island is = v.getIsland();
		sender.sendMessage("Physics Speed: " + C.GREEN + F.pc(v.getPhysicsSpeed()));
		sender.sendMessage("Island Voltage: " + C.LIGHT_PURPLE + F.pc(v.getUsedVolts() / Voltage.getTotalIslandVoltage(is.getValue()), 0) + C.GRAY + " -> " + C.BOLD + C.LIGHT_PURPLE + F.f(v.getUsedVolts(), 0) + "V" + C.GRAY + " of " + C.BOLD + C.LIGHT_PURPLE + F.f(v.getTotalVolts(), 0) + "V" + C.GRAY + " (" + F.f(v.getBonusVolts(), 0) + "V bonus)");
		sender.sendMessage("  Entity Voltage: " + C.BOLD + C.LIGHT_PURPLE + F.f(v.getUsedEntityVolts(), 0) + "V" + C.GRAY + " / " + C.BOLD + C.LIGHT_PURPLE + F.f(v.getAllowedEntityVoltage(), 0) + "V");
		sender.sendMessage("  Tile Voltage: " + C.BOLD + C.LIGHT_PURPLE + F.f(v.getUsedTileVolts(), 0) + "V" + C.GRAY + " / " + C.BOLD + C.LIGHT_PURPLE + F.f(v.getAllowedTileVoltage(), 0) + "V");

		for(String i : v.getDraw().k())
		{
			sender.sendMessage("  " + F.capitalizeWords(i.replaceAll("-", " ")) + ": " + C.BOLD + C.LIGHT_PURPLE + F.f(v.getDraw().get(i).longValue()) + "V");
		}

		return true;
	}
}
