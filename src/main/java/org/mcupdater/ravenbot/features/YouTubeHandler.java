package org.mcupdater.ravenbot.features;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import org.apache.commons.lang3.StringUtils;
import org.mcupdater.ravenbot.AbstractListener;
import org.mcupdater.ravenbot.SettingsManager;
import org.pircbotx.Colors;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.IOException;
import java.util.List;

public class YouTubeHandler extends AbstractListener
{
	private static YouTube youtube;

	public YouTubeHandler() {
		if (youtube == null) {
				youtube = new YouTube.Builder(YTAuth.HTTP_TRANSPORT, YTAuth.JSON_FACTORY, new HttpRequestInitializer()
				{
					@Override
					public void initialize(HttpRequest request) throws IOException {
					}
				}).setApplicationName("ravenbot").build();
		}
	}

	@Override
	protected void initCommands() {

	}

	@Override
	public void handleCommand(String sender, MessageEvent event, String command, String[] args) {
		if (command.equals(".youtube") || command.equals(".yt")) {
			// Perform search
			String query = StringUtils.join(args, " ");
			try {
				YouTube.Search.List search = youtube.search().list("id,snippet");
				search.setKey(SettingsManager.getInstance().getSettings().getYoutubeKey());
				search.setQ(query);
				search.setMaxResults(1L);
				search.setType("video");

				SearchListResponse searchListResponse = search.execute();
				List<SearchResult> searchResults = searchListResponse.getItems();
				if (searchResults != null) {
					SearchResult firstResult = searchResults.get(0);
					event.getBot().sendIRC().message(event.getChannel().getName(), formatSearchResult(firstResult));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onMessage(final MessageEvent event) {
		PircBotX bot = event.getBot();
		String[] splitMessage = event.getMessage().split(" ");
		for (String aSplitMessage : splitMessage) {
			if (aSplitMessage.contains("youtube.com") && aSplitMessage.contains("v=")) {
				int index = aSplitMessage.lastIndexOf("v=") + 2;
				String videoId = (aSplitMessage.indexOf("&",index) > -1 ? aSplitMessage.substring(index, aSplitMessage.indexOf("&",index)) : aSplitMessage.substring(index));
				System.out.println(videoId);
				try {
					YouTube.Videos.List videos = youtube.videos().list("id,snippet,contentDetails");
					videos.setKey(SettingsManager.getInstance().getSettings().getYoutubeKey());
					videos.setId(videoId);

					VideoListResponse videoListResponse = videos.execute();
					List<Video> videoList = videoListResponse.getItems();
					if (videoList != null) {
						Video firstResult = videoList.get(0);
						bot.sendIRC().message(event.getChannel().getName(), formatResult(firstResult));
					} else {
						bot.sendIRC().message(event.getChannel().getName(), "Could not find information about that video");
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private String formatSearchResult(SearchResult result) {
		StringBuilder response = new StringBuilder();
		response.append(Colors.BOLD).append("Title: ").append(Colors.NORMAL).append(result.getSnippet().getTitle()).append(" ");
		response.append(Colors.BOLD).append("Channel: ").append(Colors.NORMAL).append(result.getSnippet().getChannelTitle()).append(" ");
		response.append(Colors.BOLD).append("URL: ").append(Colors.NORMAL).append("https://www.youtube.com/watch?v=").append(result.getId().getVideoId());
		return response.toString();
	}

	private String formatResult(Video result) {
		StringBuilder response = new StringBuilder();
		response.append(Colors.BOLD).append("Title: ").append(Colors.NORMAL).append(result.getSnippet().getTitle()).append(" ");
		response.append(Colors.BOLD).append("Length: ").append(Colors.NORMAL).append(result.getContentDetails().getDuration().substring(2)).append(" ");
		response.append(Colors.BOLD).append("Channel: ").append(Colors.NORMAL).append(result.getSnippet().getChannelTitle());
		return response.toString();
	}
}

