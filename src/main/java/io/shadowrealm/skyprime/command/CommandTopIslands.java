package io.shadowrealm.skyprime.command;

import io.shadowrealm.skyprime.Config;
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

	/**
	 * This code is really gay and hacky
	 *
	 * @param sender
	 *            the volume sender (pre-tagged)
	 * @param args
	 *            the arguments after this command node
	 * @return
	 * @todo use proper java pagination limit streams/util
	 */
	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		final int resultsPerPage = Config.RANKS_PERPAGE;
		final GMap<Integer, IslandRankController.IslandDetails> ranks = SkyPrime.instance.islandRankController.getIslandRanks();
		final int pages = (int) Math.ceil(ranks.size() / (double) resultsPerPage);
		int page;

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

		int i = 0;

		for (IslandRankController.IslandDetails d : ranks.values()) {
			++i;
			if (Math.ceil(i / (double) resultsPerPage) == page) {
				sender.sendMessage(d.getRank() + " \u00bb " + d.getName());
			}
		}

		return true;
	}
}
