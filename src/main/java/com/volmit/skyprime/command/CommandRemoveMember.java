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

public class CommandRemoveMember extends MortarCommand
{
	public CommandRemoveMember()
	{
		super("remove", "delete");
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		if(!SkyMaster.hasIsland(sender.player()))
		{
			sender.sendMessage("You cant remove members on an island you dont have. Use /sky create");
			return true;
		}

		Island is = SkyMaster.getIslandConfig(sender.player());
		String name = args[0];
		Player p = Bukkit.getPlayer(name);

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

				new S()
				{
					@Override
					public void run()
					{
						if(!is.getMembers().contains(idd))
						{
							sender.sendMessage(name + " is not a member.");
						}

						else
						{
							is.getAdmins().remove(idd);
							is.getMembers().remove(idd);

							if(!SkyMaster.hasIslandLoaded(sender.player()))
							{
								SkyMaster.getStorageEngine().setIsland(is);
							}

							else
							{
								SkyMaster.getIsland(sender.player()).saveIsland();
							}

							sender.sendMessage(name + " was removed from your island members.");
						}
					}
				};
			}
		};

		return true;
	}
}
