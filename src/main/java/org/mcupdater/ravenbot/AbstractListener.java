package org.mcupdater.ravenbot;

import org.pircbotx.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractListener extends ListenerAdapter
{
	// key = command name, value = description
	private final Map<String, String> commands = new HashMap<>();

	public AbstractListener() {
		initCommands();
	}

	protected abstract void initCommands();

	public Map<String, String> getCommands() {
		return commands;
	}
}
