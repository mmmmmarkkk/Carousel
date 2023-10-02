package org.mmmmarkkk.carousel;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;


public class CarouselCommand implements CommandExecutor {

    private static final Main plugin = Main.getInstance();
    private final Random random = new Random();
    private final ArrayList<String> horseColors = plugin.getHorseColors();
    private final ArrayList<String> horseStyles = plugin.getHorseStyles();

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
            carousel.getHorses().forEach(Entity::remove);
            carousel.getArmorStand().remove();
            plugin.setCarousel(null);
            plugin.getServer().getScheduler().cancelTask(plugin.getTaskId());
            plugin.setTaskId(-1);
            player.sendMessage("§6Карусель была удалена.");
            return true;
        }
        plugin.setCarousel(new Carousel(player.getLocation()));
        this.spawnHorses(location);
        BukkitTask bukkitTask = plugin.getServer().getScheduler().runTaskTimer(plugin, CarouselTask.getInstance(), 0, 10 / plugin.getSpeed());
        plugin.setTaskId(bukkitTask.getTaskId());
        player.sendMessage("§6Карусель была создана.");
        return true;
    }

    private void spawnHorses(Location location) {

        double angleBetweenHorses = 2 * Math.PI / plugin.getHorseCount();

        Location armorStandLocation = location.clone();
        armorStandLocation.setYaw(0);
        armorStandLocation.setPitch(0);
        armorStandLocation.add(0.85, 5, -0.7);
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(armorStandLocation, EntityType.ARMOR_STAND);
        armorStand.setGravity(false);
        armorStand.setVisible(false);
        armorStand.setVelocity(new Vector(0, 0, 0));

        Carousel carousel = plugin.getCarousel();
        carousel.setArmorStand(armorStand);

        for (int i = 0; i < plugin.getHorseCount(); i++) {
            double x = location.getX() + plugin.getRadius() * Math.cos(i * angleBetweenHorses);
            double z = location.getZ() + plugin.getRadius() * Math.sin(i * angleBetweenHorses);

            Location horseLocation = new Location(location.getWorld(), x, location.getY(), z);

            double nextAngle = 2 * Math.PI * (i + 1) / plugin.getHorseCount();
            double deltaYaw = Math.toDegrees(nextAngle - angleBetweenHorses);

            horseLocation.setYaw((float) deltaYaw);

            Horse horse = (Horse) location.getWorld().spawnEntity(horseLocation, EntityType.HORSE);

            horse.setStyle(Horse.Style.valueOf(horseStyles.get(random.nextInt(horseStyles.size()))));
            horse.setColor(Horse.Color.valueOf(horseColors.get(random.nextInt(horseColors.size()))));
            horse.setAdult();
            horse.setVelocity(new Vector(0, 0, 0));
            horse.setAI(false);

            horse.setLeashHolder(armorStand);

            carousel.getHorses().add(horse);
        }
    }
}
