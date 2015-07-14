package org.mcupdater.ravenbot.features;

import org.mcupdater.ravenbot.Settings;
import org.mcupdater.ravenbot.SettingsManager;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterHandler extends ListenerAdapter<PircBotX> {
	Twitter twitter;

	public TwitterHandler() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		Settings current = SettingsManager.getInstance().getSettings();
		cb.setDebugEnabled(true)
				.setOAuthConsumerKey(current.getTwitCKey())
				.setOAuthConsumerSecret(current.getTwitCSecret())
				.setOAuthAccessToken(current.getTwitToken())
				.setOAuthAccessTokenSecret(current.getTwitTSecret());
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
		try {
			System.out.println("Twitter User ID: " + twitter.getId());
			System.out.println("Twitter Screen Name: " + twitter.getScreenName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessage(final MessageEvent event) {
		PircBotX bot = event.getBot();
		String[] splitMessage = event.getMessage().split(" ");
		for (String aSplitMessage : splitMessage) {
			if (aSplitMessage.contains("twitter.com") && aSplitMessage.contains("/status/")) {
				int index = aSplitMessage.lastIndexOf("/") + 1;
				long status = Long.parseLong(aSplitMessage.substring(index));
				try {
					Status lookup = twitter.showStatus(status);
					bot.sendIRC().action(event.getChannel().getName(), "found " + lookup.getUser().getScreenName() + ": " + lookup.getText());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
