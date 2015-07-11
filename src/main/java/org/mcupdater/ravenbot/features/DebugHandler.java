package org.mcupdater.ravenbot.features;

import org.mcupdater.ravenbot.RavenBot;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

public class DebugHandler extends ListenerAdapter {

    @Override
    public void onMessage(final MessageEvent event) {
        if (event.getMessage().startsWith(".ping")) {
            event.respond("Pong!");
        }
    }

    @Override
    public void onPrivateMessage(final PrivateMessageEvent event) {
        if (RavenBot.getInstance().getOps().contains(event.getUser().getNick())) {
            event.respond("I obey!");

        } else {
            event.respond("You are not my master!");
        }
    }

}
