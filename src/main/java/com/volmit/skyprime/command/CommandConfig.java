package com.volmit.skyprime.command;

import com.volmit.phantom.plugin.PhantomCommand;
import com.volmit.phantom.plugin.PhantomSender;
import com.volmit.skyprime.SkyMaster;
import com.volmit.skyprime.storage.Island;
import com.volmit.skyprime.storage.Visibility;

public class CommandConfig extends PhantomCommand
{
	public CommandConfig()
	{
		super("config", "cfg");
	}

	@Override
	public boolean handle(PhantomSender sender, String[] args)
	{
		if(!SkyMaster.hasIsland(sender.player()))
		{
			sender.sendMessage("You cant configure an island you dont have. Use /sky create");
			return true;
		}

		if(!SkyMaster.hasIslandLoaded(sender.player()))
		{
			sender.sendMessage("You cant configure your island. It isnt loaded. Use /sky.");
			return true;
		}

		Island is = SkyMaster.getIsland(sender.player()).getIsland();

		if(args.length == 0)
		{
			sender.sendMessage("public = " + is.getVisibility().equals(Visibility.PUBLIC));
			sender.sendMessage("despawn.arrow = " + is.getcDespawnArrow());
			sender.sendMessage("despawn.item = " + is.getcDespawnItem());
			sender.sendMessage("merge.xp = " + is.getcMergeXp());
			sender.sendMessage("merge.item = " + is.getcMergeItem());
			sender.sendMessage("hopper.rate = " + is.getcHopperRate());
			sender.sendMessage("hopper.amount = " + is.getcHopperAmount());
			sender.sendMessage("public.pickup = " + is.iscPublicPickup());
		}

		else if(args.length == 1)
		{
			sender.sendMessage("/sky config <key> <value>");
		}

		else if(args.length == 2)
		{
			boolean s = true;

			try
			{
				if(args[0].equalsIgnoreCase("despawn.arrow"))
				{
					is.setcDespawnArrow(Integer.valueOf(args[1]));
				}

				else if(args[0].equalsIgnoreCase("despawn.item"))
				{
					is.setcDespawnItem(Integer.valueOf(args[1]));
				}

				else if(args[0].equalsIgnoreCase("public.pickup"))
				{
					is.setcPublicPickup(Boolean.valueOf(args[1]));
				}

				else if(args[0].equalsIgnoreCase("merge.xp"))
				{
					is.setcMergeXp(Double.valueOf(args[1]));
				}

				else if(args[0].equalsIgnoreCase("merge.item"))
				{
					is.setcMergeItem(Double.valueOf(args[1]));
				}

				else if(args[0].equalsIgnoreCase("hopper.rate"))
				{
					is.setcHopperRate(Integer.valueOf(args[1]));
				}

				else if(args[0].equalsIgnoreCase("hopper.amount"))
				{
					is.setcHopperAmount(Integer.valueOf(args[1]));
				}

				else if(args[0].equalsIgnoreCase("public"))
				{
					is.setVisibility(Boolean.valueOf(args[1]) ? Visibility.PUBLIC : Visibility.PRIVATE);
				}

				else
				{
					s = false;
					sender.sendMessage("Unknown Key. Use /sky config");
				}
			}

			catch(Throwable e)
			{
				s = false;
				sender.sendMessage("Invalid value.");
			}

			if(s)
			{
				sender.sendMessage(args[0].toLowerCase() + " set to " + args[1]);
				SkyMaster.getIsland(sender.player()).saveConfig(sender);
			}
		}

		return true;
	}
}
