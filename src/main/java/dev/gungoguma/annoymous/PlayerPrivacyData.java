package dev.gungoguma.annoymous;

import java.time.Instant;
import java.util.UUID;

public final class PlayerPrivacyData {
    private final UUID uuid;
    private boolean hideNick;
    private boolean hideSkin;
    private String anonymousName;
    private String lastKnownName;
    private Instant updatedAt;

    public PlayerPrivacyData(
            UUID uuid,
            boolean hideNick,
            boolean hideSkin,
            String anonymousName,
            String lastKnownName,
            Instant updatedAt
    ) {
        this.uuid = uuid;
        this.hideNick = hideNick;
        this.hideSkin = hideSkin;
        this.anonymousName = anonymousName;
        this.lastKnownName = lastKnownName;
        this.updatedAt = updatedAt;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isHideNick() {
        return hideNick;
    }

    public void setHideNick(boolean hideNick) {
        this.hideNick = hideNick;
        touch();
    }

    public boolean isHideSkin() {
        return hideSkin;
    }

    public void setHideSkin(boolean hideSkin) {
        this.hideSkin = hideSkin;
        touch();
    }

    public String getAnonymousName() {
        return anonymousName;
    }

    public void setAnonymousName(String anonymousName) {
        this.anonymousName = anonymousName;
        touch();
    }

    public String getLastKnownName() {
        return lastKnownName;
    }

    public void setLastKnownName(String lastKnownName) {
        this.lastKnownName = lastKnownName;
        touch();
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void touch() {
        updatedAt = Instant.now();
    }
}
