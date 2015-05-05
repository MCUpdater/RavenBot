package org.mcupdater.ravenbot;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.io.IOException;
import java.sql.*;

public class RavenBot extends ListenerAdapter {
    private static PircBotX bot;
    private static Connection connection = null;

    public static void main(String[] args) {
        Settings settings = SettingsManager.getInstance().getSettings();
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        if (!initDatabase()) {
            System.err.println("Database Failure!");
            return;
        }
        System.out.println("RavenBot 2.0 - WOOT!");
        Configuration.Builder configBuilder = new Configuration.Builder()
                .setName(settings.getNick())
                .setAutoNickChange(true)
                .setRealName(settings.getRealName())
                .setLogin(settings.getLogin())
                .setServerHostname(settings.getServer())
                .setServerPort(settings.getPort())
                .setNickservPassword(settings.getNsPassword())
                .addListener(new RavenBot());
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT name FROM channels;");
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                configBuilder.addAutoJoinChannel(rs.getString("name"));
            }
            if (rowCount == 0) {
                //TODO Request initial channel to join
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        bot = new PircBotX(configBuilder.buildConfiguration());
        try {
            bot.startBot();
        } catch (IOException | IrcException e) {
            e.printStackTrace();
        }
    }

    private static boolean initDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:raven.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Channels(name)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Ops(name)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Tells(sender, rcpt, channel, message)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Info(key PRIMARY KEY, data)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Quotes(user, data)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS LastSeen(user PRIMARY KEY, timestamp)");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onGenericMessage(final GenericMessageEvent event) {
        if (event.getMessage().startsWith(".ping")) {
            event.respond("Pong!");
        }
    }

    @Override
    public void onPrivateMessage(final PrivateMessageEvent event) {
        if (event.getUser().getNick().equals("smbarbour")) {
            event.respond("I obey!");

        } else {
            event.respond("You are not my master!");
        }
    }
}
