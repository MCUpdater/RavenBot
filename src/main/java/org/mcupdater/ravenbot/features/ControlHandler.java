package org.mcupdater.ravenbot.features;

import org.mcupdater.ravenbot.RavenBot;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import java.sql.PreparedStatement;

public class ControlHandler extends ListenerAdapter<PircBotX>
{
	@Override
	public void onPrivateMessage(final PrivateMessageEvent event) {
		PircBotX bot = event.getBot();
		String sender = event.getUser().getNick();
		boolean isOp = RavenBot.getInstance().getOps().contains(sender);
		String[] splitMessage = event.getMessage().split(" ");
		if (splitMessage[0].equals("join")) {
			if (isOp) {
				try {
					String newChannel = splitMessage[1];
					PreparedStatement addChannel = RavenBot.getInstance().getPreparedStatement("addChannel");
					addChannel.setString(1, newChannel);
					addChannel.executeUpdate();
					bot.sendIRC().joinChannel(newChannel);
					bot.sendIRC().notice(sender, "Joined channel " + newChannel);
				} catch (Exception e) {
					e.printStackTrace();
					bot.sendIRC().notice(sender, "Something went wrong!");
				}
			} else {
				bot.sendIRC().notice(sender, "You cannot do that.");
			}
		}
		if (splitMessage[0].equals("part")) {
			if (isOp) {
				try {
					String oldChannel = splitMessage[1];
					PreparedStatement removeChannel = RavenBot.getInstance().getPreparedStatement("removeChannel");
					removeChannel.setString(1, oldChannel);
					removeChannel.executeUpdate();
					bot.getUserChannelDao().getChannel(oldChannel).send().part();
					bot.sendIRC().notice(sender, "Left channel " + oldChannel);
				} catch (Exception e) {
					e.printStackTrace();
					bot.sendIRC().notice(sender, "Something went wrong!");
				}
			} else {
				bot.sendIRC().notice(sender, "You cannot do that.");
			}
		}
		if (splitMessage[0].equals("op")) {
			if (isOp) {
				try {
					String newOp = splitMessage[1];
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (splitMessage[0].equals("deop")) {
			if (isOp) {

			}
		}
	}
}
