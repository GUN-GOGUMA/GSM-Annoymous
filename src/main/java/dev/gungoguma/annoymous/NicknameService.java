package dev.gungoguma.annoymous;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public final class NicknameService {
    private static final String NAMETAG_TEAM_PREFIX = "gsm_anon_";
    private final GsmAnnoymous plugin;
    private final PlayerPrivacyRepository repository;
    private AnonymousNameGenerator generator;

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

    public PlayerPrivacyData getPrepared(Player player) {
        return repository.get(player.getUniqueId());
    }

    public void setGenerator(AnonymousNameGenerator generator) {
        this.generator = generator;
    }

    public void apply(Player player, PlayerPrivacyData data) {
        String displayName = data.isHideNick() ? data.getAnonymousName() : player.getName();
        Component displayNameComponent = Component.text(displayName);

        if (plugin.getConfig().getBoolean("display.updateDisplayName", true)) {
            player.displayName(displayNameComponent);
        }

        if (plugin.getConfig().getBoolean("display.updatePlayerListName", true)) {
            player.playerListName(displayNameComponent);
        }

        if (plugin.getConfig().getBoolean("display.updateNameTag", true)) {
            if (data.isHideNick()) {
                player.customName(displayNameComponent);
                player.setCustomNameVisible(true);
                hideRealNameTag(player);
            } else {
                player.customName(null);
                player.setCustomNameVisible(false);
                showRealNameTag(player);
            }
        }
    }

    public String getVisibleName(PlayerPrivacyData data) {
        return data.isHideNick() ? data.getAnonymousName() : data.getLastKnownName();
    }

    public void cleanupNameTags() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        for (Team team : scoreboard.getTeams()) {
            if (team.getName().startsWith(NAMETAG_TEAM_PREFIX)) {
                team.unregister();
            }
        }
    }

    private void hideRealNameTag(Player player) {
        Team team = nameTagTeam(player);
        team.addEntry(player.getName());
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
    }

    private void showRealNameTag(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam(teamName(player));
        if (team == null) {
            return;
        }

        team.removeEntry(player.getName());
        if (team.getEntries().isEmpty()) {
            team.unregister();
        }
    }

    private Team nameTagTeam(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        String teamName = teamName(player);
        Team team = scoreboard.getTeam(teamName);
        if (team != null) {
            return team;
        }

        return scoreboard.registerNewTeam(teamName);
    }

    private String teamName(Player player) {
        String compactUuid = player.getUniqueId().toString().replace("-", "");
        return NAMETAG_TEAM_PREFIX + compactUuid.substring(0, 7);
    }
}
