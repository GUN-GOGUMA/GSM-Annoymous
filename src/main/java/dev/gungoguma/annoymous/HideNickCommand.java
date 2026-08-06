package dev.gungoguma.annoymous;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class HideNickCommand implements CommandExecutor {
    private final NicknameService nicknameService;
    private final PlayerPrivacyRepository repository;

    public HideNickCommand(NicknameService nicknameService, PlayerPrivacyRepository repository) {
        this.nicknameService = nicknameService;
        this.repository = repository;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("플레이어만 사용할 수 있는 명령어입니다.", NamedTextColor.RED));
            return true;
        }

        PlayerPrivacyData data = nicknameService.prepare(player);
        data.setHideNick(!data.isHideNick());
        nicknameService.apply(player, data);
        repository.save(data);

        if (data.isHideNick()) {
            player.sendMessage(Component.text(
                    "닉네임 익명이 켜졌습니다. 현재 닉네임: " + data.getAnonymousName(),
                    NamedTextColor.GREEN
            ));
        } else {
            player.sendMessage(Component.text(
                    "닉네임 익명이 꺼졌습니다. 현재 닉네임: " + player.getName(),
                    NamedTextColor.YELLOW
            ));
        }
        return true;
    }
}
