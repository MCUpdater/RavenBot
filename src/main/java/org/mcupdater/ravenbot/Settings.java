package org.mcupdater.ravenbot;

@SuppressWarnings("SameParameterValue")
public class Settings {
    private String nick;
    private String login;
    private String realName;
    private String server;
    private int port;
    private String nsPassword;
    private String twitCKey;
    private String twitCSecret;
    private String twitToken;
    private String twitTSecret;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getNsPassword() {
        return nsPassword;
    }

    public void setNsPassword(String nsPassword) {
        this.nsPassword = nsPassword;
    }

    public String getTwitCKey() {
        return twitCKey;
    }

    public void setTwitCKey(String twitCKey) {
        this.twitCKey = twitCKey;
    }

    public String getTwitCSecret() {
        return twitCSecret;
    }

    public void setTwitCSecret(String twitCSecret) {
        this.twitCSecret = twitCSecret;
    }

    public String getTwitToken() {
        return twitToken;
    }

    public void setTwitToken(String twitToken) {
        this.twitToken = twitToken;
    }

    public String getTwitTSecret() {
        return twitTSecret;
    }

    public void setTwitTSecret(String twitTSecret) {
        this.twitTSecret = twitTSecret;
    }
}
