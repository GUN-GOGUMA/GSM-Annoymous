package dev.gungoguma.annoymous;

import java.util.Objects;
import org.bukkit.entity.Player;
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
        getLogger().info("GSM-Annoymous enabled.");
    }

    public void reloadPluginConfig() {
        reloadConfig();
        anonymousNameGenerator = new AnonymousNameGenerator(
                getConfig().getString("anonymous.prefix", "gsm_"),
                getConfig().getString("anonymous.salt", ""),
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

    public String getRealName(Player player) {
        return player.getName();
    }

    public String getAnonymousName(Player player) {
        PlayerPrivacyData data = nicknameService.prepare(player);
        return data.getAnonymousName();
    }

    public boolean isNickHidden(Player player) {
        PlayerPrivacyData data = playerPrivacyRepository.get(player.getUniqueId());
        return data != null && data.isHideNick();
    }

    public boolean isSkinHidden(Player player) {
        PlayerPrivacyData data = playerPrivacyRepository.get(player.getUniqueId());
        return data != null && data.isHideSkin();
    }

    public String getDiscordName(Player player) {
        return getConfig().getBoolean("discord.useRealName", true) ? getRealName(player) : getAnonymousName(player);
    }

    public boolean shouldDiscordUseRealSkin() {
        return getConfig().getBoolean("discord.useRealSkin", true);
    }
}
