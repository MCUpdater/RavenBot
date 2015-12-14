package org.mcupdater.ravenbot.features;

import org.apache.commons.lang3.StringUtils;
import org.mcupdater.ravenbot.AbstractListener;
import org.mcupdater.ravenbot.RavenBot;
import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class InfoHandler extends AbstractListener
{

	@Override
	public void handleCommand(String sender, final MessageEvent event, String command, String[] args) {
		if (command.equals(".info") || command.equals(".i") ) {
			if (args.length == 0) {
				event.respond("No key specified.");
				return;
			}
			if (args.length == 1) {
				String key = args[0];
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
			if (args.length > 1) {
				String key = args[0];
				String data = StringUtils.join(args, " ", 1, args.length);
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

	@Override
	protected void initCommands() {

	}
}
