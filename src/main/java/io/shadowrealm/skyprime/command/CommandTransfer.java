package io.shadowrealm.skyprime.command;

import io.shadowrealm.skyprime.Config;
import io.shadowrealm.skyprime.SkyMaster;
import io.shadowrealm.skyprime.SkyPrime;
import io.shadowrealm.skyprime.storage.Island;
import io.shadowrealm.skyprime.storage.Visibility;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;
import mortar.bukkit.plugin.commands.DelayedCommand;
import mortar.lang.collection.GList;
import mortar.util.text.C;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CommandTransfer extends MortarCommand
{
	public CommandTransfer()
	{
		super("transfer", "tranny");
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		if (!Config.ISLAND_ALLOW_TRANSFER) {
			sender.sendMessage("Island transfers has been disabled.");
			return true;
		}

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

		if (args.length == 0) {
			sender.sendMessage("To transfer an island, you must use /sky transfer <player>");
			return true;
		}

		Island is = SkyMaster.getIsland(sender.player()).getIsland();
		OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);

		if (player == null || !player.isOnline()) {
			sender.sendMessage(C.RED + "Player not found or is not online.");
			return true;
		}

		if (player.equals(is.getOwnerPlayer())) {
			sender.sendMessage(C.RED + "You cannot transfer islands to yourself.");
			return true;
		}

		if (SkyMaster.hasIsland((Player) player)) {
			sender.sendMessage(C.RED + "That player already has an island.");
			return true;
		}

		sender.sendMessage("Please confirm your decision to transfer your island. Use " + C.WHITE + "/sky confirm" + C.GRAY + " or to cancel this transfer, " + C.WHITE + "/sky cancel");

		SkyPrime.instance.delayedController.register(
			new DelayedCommand(
				"Island transfer",
				sender,
				() -> {
					SkyPrime.instance.delayedController.register(
						new DelayedCommand(
							"Island transfer",
							new MortarSender((Player) player, sender.getTag()),
							() -> {
								is.transferIsland(player.getUniqueId());
								sender.sendMessage("You have successfully transferred your island to " + player.getName());
								((Player) player).sendMessage(sender.getTag() + "You are the new proud owner of: "+ C.WHITE + is.getName());
							},
							() -> {
								sender.sendMessage(player.getName() + " cancelled the island trade");
								((Player) player).sendMessage(sender.getTag() + "The island transfer was cancelled.");
							}
						)
					);

					sender.sendMessage("You sent a request to " + C.WHITE + player.getName() + C.GRAY + " to accept your island transfer");
					((Player) player).sendMessage(sender.getTag() + C.WHITE + sender.getName() + C.GRAY + " requested to transfer their island " + C.WHITE + is.getName() + C.GRAY + " to you. To accept this transfer, type " + C.WHITE + "/sky accept " + C.GRAY + "or cancel this transfer type " + C.WHITE + "/sky cancel");
				}
			)
		);

		return true;
	}
}
