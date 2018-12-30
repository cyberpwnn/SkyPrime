package com.volmit.skyprime.command;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.volmit.phantom.plugin.A;
import com.volmit.phantom.plugin.PhantomCommand;
import com.volmit.phantom.plugin.PhantomSender;
import com.volmit.phantom.plugin.S;
import com.volmit.phantom.plugin.SVC;
import com.volmit.phantom.services.MojangProfileSVC;
import com.volmit.skyprime.SkyMaster;
import com.volmit.skyprime.storage.Island;

public class CommandRemoveMember extends PhantomCommand
{
	public CommandRemoveMember()
	{
		super("remove", "delete");
	}

	@Override
	public boolean handle(PhantomSender sender, String[] args)
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
					id = SVC.get(MojangProfileSVC.class).getOnlineUUID(name);
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
