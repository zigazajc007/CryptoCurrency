package com.rabbitcompany;

import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class UpdateChecker {

	private final CryptoCurrency plugin;
	private final int resourceId;

	public UpdateChecker(CryptoCurrency plugin, int resourceId) {
		this.plugin = plugin;
		this.resourceId = resourceId;
	}

	public void getVersion(final Consumer<String> consumer) {
		Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
			try (InputStream inputStream = new URL("https://api.polymart.org/v1/getResourceInfoSimple?resource_id=" + this.resourceId + "&key=version").openStream(); Scanner scanner = new Scanner(inputStream)) {
				if (scanner.hasNext()) consumer.accept(scanner.next());
			} catch (IOException exception) {
				this.plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
			}
		});
	}

}
