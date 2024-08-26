package de.bentzin.manhunt;

import de.bentzin.manhunt.command.TimerCommand;
import de.bentzin.manhunt.timer.Timer;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class Manhunt extends JavaPlugin {

    public static @NotNull Manhunt getInstance() {
        return getPlugin(Manhunt.class);
    }

    private @NotNull ManhuntGame game;
    private @NotNull Timer timer;

    @Override
    public void onEnable() {
        // Plugin startup logic
        game = new ManhuntGame();
        timer = new Timer();

        LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register("timer", "manage the timer and the game", new TimerCommand());
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public @NotNull ManhuntGame getGame() {
        return game;
    }

    public @NotNull Timer getTimer() {
        return timer;
    }
}
