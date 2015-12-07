import org.mcupdater.ravenbot.util.ServerPing;

import java.io.IOException;
import java.net.InetSocketAddress;

public class CheckServer {

	public static void main(String[] args) {
		ServerPing test = new ServerPing();
		test.setAddress(new InetSocketAddress(args[0], Integer.parseInt(args[1])));
		try {
			ServerPing.StatusResponse status = test.fetchData();
			System.out.println(status.getDescription() + " (" + status.getPlayers().getOnline() + "/" + status.getPlayers().getMax() + ")");
			if (status.getModinfo() != null) {
				System.out.println("Mod list:");
				for (ServerPing.ModData mod : status.getModinfo().getModList()) {
					System.out.println("   " + mod.getModid() + ": " + mod.getVersion());
				}
			} else {
				System.out.println("Unmodded server");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

