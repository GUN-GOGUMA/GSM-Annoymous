package dev.gungoguma.annoymous;

import org.bukkit.plugin.java.JavaPlugin;

public final class GsmAnnoymous extends JavaPlugin {
    private PlayerPrivacyRepository playerPrivacyRepository;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        playerPrivacyRepository = new PlayerPrivacyRepository(this);
        playerPrivacyRepository.load();
        getLogger().info("GSM-Annoymous enabled.");
    }

    @Override
    public void onDisable() {
        if (playerPrivacyRepository != null) {
            playerPrivacyRepository.saveAll();
        }
        getLogger().info("GSM-Annoymous disabled.");
    }

    public PlayerPrivacyRepository getPlayerPrivacyRepository() {
        return playerPrivacyRepository;
    }
}
