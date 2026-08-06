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
        anonymousNameGenerator = new AnonymousNameGenerator(
                getConfig().getString("anonymous.prefix", "gsm_"),
                getConfig().getString("anonymous.salt", ""),
                getConfig().getInt("anonymous.hashLength", 6)
        );
        playerPrivacyRepository = new PlayerPrivacyRepository(this);
        playerPrivacyRepository.load();
        nicknameService = new NicknameService(this, playerPrivacyRepository, anonymousNameGenerator);
        skinService = new SkinService(this);
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(nicknameService, skinService), this);
        Objects.requireNonNull(getCommand("hide_nick")).setExecutor(
                new HideNickCommand(nicknameService, playerPrivacyRepository)
        );
        Objects.requireNonNull(getCommand("hide_skin")).setExecutor(
                new HideSkinCommand(nicknameService, skinService, playerPrivacyRepository)
        );
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

    public NicknameService getNicknameService() {
        return nicknameService;
    }

    public SkinService getSkinService() {
        return skinService;
    }
}
