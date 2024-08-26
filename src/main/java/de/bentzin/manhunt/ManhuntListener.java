package de.bentzin.manhunt;

import io.papermc.paper.event.block.DragonEggFormEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;


import java.util.logging.Logger;

public class ManhuntListener implements Listener {

    private final @NotNull Logger logger = Manhunt.getInstance().getLogger();

    @EventHandler
    public void onEgg(@NotNull DragonEggFormEvent event) {
        if(Manhunt.getInstance().getGame().isRunning()) {
            Manhunt.getInstance().getGame().defeat();
        }else {
            logger.warning("The dragon was defeated without the game running!");
        }
    }
}
