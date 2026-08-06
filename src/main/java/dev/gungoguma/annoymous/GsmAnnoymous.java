package dev.gungoguma.annoymous;

import org.bukkit.plugin.java.JavaPlugin;

public final class GsmAnnoymous extends JavaPlugin {
    private PlayerPrivacyRepository playerPrivacyRepository;
    private AnonymousNameGenerator anonymousNameGenerator;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        anonymousNameGenerator = new AnonymousNameGenerator(
                getConfig().getString("anonymous.prefix", "gsm_"),
                getConfig().getString("anonymous.salt", ""),
                getConfig().getInt("anonymous.hashLength", 6)
        );
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

    public AnonymousNameGenerator getAnonymousNameGenerator() {
        return anonymousNameGenerator;
    }
}
