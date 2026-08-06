package dev.gungoguma.annoymous;

import org.bukkit.plugin.java.JavaPlugin;

public final class GsmAnnoymous extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("GSM-Annoymous enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("GSM-Annoymous disabled.");
    }
}
