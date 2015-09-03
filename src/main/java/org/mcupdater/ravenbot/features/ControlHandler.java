package org.mcupdater.ravenbot.features;

import org.apache.commons.lang3.StringUtils;
import org.mcupdater.ravenbot.AbstractListener;
import org.mcupdater.ravenbot.RavenBot;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.WaitForQueue;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.events.WhoisEvent;

import java.sql.PreparedStatement;

public class ControlHandler extends AbstractListener
{
	@Override
	public void onPrivateMessage(final PrivateMessageEvent event) {
		PircBotX bot = event.getBot();
		String sender = event.getUser().getNick();
		boolean isOp = RavenBot.getInstance().isOp(event.getBot(), event.getUser());
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
					String newOpNick = splitMessage[1];
					User newOp = bot.getUserChannelDao().getUser(newOpNick);
					if (!newOp.isVerified()) {
						bot.sendIRC().notice(sender, "User " + newOpNick + " is not a registered user.");
						return;
					}
					String nsRegistration;
					bot.sendRaw().rawLine("WHOIS " + newOpNick + " " + newOpNick);
					WaitForQueue waitForQueue = new WaitForQueue(bot);
					WhoisEvent whoisEvent = waitForQueue.waitFor(WhoisEvent.class);
					waitForQueue.close();
					nsRegistration = whoisEvent.getRegisteredAs();
					RavenBot.getInstance().getOps().add(nsRegistration);
					PreparedStatement addOp = RavenBot.getInstance().getPreparedStatement("addOp");
					addOp.setString(1, nsRegistration);
					addOp.executeUpdate();
					bot.sendIRC().notice(sender, "User " + newOpNick + " (" + nsRegistration + ") added to list.");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (splitMessage[0].equals("deop")) {
			if (isOp) {
				if(RavenBot.getInstance().getOps().contains(splitMessage[1])) {
					try {
						PreparedStatement removeOp = RavenBot.getInstance().getPreparedStatement("removeOp");
						removeOp.setString(1,splitMessage[1]);
						removeOp.executeUpdate();
						RavenBot.getInstance().getOps().remove(splitMessage[1]);
						bot.sendIRC().notice(sender, "User removed from list.");
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					bot.sendIRC().notice(sender, "That user is not in the list. Verify the NickServ name is correct.");
				}
			}
		}
		if (splitMessage[0].equals("listOps")) {
			bot.sendIRC().notice(sender,"Op list");
			for (String entry : RavenBot.getInstance().getOps()) {
				bot.sendIRC().notice(sender, entry);
			}
		}
		if (splitMessage[0].equals("speakas")) {
			if (isOp) {
				bot.sendIRC().message(splitMessage[1], StringUtils.join(splitMessage, " ", 2, splitMessage.length));
			}
		}
	}

	@Override
	protected void initCommands() {

	}
}
