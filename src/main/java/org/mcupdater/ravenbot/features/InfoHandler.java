package org.mcupdater.ravenbot.features;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

public class InfoHandler extends ListenerAdapter {

    @Override
    public void onMessage(final MessageEvent event) {
        if (event.getMessage().startsWith(".info")) {
            String[] splitMessage = event.getMessage().split(" ");
            if (splitMessage.length == 1) {

            }
        }
    }
}
