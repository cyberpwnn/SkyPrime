package io.shadowrealm.skyprime.command;

import io.shadowrealm.skyprime.SkyPrime;
import io.shadowrealm.skyprime.controller.IslandRankController;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;
import mortar.lang.collection.GMap;
import mortar.util.text.C;

public class CommandTopIslands extends MortarCommand
{
	public CommandTopIslands()
	{
		super("topislands", "tops", "topisland", "tits", "top" ,"ranks");
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		final int resultsPerPage = 2;
		final GMap<Integer, IslandRankController.IslandDetails> ranks = SkyPrime.instance.islandRankController.getIslandRanks();
		final int pages = (int) Math.ceil(ranks.size() / (double) resultsPerPage);
		int page = 1;

		try {
			page = Integer.parseInt(args.length != 0 ? args[0] : "1");
		} catch (NumberFormatException ex) {
			sender.sendMessage(C.RED + "Invalid page number specified.");
			return true;
		}

		if (pages == 0) {
			sender.sendMessage(C.RED + "There are no island ranks! Please wait a moment for ranks to be calculated.");
			return true;
		}

		if (page < 1) {
			sender.sendMessage(C.RED + "Page cannot be below 1");
			return true;
		}

		if (page > pages) {
			sender.sendMessage(C.RED + "No more results");
			return true;
		}

		sender.sendMessage("-------------------------------");
		sender.sendMessage("Page " + page + " of " + pages + " pages - " + ranks.size() + " islands");
		if (page < pages) {
			sender.sendMessage("View next page: " + C.WHITE + "/is " + sender.getCommand() + " " + (page + 1));
		} else {
			sender.sendMessage("You are on the last page");
		}

		sender.sendMessage("-------------------------------");

		int i = 1;

		for (IslandRankController.IslandDetails d : ranks.values()) {
			++i;
			if (i / resultsPerPage == page) {
				sender.sendMessage(d.getRank() + " \u00bb " + d.getName());
			}
		}

		return true;
	}
}
