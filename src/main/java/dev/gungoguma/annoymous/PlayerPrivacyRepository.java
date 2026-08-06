package dev.gungoguma.annoymous;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class PlayerPrivacyRepository {
    private final JavaPlugin plugin;
    private final File playersFile;
    private final Map<UUID, PlayerPrivacyData> players = new HashMap<>();
    private YamlConfiguration configuration;

    public PlayerPrivacyRepository(JavaPlugin plugin) {
        this.plugin = plugin;
        String fileName = plugin.getConfig().getString("storage.playersFile", "players.yml");
        this.playersFile = new File(plugin.getDataFolder(), fileName);
    }

    public void load() {
        ensureDataFolder();
        configuration = YamlConfiguration.loadConfiguration(playersFile);
        players.clear();

        ConfigurationSection playersSection = configuration.getConfigurationSection("players");
        if (playersSection == null) {
            return;
        }

        for (String key : playersSection.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                ConfigurationSection section = playersSection.getConfigurationSection(key);
                if (section == null) {
                    continue;
                }
                players.put(uuid, readPlayer(uuid, section));
            } catch (IllegalArgumentException exception) {
                plugin.getLogger().warning("Invalid UUID in players.yml: " + key);
            }
        }
    }

    public PlayerPrivacyData getOrCreate(UUID uuid, String playerName) {
        PlayerPrivacyData data = players.get(uuid);
        if (data != null) {
            data.setLastKnownName(playerName);
            return data;
        }

        data = new PlayerPrivacyData(
                uuid,
                plugin.getConfig().getBoolean("defaults.hideNick", true),
                plugin.getConfig().getBoolean("defaults.hideSkin", true),
                null,
                playerName,
                Instant.now()
        );
        players.put(uuid, data);
        return data;
    }

    public PlayerPrivacyData get(UUID uuid) {
        return players.get(uuid);
    }

    public void remove(UUID uuid) {
        players.remove(uuid);
        if (configuration != null) {
            configuration.set("players." + uuid, null);
            saveFile();
        }
    }

    public Set<String> getAnonymousNamesExcept(UUID uuid) {
        Set<String> names = new HashSet<>();
        for (PlayerPrivacyData data : players.values()) {
            if (!data.getUuid().equals(uuid) && data.getAnonymousName() != null) {
                names.add(data.getAnonymousName());
            }
        }
        return names;
    }

    public void save(PlayerPrivacyData data) {
        if (configuration == null) {
            configuration = new YamlConfiguration();
        }

        String path = "players." + data.getUuid();
        configuration.set(path + ".hideNick", data.isHideNick());
        configuration.set(path + ".hideSkin", data.isHideSkin());
        configuration.set(path + ".anonymousName", data.getAnonymousName());
        configuration.set(path + ".lastKnownName", data.getLastKnownName());
        configuration.set(path + ".updatedAt", data.getUpdatedAt().toString());
        saveFile();
    }

    public void saveAll() {
        for (PlayerPrivacyData data : players.values()) {
            String path = "players." + data.getUuid();
            configuration.set(path + ".hideNick", data.isHideNick());
            configuration.set(path + ".hideSkin", data.isHideSkin());
            configuration.set(path + ".anonymousName", data.getAnonymousName());
            configuration.set(path + ".lastKnownName", data.getLastKnownName());
            configuration.set(path + ".updatedAt", data.getUpdatedAt().toString());
        }
        saveFile();
    }

    private PlayerPrivacyData readPlayer(UUID uuid, ConfigurationSection section) {
        return new PlayerPrivacyData(
                uuid,
                section.getBoolean("hideNick", plugin.getConfig().getBoolean("defaults.hideNick", true)),
                section.getBoolean("hideSkin", plugin.getConfig().getBoolean("defaults.hideSkin", true)),
                section.getString("anonymousName"),
                section.getString("lastKnownName", ""),
                readInstant(section.getString("updatedAt"))
        );
    }

    private Instant readInstant(String value) {
        if (value == null || value.isBlank()) {
            return Instant.now();
        }

        try {
            return Instant.parse(value);
        } catch (DateTimeParseException exception) {
            return Instant.now();
        }
    }

    private void ensureDataFolder() {
        if (!plugin.getDataFolder().exists() && !plugin.getDataFolder().mkdirs()) {
            plugin.getLogger().warning("Failed to create plugin data folder.");
        }
    }

    private void saveFile() {
        ensureDataFolder();
        try {
            configuration.save(playersFile);
        } catch (IOException exception) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save players.yml.", exception);
        }
    }
}
