package dev.gungoguma.annoymous;

import java.util.Objects;
import org.bukkit.plugin.java.JavaPlugin;

public final class GsmAnnoymous extends JavaPlugin {
    private PlayerPrivacyRepository playerPrivacyRepository;
    private AnonymousNameGenerator anonymousNameGenerator;
    private NicknameService nicknameService;
    private SkinService skinService;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadPluginConfig();
        playerPrivacyRepository = new PlayerPrivacyRepository(this);
        playerPrivacyRepository.load();
        nicknameService = new NicknameService(this, playerPrivacyRepository, anonymousNameGenerator);
        skinService = new SkinService(this);
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(nicknameService, skinService), this);
        getServer().getPluginManager().registerEvents(new PlayerDisplayListener(this, nicknameService), this);
        Objects.requireNonNull(getCommand("hide_nick")).setExecutor(
                new HideNickCommand(nicknameService, playerPrivacyRepository)
        );
        Objects.requireNonNull(getCommand("hide_skin")).setExecutor(
                new HideSkinCommand(nicknameService, skinService, playerPrivacyRepository)
        );
        Objects.requireNonNull(getCommand("annoymous")).setExecutor(new AnnoymousAdminCommand(this));
        getServer().getOnlinePlayers().forEach(player -> nicknameService.apply(player, nicknameService.prepare(player)));
        getLogger().info("GSM-Annoymous enabled.");
    }

    public void reloadPluginConfig() {
        reloadConfig();
        String salt = getConfig().getString("anonymous.salt", "");
        if (salt == null || salt.isBlank() || "change-me".equalsIgnoreCase(salt)) {
            getLogger().warning("anonymous.salt is not set. Set a private salt in config.yml before production use.");
        }
        anonymousNameGenerator = new AnonymousNameGenerator(
                getConfig().getString("anonymous.prefix", "gsm_"),
                salt,
                getConfig().getInt("anonymous.hashLength", 6)
        );
        if (nicknameService != null) {
            nicknameService.setGenerator(anonymousNameGenerator);
        }
    }

    @Override
    public void onDisable() {
        if (playerPrivacyRepository != null) {
            playerPrivacyRepository.saveAll();
        }
        if (nicknameService != null) {
            nicknameService.cleanupNameTags();
        }
        getLogger().info("GSM-Annoymous disabled.");
    }

    public PlayerPrivacyRepository getPlayerPrivacyRepository() {
        return playerPrivacyRepository;
    }

    public AnonymousNameGenerator getAnonymousNameGenerator() {
        return anonymousNameGenerator;
    }

    public NicknameService getNicknameService() {
        return nicknameService;
    }

    public SkinService getSkinService() {
        return skinService;
    }

}
