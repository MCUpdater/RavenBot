package org.mcupdater.ravenbot.features;

import org.apache.commons.lang3.StringUtils;
import org.mcupdater.ravenbot.AbstractListener;
import org.mcupdater.ravenbot.RavenBot;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class QuoteHandler extends AbstractListener
{
	@Override
	protected void initCommands() {

	}

	@Override
	public void handleCommand(String sender, final MessageEvent event, String command, String[] args) {
		if (command.equals(".quote") || command.equals(".q")) {
			if (args.length == 0) {
				try {
					PreparedStatement getAnyQuote = RavenBot.getInstance().getPreparedStatement("getAnyQuote");
					ResultSet results = getAnyQuote.executeQuery();
					if (results.next()) {
						event.respond("<" + results.getString(1) + "> " + results.getString(2));
					}
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (args.length == 1) {
				String key = args[0];
				try {
					PreparedStatement getQuote = RavenBot.getInstance().getPreparedStatement("getUserQuote");
					getQuote.setString(1, key);
					ResultSet results = getQuote.executeQuery();
					if (results.next()) {
						event.respond("<" + key + "> " + results.getString(1));
					} else {
						event.respond("No quotes found for " + key);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (args.length > 1) {
				String key = args[0];
				String data = StringUtils.join(args, " ", 1, args.length);
				try {
					PreparedStatement addQuote = RavenBot.getInstance().getPreparedStatement("addQuote");
					addQuote.setString(1, key);
					addQuote.setString(2, data);
					if (addQuote.executeUpdate() > 0) {
						event.respond("Quote added.");
					} else {
						event.respond("An error occurred while trying to set the value.");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
