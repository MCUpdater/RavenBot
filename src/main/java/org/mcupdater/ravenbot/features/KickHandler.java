package org.mcupdater.ravenbot.features;

import org.mcupdater.ravenbot.AbstractListener;
import org.mcupdater.ravenbot.RavenBot;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KickHandler extends ListenerAdapter
{
    private final List<String> responses = new ArrayList<>();

    private void initResponses() {
        responses.add("Meh...  I didn't like them anyway. :P");
        responses.add("That'll teach them!");
        responses.add("... and don't come back!");
        responses.add("It's about time!");
        responses.add("Somebody's in trouble!");
    }

    public KickHandler() {
        initResponses();
    }

    private String getResponse() {
        Random rng = new Random();
        return responses.get(rng.nextInt(responses.size()));
    }

    @Override
    public void onKick(final KickEvent event) {
        RavenBot.getInstance().sendMessage(event.getChannel().getName(), getResponse());
    }
}
