package dev.gungoguma.annoymous;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class HideSkinCommand implements CommandExecutor {
    private final NicknameService nicknameService;
    private final SkinService skinService;
    private final PlayerPrivacyRepository repository;

    public HideSkinCommand(
            NicknameService nicknameService,
            SkinService skinService,
            PlayerPrivacyRepository repository
    ) {
        this.nicknameService = nicknameService;
        this.skinService = skinService;
        this.repository = repository;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("플레이어만 사용할 수 있는 명령어입니다.", NamedTextColor.RED));
            return true;
        }

        PlayerPrivacyData data = nicknameService.prepare(player);
        skinService.rememberRealSkin(player);
        data.setHideSkin(!data.isHideSkin());
        if (data.isHideSkin()) {
            skinService.applyAnonymous(player);
        } else {
            skinService.restore(player);
        }
        repository.save(data);

        if (data.isHideSkin()) {
            player.sendMessage(Component.text("스킨 익명이 켜졌습니다.", NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("스킨 익명이 꺼졌습니다. 실제 스킨으로 복구했습니다.", NamedTextColor.YELLOW));
        }
        return true;
    }
}
