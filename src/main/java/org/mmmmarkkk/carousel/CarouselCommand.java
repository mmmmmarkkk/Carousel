package org.mmmmarkkk.carousel;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitTask;

public class CarouselCommand implements CommandExecutor {

    private static final Main plugin = Main.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Доступно только для игроков!");
            return false;
        }
        Player player = (Player) commandSender;
        Location location = player.getLocation();

        Carousel carousel = plugin.getCarousel();
        if (carousel != null) {
            carousel.getArmorStand().remove();
            carousel.getHorses().forEach(Entity::remove);
            plugin.setCarousel(null);
            plugin.getServer().getScheduler().cancelTask(plugin.getTaskId());
            plugin.setTaskId(-1);
            player.sendMessage("§6Карусель была удалена.");
            return true;
        }
        plugin.setCarousel(new Carousel(player.getLocation()));
        plugin.getHorseManager().spawnHorses(location);
        BukkitTask bukkitTask = plugin.getServer().getScheduler().runTaskTimer(plugin, CarouselTask.getInstance(), 0, 10 / plugin.getConfigManager().getSpeed());
        plugin.setTaskId(bukkitTask.getTaskId());
        player.sendMessage("§6Карусель была создана.");
        return true;
    }
}
