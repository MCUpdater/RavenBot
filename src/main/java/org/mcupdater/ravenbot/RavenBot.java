package org.mcupdater.ravenbot;

import org.mcupdater.ravenbot.features.KickHandler;
import org.mcupdater.ravenbot.features.Magic8Ball;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.io.IOException;
import java.sql.*;
import java.util.*;

public class RavenBot extends ListenerAdapter {
    public static RavenBot instance;
    protected PircBotX bot;
    protected Connection connection = null;
    protected List<String> ops = new ArrayList<>();
    protected Map<String, PreparedStatement> preparedStatements = new HashMap<>();
    private Scanner scanner;

    public static void main(String[] args) {
        instance = new RavenBot();
    }

    public RavenBot() {
        scanner = new Scanner(System.in);
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
        loadOps();
        System.out.println("RavenBot 2.0 - WOOT!");
        Configuration.Builder<PircBotX> configBuilder = new Configuration.Builder()
                .setName(settings.getNick())
                .setAutoNickChange(true)
                .setRealName(settings.getRealName())
                .setLogin(settings.getLogin())
                .setServerHostname(settings.getServer())
                .setServerPort(settings.getPort())
                .addListener(new RavenBot.IRCListener())
                .addListener(new Magic8Ball())
                .addListener(new KickHandler());
        if (!settings.getNsPassword().isEmpty()) {
            configBuilder.setNickservPassword(settings.getNsPassword());
        }
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT name FROM channels;");
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                configBuilder.addAutoJoinChannel(rs.getString("name"));
            }
            if (rowCount == 0) {
                System.out.print("Please enter the name of the first channel to join (i.e. #RavenBot)\n> ");
                String channel = scanner.nextLine();
                configBuilder.addAutoJoinChannel(channel);
                preparedStatements.get("addChannel").setString(1,channel);
                preparedStatements.get("addChannel").executeUpdate();
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

    private void loadOps() {
        try {
            ResultSet readOps = connection.createStatement().executeQuery("SELECT name FROM ops;");
            int rowCount = 0;
            while (readOps.next()) {
                rowCount++;
                ops.add(readOps.getString("name"));
            }
            if (rowCount == 0) {
                System.out.print("Please enter the nick of the first person with op privileges for the bot:\n> ");
                String op = scanner.nextLine();
                ops.add(op);
                preparedStatements.get("addOp").setString(1, op);
                preparedStatements.get("addOp").executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private boolean initDatabase() {
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
            preparedStatements.put("addChannel", connection.prepareStatement("REPLACE INTO Channels (name) VALUES (?);"));
            preparedStatements.put("removeChannel",connection.prepareStatement("DELETE FROM Channels WHERE name = ?;"));
            preparedStatements.put("addOp",connection.prepareStatement("REPLACE INTO Ops (name) VALUES (?);"));
            preparedStatements.put("removeOp",connection.prepareStatement("DELETE FROM Ops WHERE name = ?;"));
            preparedStatements.put("addQuote",connection.prepareStatement("INSERT INTO Quotes(user, data) VALUES (?, ?);"));
            preparedStatements.put("getUserQuote",connection.prepareStatement("SELECT data FROM Quotes WHERE user = ? ORDER BY RANDOM () LIMIT 1;"));
            preparedStatements.put("getUserQuoteAll",connection.prepareStatement("SELECT data FROM Quotes WHERE user = ?;"));
            preparedStatements.put("getAnyQuote",connection.prepareStatement("SELECT user, data FROM Quotes ORDER BY RANDOM () LIMIT 1;"));
            preparedStatements.put("getSpecificQuote",connection.prepareStatement("SELECT data FROM Quotes WHERE user = ? AND data = ?;"));
            preparedStatements.put("removeQuote",connection.prepareStatement("DELETE FROM Quotes WHERE user = ? AND data = ?;"));
            preparedStatements.put("updateLastSeen",connection.prepareStatement("REPLACE INTO LastSeen(user, timestamp) VALUES (?, ?);"));
            preparedStatements.put("getLastSeen",connection.prepareStatement("SELECT timestamp FROM LastSeen WHERE user = ?;"));
            preparedStatements.put("updateInfo",connection.prepareStatement("REPLACE INTO Info(key, data) VALUES (?, ?);"));
            preparedStatements.put("getInfo",connection.prepareStatement("SELECT data FROM Info WHERE key = ?;"));
            preparedStatements.put("getInfoAll",connection.prepareStatement("SELECT key, data FROM Info;"));
            preparedStatements.put("removeInfo",connection.prepareStatement("DELETE FROM Info WHERE key = ?;"));
            preparedStatements.put("addTell",connection.prepareStatement("INSERT INTO Tells(sender, rcpt, channel, message) VALUES (?, ?, ?, ?);"));
            preparedStatements.put("getTells",connection.prepareStatement("SELECT rowid, sender, channel, message FROM Tells WHERE rcpt = ?;"));
            preparedStatements.put("removeTells",connection.prepareStatement("DELETE FROM Tells WHERE rcpt = ?;"));
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void sendMessage(String target, String message) {
        bot.sendIRC().message(target, message);
    }

    private class IRCListener extends ListenerAdapter {
        private Random rng = new Random();

        @Override
        public void onGenericMessage(final GenericMessageEvent event) {
            if (event.getMessage().startsWith(".ping")) {
                event.respond("Pong!");
                return;
            }
            /*
            YouTube lookup
            Twitter lookup
            update lastSeen
            .tell
            .google
            .youtube
            .info
            .wiki
            .down
            .quote
            .define
            .urban
            .curseforge
            .seen
            .help
            .ann
            .qlist
            .ilist

             */
        }

        @Override
        public void onPrivateMessage(final PrivateMessageEvent event) {
            if (ops.contains(event.getUser().getNick())) {
                event.respond("I obey!");

            } else {
                event.respond("You are not my master!");
            }
        }


    }
}
