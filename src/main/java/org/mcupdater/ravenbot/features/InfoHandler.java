package org.mcupdater.ravenbot.features;

import org.apache.commons.lang3.StringUtils;
import org.mcupdater.ravenbot.RavenBot;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class InfoHandler extends ListenerAdapter<PircBotX>
{

	@Override
	public void onMessage(final MessageEvent event) {
		if (event.getMessage().startsWith(".info")) {
			String[] splitMessage = event.getMessage().split(" ");
			if (splitMessage.length == 1) {
				event.respond("No key specified.");
				return;
			}
			if (splitMessage.length == 2) {
				String key = splitMessage[1];
				try {
					PreparedStatement getInfo = RavenBot.getInstance().getPreparedStatement("getInfo");
					getInfo.setString(1, key);
					ResultSet results = getInfo.executeQuery();
					if (results.next()) {
						event.respond(Colors.BOLD + key + Colors.NORMAL + " - " + results.getString(1));
					} else {
						event.respond("No information found for: " + key);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (splitMessage.length > 2) {
				String key = splitMessage[1];
				String data = StringUtils.join(splitMessage, " ", 2, splitMessage.length);
				try {
					PreparedStatement updateInfo = RavenBot.getInstance().getPreparedStatement("updateInfo");
					updateInfo.setString(1, key);
					updateInfo.setString(2, data);
					if (updateInfo.executeUpdate() > 0) {
						event.respond("Value set.");
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
