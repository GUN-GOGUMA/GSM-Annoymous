package dev.gungoguma.annoymous;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class AnnoymousAdminCommand implements CommandExecutor {
    private final GsmAnnoymous plugin;

    public AnnoymousAdminCommand(GsmAnnoymous plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> reload(sender);
            case "status" -> status(sender, args);
            case "reset" -> reset(sender, args);
            case "regenerate" -> regenerate(sender, args);
            default -> sendUsage(sender);
        }
        return true;
    }

    private void reload(CommandSender sender) {
        if (!sender.hasPermission("gsmannoymous.admin.reload")) {
            deny(sender);
            return;
        }

        plugin.reloadPluginConfig();
        sender.sendMessage(Component.text("GSM-Annoymous 설정을 다시 불러왔습니다.", NamedTextColor.GREEN));
    }

    private void status(CommandSender sender, String[] args) {
        if (!sender.hasPermission("gsmannoymous.admin.status")) {
            deny(sender);
            return;
        }

        Player target = getOnlineTarget(sender, args);
        if (target == null) {
            return;
        }

        PlayerPrivacyData data = plugin.getNicknameService().prepare(target);
        sender.sendMessage(Component.text("UUID: " + target.getUniqueId(), NamedTextColor.GRAY));
        sender.sendMessage(Component.text("실제 닉네임: " + target.getName(), NamedTextColor.GRAY));
        sender.sendMessage(Component.text("익명 닉네임: " + data.getAnonymousName(), NamedTextColor.GRAY));
        sender.sendMessage(Component.text("닉네임 익명: " + data.isHideNick(), NamedTextColor.GRAY));
        sender.sendMessage(Component.text("스킨 익명: " + data.isHideSkin(), NamedTextColor.GRAY));
        sender.sendMessage(Component.text("마지막 갱신: " + data.getUpdatedAt(), NamedTextColor.GRAY));
    }

    private void reset(CommandSender sender, String[] args) {
        if (!sender.hasPermission("gsmannoymous.admin.reset")) {
            deny(sender);
            return;
        }

        Player target = getOnlineTarget(sender, args);
        if (target == null) {
            return;
        }

        plugin.getPlayerPrivacyRepository().remove(target.getUniqueId());
        PlayerPrivacyData data = plugin.getNicknameService().prepare(target);
        plugin.getNicknameService().apply(target, data);
        plugin.getSkinService().apply(target, data);
        sender.sendMessage(Component.text(target.getName() + "님의 익명 상태를 초기화했습니다.", NamedTextColor.GREEN));
    }

    private void regenerate(CommandSender sender, String[] args) {
        if (!sender.hasPermission("gsmannoymous.admin.reset")) {
            deny(sender);
            return;
        }

        Player target = getOnlineTarget(sender, args);
        if (target == null) {
            return;
        }

        PlayerPrivacyData data = plugin.getNicknameService().prepare(target);
        data.setAnonymousName(plugin.getAnonymousNameGenerator().generate(
                target.getUniqueId(),
                plugin.getPlayerPrivacyRepository().getAnonymousNamesExcept(target.getUniqueId())
        ));
        plugin.getNicknameService().apply(target, data);
        plugin.getPlayerPrivacyRepository().save(data);
        sender.sendMessage(Component.text(target.getName() + "님의 익명 닉네임을 재생성했습니다: " + data.getAnonymousName(), NamedTextColor.GREEN));
    }

    private Player getOnlineTarget(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("대상 플레이어를 입력해주세요.", NamedTextColor.RED));
            return null;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(args[1]);
        Player player = offlinePlayer == null ? Bukkit.getPlayerExact(args[1]) : offlinePlayer.getPlayer();
        if (player == null) {
            sender.sendMessage(Component.text("온라인 플레이어만 대상으로 사용할 수 있습니다.", NamedTextColor.RED));
        }
        return player;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(Component.text("/annoymous reload", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/annoymous status <player>", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/annoymous reset <player>", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/annoymous regenerate <player>", NamedTextColor.YELLOW));
    }

    private void deny(CommandSender sender) {
        sender.sendMessage(Component.text("권한이 없습니다.", NamedTextColor.RED));
    }
}
