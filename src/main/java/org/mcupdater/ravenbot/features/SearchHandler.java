package org.mcupdater.ravenbot.features;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class SearchHandler extends ListenerAdapter<PircBotX> {

	@Override
	public void onMessage(final MessageEvent event) {
		String filter = null;
		String[] splitMessage = event.getMessage().split(" ");
		switch (splitMessage[0]) {
			case ".google":
			case ".g":
				break;
			case ".curseforge":
			case ".cf":
				filter = "site:minecraft.curseforge.com";
				break;
			case ".wiki":
				filter = "wiki";
				break;
			case ".urban":
				filter = "site:urbandictionary.com";
				break;
			case ".devbukkit":
			case ".db":
				filter = "site:dev.bukkit.org";
				break;
			case ".ann":
				filter = "site:animenewsnetwork.com";
				break;

			default:
				return;
		}
		event.respond(performSearch(filter, StringUtils.join(splitMessage, " ", 1, splitMessage.length)));
	}

	private String performSearch(String filter, String terms) {
		try {
			StringBuilder searchURLString = new StringBuilder();
			searchURLString.append("https://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=");
			if (filter != null) {
				searchURLString.append(filter).append("+");
			}
			searchURLString.append(terms.replace(" ", "+"));
			URL searchURL = new URL(searchURLString.toString());
			URLConnection conn = searchURL.openConnection();
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:39.0) Gecko/20100101 RavenBot/2.0");
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder json = new StringBuilder();
			String line;
			while ((line = in.readLine()) != null) {
				json.append(line).append("\n");
			}
			in.close();
			JsonElement element = new JsonParser().parse(json.toString());
			JsonObject output = element.getAsJsonObject().getAsJsonObject("responseData").getAsJsonArray("results").get(0).getAsJsonObject();
			String title = StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml4(output.get("titleNoFormatting").toString().replaceAll("\"", "")));
			String content = StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml4(output.get("content").toString().replaceAll("\\s+", " ").replaceAll("\\<.*?>", "").replaceAll("\"", "")));
			String url = StringEscapeUtils.unescapeJava(output.get("url").toString().replaceAll("\"", ""));
			return url + " - " + Colors.BOLD + title + Colors.NORMAL + ": \"" + content + "\"";
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}