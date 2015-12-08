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
	public void onMessage(final MessageEvent event) {
		String[] splitMessage = event.getMessage().split(" ");
		if (splitMessage[0].equals(".quote") || splitMessage[0].equals(".q")) {
			if (splitMessage.length == 1) {
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
			if (splitMessage.length == 2) {
				String key = splitMessage[1];
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
			if (splitMessage.length > 2) {
				String key = splitMessage[1];
				String data = StringUtils.join(splitMessage, " ", 2, splitMessage.length);
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
