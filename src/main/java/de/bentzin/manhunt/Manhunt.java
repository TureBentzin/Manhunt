package de.bentzin.manhunt;

import org.bukkit.plugin.java.JavaPlugin;

public final class Manhunt extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().warning("Paper-Plugin seems to work fine!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
