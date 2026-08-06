package dev.gungoguma.annoymous;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.bukkit.entity.Player;

public final class SkinService {
    private static final String TEXTURES_PROPERTY = "textures";
    private static final String STEVE_TEXTURE = "ewogICJ0aW1lc3RhbXAiIDogMTYwNzY1NTE3ODc4NCwKICAicHJvZmlsZUlkIiA6ICI4NjY3YmFhNzEyNzQ0MDA0YWY1NDQ1N2E5NzM0ZWVkNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJTdGV2ZSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IGZhbHNlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjBkYzIwN2JiMDg2NjhiY2Q5ZjExNTIzODc4YzY5OWY5YjE4NDk2YTA0YjFiYWFkM2I2ODQ4NzBjN2ZkYzQifQogIH0KfQ==";

    private final GsmAnnoymous plugin;
    private final ConcurrentMap<UUID, Set<ProfileProperty>> realSkinProperties = new ConcurrentHashMap<>();

    public SkinService(GsmAnnoymous plugin) {
        this.plugin = plugin;
    }

    public void rememberRealSkin(Player player) {
        PlayerProfile profile = player.getPlayerProfile();
        Set<ProfileProperty> textures = new HashSet<>();
        for (ProfileProperty property : profile.getProperties()) {
            if (TEXTURES_PROPERTY.equals(property.getName())) {
                textures.add(property);
            }
        }
        if (!textures.isEmpty()) {
            realSkinProperties.put(player.getUniqueId(), textures);
        }
    }

    public void applyAnonymous(Player player) {
        String anonymousProfile = plugin.getConfig().getString("skin.anonymousProfile", "STEVE");
        if (!"STEVE".equalsIgnoreCase(anonymousProfile)) {
            plugin.getLogger().warning("Unsupported anonymous skin profile '" + anonymousProfile + "'. Only STEVE is supported.");
            return;
        }

        PlayerProfile profile = player.getPlayerProfile();
        profile.removeProperty(TEXTURES_PROPERTY);
        profile.setProperty(new ProfileProperty(TEXTURES_PROPERTY, STEVE_TEXTURE));
        player.setPlayerProfile(profile);
    }

    public void restore(Player player) {
        if (!realSkinProperties.containsKey(player.getUniqueId())) {
            return;
        }

        Set<ProfileProperty> textures = realSkinProperties.get(player.getUniqueId());
        if (textures == null || textures.isEmpty()) {
            plugin.getLogger().warning(
                    "Could not restore real skin for "
                            + player.getName()
                            + " ("
                            + player.getUniqueId()
                            + "): no cached texture. The player may need to reconnect."
            );
            return;
        }

        PlayerProfile profile = player.getPlayerProfile();
        profile.removeProperty(TEXTURES_PROPERTY);
        for (ProfileProperty texture : textures) {
            profile.setProperty(texture);
        }
        player.setPlayerProfile(profile);
    }
}
