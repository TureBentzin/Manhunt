package de.bentzin.manhunt.timer;


import de.bentzin.manhunt.Manhunt;
import de.bentzin.manhunt.ManhuntGame;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.logging.Logger;

/**
 * @author Ture Bentzin
 * @since 26-08-2024
 */
public class Timer extends BukkitRunnable {

    private @NotNull Logger logger = Manhunt.getInstance().getLogger();

    private long duration = 0;
    private long start = 0;
    private long duration_buffer = 0;
    private boolean paused = false;

    @Override
    public void run() {
        //calculate
        start = System.currentTimeMillis();
        while (!isCancelled()) {
            long now = System.currentTimeMillis();
            duration = (now - start) + duration_buffer;
            if (Manhunt.getInstance().getGame().isPaused() && !paused) {
                paused = true;
                logger.info("Timer was paused!");
                duration_buffer = duration;
            } else {
                if (paused) {
                    start = System.currentTimeMillis();
                    paused = false;
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.clearActivePotionEffects();
                        player.setAllowFlight(false);
                        player.setFlying(false);
                        player.setWalkSpeed(0.2f);
                        player.setHealth(20);
                        player.setFoodLevel(20);
                        player.setFallDistance(0);
                        player.setFireTicks(0);
                    }
                }
            }

            Location spawn = Bukkit.getWorld("world").getSpawnLocation();
            ManhuntGame game = Manhunt.getInstance().getGame();
            final String time = getFormat();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (game.getRunners().contains(onlinePlayer.getUniqueId())) {
                    onlinePlayer.sendActionBar(MiniMessage.miniMessage().deserialize("<green><time>", Placeholder.unparsed("time", time)));
                    if(paused)
                    {
                        blockPlayer(spawn, onlinePlayer);
                    }
                } else if (game.getHunters().contains(onlinePlayer.getUniqueId())) {
                    onlinePlayer.sendActionBar(MiniMessage.miniMessage().deserialize("<color:#ff5e64><time>", Placeholder.unparsed("time", time)));
                    if(paused || getTimer() < Duration.of(10, ChronoUnit.MINUTES).toMillis()) //10 minutes
                    {
                        blockPlayer(spawn, onlinePlayer);
                    }
                } else {
                    onlinePlayer.sendActionBar(MiniMessage.miniMessage().deserialize("<gray><time>", Placeholder.unparsed("time", time)));
                    Bukkit.getScheduler().runTask(Manhunt.getInstance(), () -> {
                        onlinePlayer.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 1, 10));
                    });
                }
            }


            try {
                Thread.sleep(20);
            } catch (InterruptedException ignored) {
                logger.warning("Timer was forced to update!");
            }
        }
        logger.info("Timer Thread was killed!");
    }

    private void blockPlayer(Location spawn, Player onlinePlayer) {
        Bukkit.getScheduler().runTask(Manhunt.getInstance(), () -> {
            onlinePlayer.teleport(spawn);
            onlinePlayer.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 2, 10));
            onlinePlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 2, 5));
            onlinePlayer.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 2, 5));
        });
    }

    public @NotNull String getFormat() {
        return DurationFormatUtils.formatDuration(duration, "HH : mm : ss");
    }

    public void modify(long mod) {
        duration_buffer += mod;
    }

    /**
     * When paused or not started this is not accurate!
     */
    public long getTimer() {
        return duration;
    }

    public void setTimer(long duration) {
        this.duration = duration;
        this.duration_buffer = 0;
        this.start = System.currentTimeMillis() - duration;
    }


}
