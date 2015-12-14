package org.mcupdater.ravenbot.features;

import org.apache.commons.lang3.StringUtils;
import org.mcupdater.ravenbot.AbstractListener;
import org.mcupdater.ravenbot.RavenBot;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TellHandler extends AbstractListener
{

    @Override
    public void onMessage(final MessageEvent event) {
	    super.onMessage(event);
	    try {
		    PircBotX bot = event.getBot();
		    String user = event.getUser().getNick();
            PreparedStatement checkTells = RavenBot.getInstance().getPreparedStatement("getTells");
            checkTells.setString(1, user);
            ResultSet results = checkTells.executeQuery();
		        while (results.next()) {
			        bot.sendIRC().notice(user, results.getString(2) + " in " + results.getString(3) + " said: " + results.getString(4));
		        }
		        PreparedStatement clearTells = RavenBot.getInstance().getPreparedStatement("removeTells");
		        clearTells.setString(1, user);
		        clearTells.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleCommand(String sender, MessageEvent event, String command, String[] args) {
        PircBotX bot = event.getBot();
        if (command.equals(".tell")) {
            try {
                PreparedStatement addTell = RavenBot.getInstance().getPreparedStatement("addTell");
                if (args.length == 0) {
                    event.respond("Who did you want to tell?");
                    return;
                }
                String recipient = args[0];
                if (args.length == 1) {
                    event.respond("What did you want to say to " + recipient + "?");
                    return;
                }

                String channel = event.getChannel().getName();
                String message = StringUtils.join(args," ", 1, args.length);

                addTell.setString(1, sender);
                addTell.setString(2, recipient);
                addTell.setString(3, channel);
                addTell.setString(4, message);
                addTell.executeUpdate();
                event.respond(recipient + " will be notified of this message when next seen.");
            } catch (Exception e) {
                e.printStackTrace();
                event.respond("An error occurred while processing this command (.tell)");
            }
        }
    }

    @Override
    protected void initCommands() {

    }

}
