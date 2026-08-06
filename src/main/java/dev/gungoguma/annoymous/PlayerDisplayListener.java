package dev.gungoguma.annoymous;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class PlayerDisplayListener implements Listener {
    private final GsmAnnoymous plugin;
    private final NicknameService nicknameService;

    public PlayerDisplayListener(GsmAnnoymous plugin, NicknameService nicknameService) {
        this.plugin = plugin;
        this.nicknameService = nicknameService;
    }

    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        if (!plugin.getConfig().getBoolean("display.updateChatName", true)) {
            return;
        }

        PlayerPrivacyData data = nicknameService.prepare(event.getPlayer());
        event.renderer((source, sourceDisplayName, message, viewer) ->
                Component.text("<")
                        .append(Component.text(nicknameService.getVisibleName(data)))
                        .append(Component.text("> "))
                        .append(message)
        );
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!plugin.getConfig().getBoolean("display.updateJoinQuitMessage", true)) {
            return;
        }

        event.quitMessage(replaceRealName(event.quitMessage(), event.getPlayer()));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!plugin.getConfig().getBoolean("display.updateDeathMessage", true)) {
            return;
        }

        event.deathMessage(replaceRealName(event.deathMessage(), event.getPlayer()));
    }

    private Component replaceRealName(Component message, Player player) {
        if (message == null) {
            return null;
        }

        PlayerPrivacyData data = nicknameService.prepare(player);
        return message.replaceText(builder -> builder
                .matchLiteral(player.getName())
                .replacement(nicknameService.getVisibleName(data)));
    }
}
