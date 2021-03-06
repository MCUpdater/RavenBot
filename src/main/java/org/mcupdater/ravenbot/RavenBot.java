package org.mcupdater.ravenbot;

import org.mcupdater.ravenbot.features.*;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.WaitForQueue;
import org.pircbotx.hooks.events.WhoisEvent;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class RavenBot {
    private static RavenBot instance;
    private PircBotX bot;
    private Connection connection = null;
    private final List<String> ops = new ArrayList<>();
    private final Map<String, PreparedStatement> preparedStatements = new HashMap<>();
    private final Scanner scanner;
	private Map<UUID,ExpiringToken> userCache = new HashMap<>();
    private boolean keepAlive;

    public static void main(String[] args) {
        instance = new RavenBot();
        try {
            instance.bot.startBot();
        } catch (IOException | IrcException e) {
            e.printStackTrace();
        }

    }

    public RavenBot() {
        keepAlive = true;
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
        System.out.println("RavenBot 2.0");
        Configuration.Builder configBuilder = new Configuration.Builder()
                .setName(settings.getNick())
                .setAutoNickChange(true)
                .setRealName(settings.getRealName())
                .setLogin(settings.getLogin())
		        .addServer(settings.getServer(), settings.getPort())
		        .setAutoReconnect(true)
                .addListener(new DebugHandler())
                .addListener(new ControlHandler())
                .addListener(new Magic8BallHandler())
                .addListener(new KickHandler())
                .addListener(new TellHandler())
                .addListener(new YouTubeHandler())
                .addListener(new TwitterHandler())
                .addListener(new SearchHandler())
                .addListener(new WatcherHandler())
                .addListener(new QuoteHandler())
                .addListener(new InfoHandler())
                .addListener(new MinecraftHandler())
        /*
            .down
            .help
            .qlist
            .ilist
        */

                ;
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
        System.out.println("Statements: " + preparedStatements.size());
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                instance.setKeepAlive(false);
                bot.close();
            }
        });
    }

    public static RavenBot getInstance() {
        return instance;
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
                System.out.print("Please enter the primary nickserv name of the first person with op privileges for the bot:\n> ");
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

    public PreparedStatement getPreparedStatement(String statement) throws Exception {
        if (!preparedStatements.containsKey(statement)) {
            throw new Exception("Invalid statement!");
        }
        return preparedStatements.get(statement);
    }

    public List<String> getOps() {
        return ops;
    }

    public boolean isOp(PircBotX sourceBot, User user) {
	    String nsRegistration = "";
	    if (userCache.containsKey(user.getUserId()) && userCache.get(user.getUserId()).getExpiration().after(Calendar.getInstance().getTime())) {
		    nsRegistration = userCache.get(user.getUserId()).getValue();
		    System.out.println(user.getNick() + " is cached");
	    } else {
		    System.out.println(user.getNick() + " is NOT cached");
		    user.isVerified();
		    try {
			    sourceBot.sendRaw().rawLine("WHOIS " + user.getNick() + " " + user.getNick());
			    WaitForQueue waitForQueue = new WaitForQueue(sourceBot);
			    WhoisEvent whoisEvent = waitForQueue.waitFor(WhoisEvent.class);
			    waitForQueue.close();
			    nsRegistration = whoisEvent.getRegisteredAs();
		    } catch (Exception e) {
			    e.printStackTrace();
		    }
		    if (!nsRegistration.isEmpty()) {
			    Calendar future = Calendar.getInstance();
			    future.add(Calendar.MINUTE,5);
			    userCache.put(user.getUserId(), new ExpiringToken(future.getTime(),nsRegistration));
			    System.out.println(user.getUserId().toString() + " added to cache: " + nsRegistration + " expires at " + future.toString());
		    }
	    }
	    return (getOps().contains(nsRegistration));
    }

	public boolean isKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}

	private class ExpiringToken
	{
		private final Date expiration;
		private final String value;

		private ExpiringToken(Date expiration, String value) {
			this.expiration = expiration;
			this.value = value;
		}

		public Date getExpiration() {
			return expiration;
		}

		public String getValue() {
			return value;
		}

	}
}
