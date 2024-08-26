package de.bentzin.manhunt;

import de.bentzin.manhunt.timer.Timer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.*;


public class ManhuntGame {

    private final @NotNull Logger logger = Manhunt.getInstance().getLogger();

    private boolean defeated;
    private boolean started;
    private boolean running;

    private @Nullable Timer timer;

    private @NotNull ArrayList<UUID> runners;
    private @NotNull ArrayList<UUID> hunters;

    public ManhuntGame() {
        runners = new ArrayList<>();
        hunters = new ArrayList<>();
    }

    public @NotNull ArrayList<UUID> getRunners() {
        return runners;
    }

    public void setRunners(@NotNull ArrayList<UUID> runners) {
        this.runners = runners;
    }

    public @NotNull ArrayList<UUID> getHunters() {
        return hunters;
    }

    public void setHunters(@NotNull ArrayList<UUID> hunters) {
        this.hunters = hunters;
    }

    public boolean isDefeated() {
        return defeated;
    }

    public void defeat() {
        defeated = true;
        Bukkit.broadcast(MiniMessage.miniMessage().deserialize("[<red>MANHUNT</red>] <b>The Enderdragon was defeated!"));
    }

    public boolean isStarted() {
        return started;
    }

    public void start() {
        started = true;
        if(timer == null) {
            timer = new Timer();
            timer.runTaskAsynchronously(Manhunt.getInstance());
        }
        run();
    }

    public boolean isRunning() {
        if(!started) {
            return false;
        }
        return running;
    }

    public boolean isPaused() {
        return started && !running;
    }

    public void run() {
        if(!started)
        {
            throw new IllegalStateException("Cant run an unstarted game!");
        }
        running = true;
        Bukkit.broadcast(MiniMessage.miniMessage().deserialize("[<red>MANHUNT</red>] <b>The Timer was resumed!"));
    }

    public void stop() {
        if(!started) {
            logger.warning("Game was stopped using .stop(), but was never started!");
        }
        running = false;
        Bukkit.broadcast(MiniMessage.miniMessage().deserialize("[<red>MANHUNT</red>] <b>The Timer was stopped!"));
    }

    public void reset() {
        defeated = false;
        started = false;
        running = false;
        if(timer != null)
            timer.cancel();
        timer = null; //Garbage collection
        runners.clear();
        hunters.clear();
        logger.warning("Game reset successful!");
        Bukkit.broadcast(MiniMessage.miniMessage().deserialize("[<red>MANHUNT</red>] <red> The game was reset!"));
    }
}
