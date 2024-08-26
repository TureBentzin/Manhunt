package de.bentzin.manhunt.command;


import de.bentzin.manhunt.Manhunt;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.UUID;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.*;
import static net.kyori.adventure.text.minimessage.tag.resolver.TagResolver.*;

/**
 * @author Ture Bentzin
 * @since 26-08-2024
 */
public class CompassCommand implements BasicCommand {

    private @NotNull HashMap<UUID, Long> cooldowns = new HashMap<>();

    private boolean isReady(@NotNull UUID id) {
        if (cooldowns.containsKey(id)) {
            long time = cooldowns.get(id);
            long now = System.currentTimeMillis();
            if (now - time > Duration.of(1, ChronoUnit.MINUTES).toMillis()) {
                cooldowns.put(id, now);
                return true;
            } else {
                return false;
            }
        } else {
            cooldowns.put(id, System.currentTimeMillis());
            return true;
        }
    }

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] args) {
        if (args.length == 0) {
            commandSourceStack.getSender().sendMessage(miniMessage().deserialize("<red>Usage: /compass <player>"));
        } else if (args.length == 1) {
            if (commandSourceStack.getSender() instanceof Player player) {
                if(!Manhunt.getInstance().getGame().getHunters().contains(player.getUniqueId())) {
                    player.sendMessage(miniMessage().deserialize("<red>You are not a hunter!"));
                    return;
                }
                Player target = Bukkit.getPlayer(args[0]);
                if (!player.getInventory().contains(Material.COMPASS)) {
                    player.getInventory().addItem(new ItemStack(Material.COMPASS));
                }
                if (target != null) {
                    player.setCompassTarget(target.getLocation());
                    player.sendMessage(miniMessage().deserialize("<green>Compass is now pointing at <player>", unparsed("player", target.getName())));
                    target.sendMessage(miniMessage().deserialize("[<red>MANHUNT</red>] <yellow> <dark_red><player></dark_red> is now tracking you!", unparsed("player", player.getName())));
                } else {
                    player.sendMessage(miniMessage().deserialize("<red>Player not found!"));
                }
            } else {
                commandSourceStack.getSender().sendMessage(miniMessage().deserialize("<red>Only players can use this command!"));
            }
        }
    }
}
