package de.bentzin.manhunt.command;


import de.bentzin.manhunt.Manhunt;
import de.bentzin.manhunt.ManhuntGame;
import de.bentzin.manhunt.timer.Timer;
import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;
import static net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.*;
import static net.kyori.adventure.text.minimessage.tag.resolver.TagResolver.*;

/**
 * @author Ture Bentzin
 * @since 26-08-2024
 */
public class TimerCommand implements BasicCommand {


    private void sendHelpMessage(@NotNull Audience audience) {
        Timer timer = Manhunt.getInstance().getTimer();
        audience.sendMessage(miniMessage().deserialize("<green> Timer:<gray> <timer>", unparsed("timer", timer.getFormat())));
        audience.sendMessage(miniMessage().deserialize("<yellow> /timer start"));
        audience.sendMessage(miniMessage().deserialize("<yellow> /timer stop"));
        audience.sendMessage(miniMessage().deserialize("<yellow> /timer <red>reset"));
        audience.sendMessage(miniMessage().deserialize("<yellow> /timer set <blue>HH:mm:ss"));
    }

    @Override
    public void execute(@NotNull CommandSourceStack stack, @NotNull String @NotNull [] strings) {
        ManhuntGame game = Manhunt.getInstance().getGame();
        if (strings.length == 0) {
            sendHelpMessage(stack.getSender());
        } else if (strings.length == 1) {
            if (strings[0].equals("start")) {
                if (!game.isRunning()) {
                    game.start();
                } else {
                    stack.getSender().sendMessage(miniMessage().deserialize("<red>Timer is already running!"));
                }
            } else if (strings[0].equals("stop")) {
                if (game.isRunning()) {
                    game.stop();
                } else {
                    stack.getSender().sendMessage(miniMessage().deserialize("<red>Timer is not running!"));
                }
            } else if (strings[0].equals("reset")) {
                stack.getSender().sendMessage(miniMessage().deserialize("<red>Resetting..."));
                game.reset();
            } else {
                sendHelpMessage(stack.getSender());
            }
        } else if (strings.length == 2) {
            if (strings[0].equals("set")) {
                String val = strings[1]; //Format: HH:mm:ss, convert to milliseconds
                long time = 0;
                try {
                    String[] split = val.split(":");
                    time = Long.parseLong(split[0]) * 3600000 + Long.parseLong(split[1]) * 60000 + Long.parseLong(split[2]) * 1000;
                } catch (NumberFormatException e) {
                    stack.getSender().sendMessage(miniMessage().deserialize("<red>Invalid time format!"));
                }
                Manhunt.getInstance().getTimer().setTimer(time);

            } else {
                sendHelpMessage(stack.getSender());
            }
        }
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, @NotNull String @NotNull [] args) {
        return BasicCommand.super.suggest(commandSourceStack, args);
    }


    @Override
    public @Nullable String permission() {
        return "manhunt.timer";
    }
}
