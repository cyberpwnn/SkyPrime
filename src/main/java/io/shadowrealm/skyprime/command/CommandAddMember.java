package io.shadowrealm.skyprime.command;

import java.util.UUID;

import io.shadowrealm.skyprime.SkyPrime;
import io.shadowrealm.skyprime.VirtualIsland;
import mortar.bukkit.plugin.commands.DelayedCommand;
import mortar.util.text.C;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.shadowrealm.skyprime.SkyMaster;
import io.shadowrealm.skyprime.storage.Island;
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

		final VirtualIsland vi = SkyMaster.getIsland(sender.player());
		final Island is = vi.getIsland();

		if (is.getMaximumMembers() < is.totalMembers()) {
			sender.sendMessage(C.RED + "You exceeded your limit for " + is.getMaximumMembers() + " maximum members");
			return true;
		}

		String name = args[0];
		Player p = Bukkit.getPlayer(name);
		if (p == null || !p.isOnline()) {
			sender.sendMessage("Cannot find " + name);
			return true;
		}

		UUID idd = p.getUniqueId();

		new A()
		{
			@Override
			public void run()
			{
				/*UUID id = null;

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
				}

				else
				{*/
					new S()
					{
						@Override
						public void run()
						{
							if(is.getMembers().contains(idd))
							{
								sender.sendMessage(name + " is already a member.");
								return;
							}

							else if(is.getOwner().equals(idd))
							{
								sender.sendMessage(name + " is the owner of this island.");
								return;
							}

							if (sender.getCommand().equalsIgnoreCase("add"))
							{
								if (SkyPrime.perm.members.add.has(sender)) {
									is.getMembers().add(idd);
									sender.sendMessage(is.getMembers().size() + " members");
									vi.sendMessage(sender.getTag() + p.getName() + " has joined your island.");
									save(is, sender.player());
								} else {
									sender.sendMessage(C.RED + "You do not have permission to add players");
								}
							} else {
								p.sendMessage(sender.getTag() + "You were invited to join " + C.WHITE + is.getName() + C.GRAY +
											" You can accept this invitation using " + C.WHITE + "/sky accept " + C.GRAY  +
											"or reject this request " + C.WHITE + "/sky reject");
								sender.sendMessage("You have successfully sent an invitation to " + p.getName());

								SkyPrime.instance.delayedController.register(
									new DelayedCommand(
										"Island invitation",
										new MortarSender(p, sender.getTag()),
										() -> {
											is.getMembers().add(idd);
											save(is, sender.player());
											vi.sendMessage(sender.getTag() + p.getName() + " has joined the island!");
										},
										() -> {
											p.sendMessage(sender.getTag() + "Your island invitation has expired");
											sender.sendMessage(p.getName() + "'s island invitation has expired.");
										}
									)
								);
							}
						}
					};
				// }
			}
		};

		return true;
	}

	private void save(Island is, Player p)
	{
		if(!SkyMaster.hasIslandLoaded(p))
		{
			SkyMaster.getStorageEngine().setIsland(is);
		}

		else
		{
			SkyMaster.getIsland(p).saveIsland();
		}
	}

}
