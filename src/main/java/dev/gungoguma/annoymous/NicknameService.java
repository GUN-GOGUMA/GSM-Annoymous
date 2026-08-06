package dev.gungoguma.annoymous;

import org.bukkit.entity.Player;

public final class NicknameService {
    private final GsmAnnoymous plugin;
    private final PlayerPrivacyRepository repository;
    private final AnonymousNameGenerator generator;

    public NicknameService(
            GsmAnnoymous plugin,
            PlayerPrivacyRepository repository,
            AnonymousNameGenerator generator
    ) {
        this.plugin = plugin;
        this.repository = repository;
        this.generator = generator;
    }

    public PlayerPrivacyData prepare(Player player) {
        PlayerPrivacyData data = repository.getOrCreate(player.getUniqueId(), player.getName());
        if (data.getAnonymousName() == null || data.getAnonymousName().isBlank()) {
            data.setAnonymousName(generator.generate(
                    player.getUniqueId(),
                    repository.getAnonymousNamesExcept(player.getUniqueId())
            ));
        }
        repository.save(data);
        return data;
    }

    public void apply(Player player, PlayerPrivacyData data) {
        String displayName = data.isHideNick() ? data.getAnonymousName() : player.getName();

        if (plugin.getConfig().getBoolean("display.updateDisplayName", true)) {
            player.setDisplayName(displayName);
        }

        if (plugin.getConfig().getBoolean("display.updatePlayerListName", true)) {
            player.setPlayerListName(displayName);
        }
    }

    public String getVisibleName(PlayerPrivacyData data) {
        return data.isHideNick() ? data.getAnonymousName() : data.getLastKnownName();
    }
}
