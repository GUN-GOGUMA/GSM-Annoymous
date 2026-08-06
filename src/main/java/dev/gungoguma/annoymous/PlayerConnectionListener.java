package dev.gungoguma.annoymous;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class PlayerConnectionListener implements Listener {
    private final NicknameService nicknameService;
    private final SkinService skinService;

    public PlayerConnectionListener(NicknameService nicknameService, SkinService skinService) {
        this.nicknameService = nicknameService;
        this.skinService = skinService;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerPrivacyData data = nicknameService.prepare(event.getPlayer());
        nicknameService.apply(event.getPlayer(), data);
        skinService.apply(event.getPlayer(), data);

        if (event.joinMessage() != null) {
            event.joinMessage(event.joinMessage().replaceText(builder -> builder
                    .matchLiteral(event.getPlayer().getName())
                    .replacement(nicknameService.getVisibleName(data))));
        }
    }
}
