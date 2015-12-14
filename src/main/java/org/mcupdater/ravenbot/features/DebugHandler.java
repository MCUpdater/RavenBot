package org.mcupdater.ravenbot.features;

import org.mcupdater.ravenbot.AbstractListener;
import org.pircbotx.hooks.WaitForQueue;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.WhoisEvent;

public class DebugHandler extends AbstractListener
{

	@Override
	public void handleCommand(String sender, MessageEvent event, String command, String[] args) {
		if (command.equals(".ping")) {
			event.respond("Pong!");
		}
		if (command.equals(".whoami")) {
			event.getUser().send().notice("UUID: " + event.getUser().getUserId().toString());
			event.getUser().send().notice("Verified: " + event.getUser().isVerified() + "\n");
			event.getUser().send().notice("Login: " + event.getUser().getLogin() + "\n");
			event.getUser().send().notice("Real name: " + event.getUser().getRealName() + "\n");
			event.getUser().send().notice("Hostmask: " + event.getUser().getHostmask() + "\n");
			try {
				event.getBot().sendRaw().rawLine("WHOIS " + event.getUser().getNick() + " " + event.getUser().getNick());
				WaitForQueue waitForQueue = new WaitForQueue(event.getBot());
				while (true) {
					WhoisEvent whoisEvent = waitForQueue.waitFor(WhoisEvent.class);
					if (!whoisEvent.getNick().equals(event.getUser().getNick()))
						continue;
					 waitForQueue.close();
					event.getUser().send().notice("Registered as: " + whoisEvent.getRegisteredAs());
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void initCommands() {

	}

}
