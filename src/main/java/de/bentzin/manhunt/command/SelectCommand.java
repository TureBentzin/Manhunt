package de.bentzin.manhunt.command;


import de.bentzin.manhunt.Manhunt;
import de.bentzin.manhunt.ManhuntGame;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.*;
import static net.kyori.adventure.text.minimessage.tag.resolver.TagResolver.*;

/**
 * @author Ture Bentzin
 * @since 26-08-2024
 */
public class SelectCommand implements BasicCommand {

    private void prepPlayer(@NotNull Player player) {
        player.clearActivePotionEffects();
        player.setFireTicks(0);
        player.getInventory().clear();
        player.setExp(0);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setWalkSpeed(0.2f);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setFallDistance(0);
        player.setFireTicks(0);
        player.setGlowing(false);
        player.setRemainingAir(player.getMaximumAir());
        Location spawn = Objects.requireNonNull(Bukkit.getWorld("world")).getSpawnLocation();
        player.teleport(spawn);
        player.spawnAt(spawn, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }

    @Override
    public void execute(@NotNull CommandSourceStack commandSourceStack, @NotNull String[] strings) {
        if(strings.length == 0) {
            commandSourceStack.getSender().sendMessage(miniMessage().deserialize("<red>Usage: /select <hunter/runner/none>"));
        } else if(strings.length == 1) {
            ManhuntGame game = Manhunt.getInstance().getGame();
            if(strings[0].equals("hunter")) {
                if(commandSourceStack.getSender() instanceof Player player) {
                    if(game.getRunners().contains(player.getUniqueId())) {
                        game.getRunners().remove(player.getUniqueId());
                        Bukkit.broadcast(miniMessage().deserialize("[<red>MANHUNT</red>] <yellow> <player> is no longer a <blue>Runner</blue>!", unparsed("player", player.getName())));
                    }
                    if(!game.getHunters().contains(player.getUniqueId())) {
                        game.getHunters().add(player.getUniqueId());
                        Bukkit.broadcast(miniMessage().deserialize("[<red>MANHUNT</red>] <yellow> <player> is now a <dark_red>Hunter</dark_red>!", unparsed("player", player.getName())));
                        prepPlayer(player);
                        player.setGlowing(true);
                    }
                }else {
                    commandSourceStack.getSender().sendMessage(miniMessage().deserialize("<red>Only players can be hunters!"));
                }
            } else if(strings[0].equals("runner")) {
                if(commandSourceStack.getSender() instanceof Player player) {
                    if(game.getHunters().contains(player.getUniqueId())) {
                        game.getHunters().remove(player.getUniqueId());
                        Bukkit.broadcast(miniMessage().deserialize("[<red>MANHUNT</red>] <yellow> <player> is no longer a <dark_red>Hunter</dark_red>!", unparsed("player", player.getName())));
                    }
                    if(!game.getRunners().contains(player.getUniqueId())) {
                        game.getRunners().add(player.getUniqueId());
                        Bukkit.broadcast(miniMessage().deserialize("[<red>MANHUNT</red>] <yellow> <player> is now a <blue>Runner</blue>!", unparsed("player", player.getName())));
                        prepPlayer(player);
                    }
                }else {
                    commandSourceStack.getSender().sendMessage(miniMessage().deserialize("<red>Only players can be runners!"));
                }
            } else if(strings[0].equals("none")) {
                if(commandSourceStack.getSender() instanceof Player player) {
                    prepPlayer(player);
                    if(game.getHunters().contains(player.getUniqueId())) {
                        game.getHunters().remove(player.getUniqueId());
                        Bukkit.broadcast(miniMessage().deserialize("[<red>MANHUNT</red>] <yellow> <player> is no longer a <dark_red>Hunter</dark_red>!", unparsed("player", player.getName())));
                    }
                    if(game.getRunners().contains(player.getUniqueId())) {
                        game.getRunners().remove(player.getUniqueId());
                        Bukkit.broadcast(miniMessage().deserialize("[<red>MANHUNT</red>] <yellow> <player> is no longer a <blue>Runner</blue>!", unparsed("player", player.getName())));
                    }
                }else {
                    commandSourceStack.getSender().sendMessage(miniMessage().deserialize("<red>Only players can be runners!"));
                }
            } else {
                commandSourceStack.getSender().sendMessage(miniMessage().deserialize("<red>Usage: /select <hunter/runner/none>"));
            }

        }
    }
}
