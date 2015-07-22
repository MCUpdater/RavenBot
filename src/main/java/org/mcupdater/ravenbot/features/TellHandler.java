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
        PircBotX bot = event.getBot();
        User sender = event.getUser();
        if (event.getMessage().startsWith(".tell")) {
            try {
                PreparedStatement addTell = RavenBot.getInstance().getPreparedStatement("addTell");
                String[] splitMessage = event.getMessage().split(" ");
                if (splitMessage.length == 1) {
                    event.respond("Who did you want to tell?");
                    return;
                }
                String recipient = splitMessage[1];
                if (splitMessage.length == 2) {
                    event.respond("What did you want to say to " + recipient + "?");
                    return;
                }

                String channel = event.getChannel().getName();
                String message = StringUtils.join(splitMessage," ", 2, splitMessage.length);

                addTell.setString(1, sender.getNick());
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
        try {
            PreparedStatement checkTells = RavenBot.getInstance().getPreparedStatement("getTells");
            checkTells.setString(1, sender.getNick());
            ResultSet results = checkTells.executeQuery();
            while (results.next()) {
                bot.sendIRC().notice(sender.getNick(), results.getString(2) + " in " + results.getString(3) + " said: " + results.getString(4));
            }
            PreparedStatement clearTells = RavenBot.getInstance().getPreparedStatement("removeTells");
            clearTells.setString(1, sender.getNick());
            clearTells.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initCommands() {

    }
}
