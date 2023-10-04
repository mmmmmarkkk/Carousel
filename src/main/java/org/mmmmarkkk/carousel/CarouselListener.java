package org.mmmmarkkk.carousel;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

public class CarouselListener implements Listener {

    private static final Main plugin = Main.getInstance();

    @EventHandler
    public void on(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        if (entity instanceof Horse) {
            if (plugin.getCarousel() != null) {
                if (plugin.getCarousel().getHorses().contains(entity)) {
                    event.setCancelled(true);
                    if (entity.getPassengers().size() >= 1) {
                        player.sendMessage("§cЭта лошадь уже занята!");
                        return;
                    }
                    player.setVelocity(new Vector(0, 0, 0));
                    entity.addPassenger(player);
                    player.sendMessage("§aУспешно сели на лошадь!");
                    plugin.getDatabaseManager().createDocument(player, entity);
                }
            }
        }
    }

    @EventHandler
    public void on(EntityDismountEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (event.getDismounted() instanceof Horse) {
                Horse horse = (Horse) event.getDismounted();
                if (plugin.getCarousel() != null) {
                    if (plugin.getCarousel().getHorses().contains(horse)) {
                        plugin.getDatabaseManager().updateDocument(player);
                    }
                }
            }
        }
    }

    @EventHandler
    public void on(EntityDamageEvent event) {
        if (event.getEntity() instanceof Horse) {
            Horse horse = (Horse) event.getEntity();
            if (plugin.getCarousel() != null) {
                if (plugin.getCarousel().getHorses().contains(horse)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Entity entity = player.getVehicle();
        if (entity != null) {
            if (entity instanceof Horse) {
                if (plugin.getCarousel() != null) {
                    if (plugin.getCarousel().getHorses().contains(entity)) {
                        entity.removePassenger(player);
                    }
                }
            }
        }
    }

}
