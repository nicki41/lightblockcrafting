package net.lightblockcrafting.telemetry;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.HexFormat;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * Sends a small, anonymous heartbeat to the official nicki41-telemetry
 * instance: plugin id, a random installation id, versions, and player
 * counts - nothing that identifies this server. {@code telemetry.enabled}
 * in config.yml is the only switch; the endpoint itself is fixed here, not
 * configurable.
 */
public final class TelemetryReporter {

    private static final URI ENDPOINT = URI.create("https://telemetry.0nicki.de/v1/telemetry");
    private static final String PLUGIN_ID = "lightblockcrafting";
    private static final long INTERVAL_TICKS = 30L * 60 * 20;
    private static final long INITIAL_DELAY_TICKS = 5L * 60 * 20;

    private final JavaPlugin plugin;
    private final SecureRandom random = new SecureRandom();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private String installationId;
    private boolean firstFailureLogged;
    private BukkitTask task;

    public TelemetryReporter(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void start() {
        if (!plugin.getConfig().getBoolean("telemetry.enabled", true)) {
            plugin.getLogger().info("Anonymous usage statistics are disabled (telemetry.enabled: false); nothing is sent.");
            return;
        }
        installationId = loadOrCreateInstallationId();
        scheduleNext(INITIAL_DELAY_TICKS + jitterTicks(INITIAL_DELAY_TICKS));
        plugin.getLogger().info("Anonymous usage statistics enabled: a heartbeat is sent to " + ENDPOINT
                + " roughly every 30 minutes. Set telemetry.enabled: false in config.yml to switch it off.");
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private void scheduleNext(long delayTicks) {
        task = Bukkit.getScheduler().runTaskLater(plugin, this::tick, Math.max(1, delayTicks));
    }

    /** Runs on the main thread: reads player counts here, sends in the background. */
    private void tick() {
        if (plugin.getConfig().getBoolean("telemetry.enabled", true)) {
            String json = buildPayload(Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers());
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> send(json));
        }
        scheduleNext(INTERVAL_TICKS + jitterTicks(INTERVAL_TICKS));
    }

    private long jitterTicks(long baseTicks) {
        return (long) (random.nextDouble() * (baseTicks / 2.0));
    }

    private void send(String json) {
        try {
            HttpRequest request = HttpRequest.newBuilder(ENDPOINT)
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();
            httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (IOException | InterruptedException exception) {
            if (exception instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            if (!firstFailureLogged) {
                firstFailureLogged = true;
                plugin.getLogger().info("Could not reach the usage-statistics endpoint; will keep trying silently.");
            }
        }
    }

    /** No JSON library is bundled with this plugin, so the fixed, small payload is built by hand. */
    private String buildPayload(int online, int max) {
        String minecraftVersion = Bukkit.getBukkitVersion().split("-")[0];
        return "{"
                + field("pluginId", PLUGIN_ID) + ","
                + field("installationId", installationId) + ","
                + field("pluginVersion", plugin.getDescription().getVersion()) + ","
                + field("minecraftVersion", minecraftVersion) + ","
                + "\"javaMajorVersion\":" + Runtime.version().feature() + ","
                + "\"onlinePlayers\":" + online + ","
                + "\"maxPlayers\":" + max
                + "}";
    }

    private String field(String key, String value) {
        return "\"" + key + "\":\"" + escape(value) + "\"";
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String loadOrCreateInstallationId() {
        Path file = plugin.getDataFolder().toPath().resolve("installation-id.txt");
        try {
            if (Files.exists(file)) {
                String existing = Files.readString(file, StandardCharsets.UTF_8).trim();
                if (existing.matches("[0-9a-f]{32}")) {
                    return existing;
                }
            }
            String id = newId();
            Files.createDirectories(file.getParent());
            Files.writeString(file, id, StandardCharsets.UTF_8);
            return id;
        } catch (IOException exception) {
            plugin.getLogger().warning("Could not persist installation-id.txt; a new id will be generated next start.");
            return newId();
        }
    }

    private String newId() {
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }
}
