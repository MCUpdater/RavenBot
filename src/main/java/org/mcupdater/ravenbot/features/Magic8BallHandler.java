package org.mcupdater.ravenbot.features;

import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Magic8BallHandler extends ListenerAdapter<PircBotX> {
    private final List<String> responses = new ArrayList<>();

    private void initResponses() {
        responses.add(Colors.GREEN + " Signs point to yes.");
        responses.add(Colors.GREEN + " Yes.");
        responses.add(Colors.YELLOW + " Reply hazy, try again.");
        responses.add(Colors.GREEN + " Without a doubt.");
        responses.add(Colors.RED + " My sources say no.");
        responses.add(Colors.GREEN + " As I see it, yes.");
        responses.add(Colors.GREEN + " You may rely on it.");
        responses.add(Colors.YELLOW + " Concentrate and ask again.");
        responses.add(Colors.RED + " Outlook not so good.");
        responses.add(Colors.GREEN + " It is decidedly so.");
        responses.add(Colors.YELLOW + " Better not tell you now.");
        responses.add(Colors.RED + " Very doubtful.");
        responses.add(Colors.GREEN + " Yes - definitely.");
        responses.add(Colors.GREEN + " It is certain.");
        responses.add(Colors.YELLOW + " Cannot predict now.");
        responses.add(Colors.GREEN + " Most likely.");
        responses.add(Colors.YELLOW + " Ask again later.");
        responses.add(Colors.RED + " My reply is no.");
        responses.add(Colors.GREEN + " Outlook good.");
        responses.add(Colors.RED + " Do not count on it.");
    }

    public Magic8BallHandler() {
        initResponses();
    }

    private String getResponse() {
        Random rng = new Random();
        return responses.get(rng.nextInt(responses.size()));
    }

    @Override
    public void onGenericMessage(final GenericMessageEvent event) {
        if (event.getMessage().startsWith(".8ball")) {
            event.respond(getResponse());
        }
    }
}
