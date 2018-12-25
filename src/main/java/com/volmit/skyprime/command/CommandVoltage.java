package com.volmit.skyprime.command;

import com.volmit.skyprime.SkyMaster;
import com.volmit.skyprime.VirtualIsland;
import com.volmit.skyprime.Voltage;
import com.volmit.skyprime.storage.Island;
import com.volmit.volume.bukkit.command.PawnCommand;
import com.volmit.volume.bukkit.command.VolumeSender;
import com.volmit.volume.bukkit.util.text.C;
import com.volmit.volume.lang.format.F;

public class CommandVoltage extends PawnCommand
{
	public CommandVoltage()
	{
		super("voltage", "volt", "v", "volts");
	}

	@Override
	public boolean handle(VolumeSender sender, String[] args)
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
		sender.sendMessage("Island Voltage: " + C.LIGHT_PURPLE + F.pc(v.getUsedVolts() / Voltage.getTotalIslandVoltage(is.getValue()), 0) + C.GRAY + " -> " + C.BOLD + C.LIGHT_PURPLE + F.f(v.getUsedVolts(), 0) + "V" + C.GRAY + " of " + C.BOLD + C.LIGHT_PURPLE + F.f(v.getTotalVolts(), 0) + "V" + C.GRAY + " (" + F.f(v.getBonusVolts(), 0) + "V bonus)");
		sender.sendMessage("  Entity Voltage: " + C.BOLD + C.LIGHT_PURPLE + F.f(v.getUsedEntityVolts(), 0) + "V" + C.GRAY + " / " + C.BOLD + C.LIGHT_PURPLE + F.f(v.getAllowedEntityVoltage(), 0) + "V");
		sender.sendMessage("  Tile Voltage: " + C.BOLD + C.LIGHT_PURPLE + F.f(v.getUsedTileVolts(), 0) + "V" + C.GRAY + " / " + C.BOLD + C.LIGHT_PURPLE + F.f(v.getAllowedTileVoltage(), 0) + "V");

		return true;
	}
}
