package io.shadowrealm.skyprime.command;

import io.shadowrealm.skyprime.SkyPrime;
import mortar.bukkit.command.MortarCommand;
import mortar.bukkit.command.MortarSender;
import org.apache.commons.lang.StringUtils;

public class CommandSay extends MortarCommand
{
	public CommandSay()
	{
		super("say", "s", "chat", "send", "talk");
	}

	@Override
	public boolean handle(MortarSender sender, String[] args)
	{
		if (args.length == 0) {
			final boolean s = SkyPrime.instance.islandChatController.isSubscribed(sender.player());
			if (s) {
				SkyPrime.instance.islandChatController.unsubscribe(sender.player());
				sender.sendMessage("You are no longer in island chat");
			} else {
				SkyPrime.instance.islandChatController.subscribe(sender.player());
				sender.sendMessage("You are now in island chat. All messages will be sent to the island you are physically in or to your personal island.");
			}
			return true;
		}

		if (!SkyPrime.instance.islandChatController.doChat(sender.player(), StringUtils.join(args, " "))) {
			sender.sendMessage("You can't speak in island chat. You are not in an island.");
		}

		return true;
	}
}
