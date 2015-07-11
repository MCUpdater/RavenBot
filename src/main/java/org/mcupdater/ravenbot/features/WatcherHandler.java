package org.mcupdater.ravenbot.features;

import org.mcupdater.ravenbot.RavenBot;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class WatcherHandler extends ListenerAdapter {

    @Override
    public void onMessage(final MessageEvent event) {
        User sender = event.getUser();
        try {
            PreparedStatement updateSeen = RavenBot.getInstance().getPreparedStatement("updateLastSeen");
            updateSeen.setString(1, sender.getNick());
            updateSeen.setLong(2, System.currentTimeMillis());
            updateSeen.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (event.getMessage().startsWith(".seen")) {
            try {
                PreparedStatement getSeen = RavenBot.getInstance().getPreparedStatement("getLastSeen");
                String[] splitMessage = event.getMessage().split(" ");
                String target = splitMessage[1];
                getSeen.setString(1, target);
                ResultSet results = getSeen.executeQuery();
                if (results.next()) {
                    event.respond(target + " was last seen " + formatTime(System.currentTimeMillis() - results.getLong(1)) + "ago.");
                } else {
                    event.respond(target + " has not been seen.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String formatTime(long delta) {
        StringBuilder duration = new StringBuilder();
        if (delta > 86400000L) {
            duration.append(Long.toString(delta / 86400000L)).append("d ");
            delta = delta % 86400000L;
        }
        if (delta > 3600000L) {
            duration.append(Long.toString(delta / 3600000L)).append("h ");
            delta = delta % 3600000L;
        }
        if (delta > 60000L) {
            duration.append(Long.toString(delta / 60000L)).append("m ");
            delta = delta % 60000L;
        }
        if (delta > 1000L) {
            duration.append(Long.toString(delta / 1000L)).append("s ");
        }
        if (duration.length() == 0) {
            duration.append("0s ");
        }
        return duration.toString();
    }
}
