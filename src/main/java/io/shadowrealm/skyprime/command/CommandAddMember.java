package com.volmit.skyprime.command;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.volmit.skyprime.SkyMaster;
import com.volmit.skyprime.storage.Island;

import mortar.api.sched.A;
import mortar.api.sched.S;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;
import mortar.bukkit.plugin.Mortar;
import mortar.lib.control.MojangProfileController;

public class CommandAddMember extends MortarCommand
{
	public CommandAddMember()
	{
		super("invite", "add");
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		if(!SkyMaster.hasIsland(sender.player()))
		{
			sender.sendMessage("You cant add members on an island you dont have. Use /sky create");
			return true;
		}

		if(!SkyMaster.hasIslandLoaded(sender.player()))
		{
			sender.sendMessage("You cant configure your island. It isnt loaded. Use /sky.");
			return true;
		}

		Island is = SkyMaster.getIsland(sender.player()).getIsland();
		String name = args[0];
		Player p = Bukkit.getPlayer(name);
		System.out.println(name);

		new A()
		{
			@Override
			public void run()
			{
				UUID id = null;

				if(p == null)
				{
					sender.sendMessage("Please wait, looking up offline player.");
					id = Mortar.getController(MojangProfileController.class).getOnlineUUID(name);
				}

				else
				{
					id = p.getUniqueId();
				}

				UUID idd = id;

				if(idd == null)
				{
					sender.sendMessage("Cannot find " + name);
				}

				else
				{
					new S()
					{
						@Override
						public void run()
						{
							System.out.println(idd);
							if(is.getMembers().contains(idd))
							{
								sender.sendMessage(name + " is already a member.");
							}

							else if(is.getOwner().equals(idd))
							{
								sender.sendMessage(name + " is the owner of this island.");
							}

							else
							{
								is.getMembers().add(idd);
								sender.sendMessage(is.getMembers().size() + " members");

								if(!SkyMaster.hasIslandLoaded(sender.player()))
								{
									SkyMaster.getStorageEngine().setIsland(is);
								}

								else
								{
									SkyMaster.getIsland(sender.player()).saveIsland();
								}

								sender.sendMessage(name + " was added to your island members.");
							}
						}
					};
				}
			}
		};

		return true;
	}
}
