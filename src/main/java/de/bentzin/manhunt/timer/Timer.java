package de.bentzin.manhunt.timer;


import de.bentzin.manhunt.Manhunt;
import de.bentzin.manhunt.ManhuntGame;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

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
                duration_buffer = duration;
            } else {
                if(paused) {
                    start = System.currentTimeMillis();
                    paused = false;
                    continue;
                }
                ManhuntGame game = Manhunt.getInstance().getGame();
                final String time = getFormat();
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    if (game.getRunners().contains(onlinePlayer.getUniqueId())) {
                        onlinePlayer.sendActionBar(MiniMessage.miniMessage().deserialize("<green><time>", Placeholder.unparsed("time", time)));
                    } else if (game.getHunters().contains(onlinePlayer.getUniqueId())) {
                        onlinePlayer.sendActionBar(MiniMessage.miniMessage().deserialize("<color:#ff5e64><time>", Placeholder.unparsed("time", time)));
                    } else {
                        onlinePlayer.sendActionBar(MiniMessage.miniMessage().deserialize("<gray><time>", Placeholder.unparsed("time", time)));
                    }
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
