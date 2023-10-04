package org.mmmmarkkk.carousel.managers;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.util.Vector;
import org.mmmmarkkk.carousel.Carousel;
import org.mmmmarkkk.carousel.Main;

import java.util.Random;

public class HorseManager {

    private static final Main plugin = Main.getInstance();
    private final ConfigManager configManager = plugin.getConfigManager();
    private final Random random = new Random();

    public void spawnHorses(Location location) {
        double angleBetweenHorses = 2 * Math.PI / configManager.getHorseCount();

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

        for (int i = 0; i < configManager.getHorseCount(); i++) {
            double x = location.getX() + configManager.getRadius() * Math.cos(i * angleBetweenHorses);
            double z = location.getZ() + configManager.getRadius() * Math.sin(i * angleBetweenHorses);

            Location horseLocation = new Location(location.getWorld(), x, location.getY(), z);

            double nextAngle = 2 * Math.PI * (i + 1) / configManager.getHorseCount();
            double deltaYaw = Math.toDegrees(nextAngle - angleBetweenHorses);

            horseLocation.setYaw((float) deltaYaw);

            Horse horse = (Horse) location.getWorld().spawnEntity(horseLocation, EntityType.HORSE);

            horse.setStyle(Horse.Style.valueOf(configManager.getHorseStyles().get(random.nextInt(configManager.getHorseStyles().size()))));
            horse.setColor(Horse.Color.valueOf(configManager.getHorseColors().get(random.nextInt(configManager.getHorseColors().size()))));
            horse.setAdult();
            horse.setVelocity(new Vector(0, 0, 0));
            horse.setAI(false);

            horse.setLeashHolder(armorStand);

            carousel.getHorses().add(horse);
        }
    }

    public void updateHorseLocation(Carousel carousel) {
        double amplitude = configManager.getAmplitude();
        double angleBetweenHorses = 2 * Math.PI / configManager.getHorseCount();
        double time = carousel.getTime();

        for (int i = 0; i < carousel.getHorses().size(); i++) {

            double x = carousel.getCenter().getX() + configManager.getRadius() * Math.cos(i * angleBetweenHorses + carousel.getAngle());
            double z = carousel.getCenter().getZ() + configManager.getRadius() * Math.sin(i * angleBetweenHorses + carousel.getAngle());
            double y = carousel.getCenter().getY();

            y = y + amplitude * Math.sin(2 * 0.25 * time);

            Location horseLocation = new Location(carousel.getCenter().getWorld(), x, y, z);

            double nextAngle = 2 * Math.PI * (i + 1) / configManager.getHorseCount();
            double deltaYaw = Math.toDegrees(nextAngle - angleBetweenHorses + carousel.getAngle());


            horseLocation.setYaw((float) deltaYaw);

            Location location = carousel.getHorses().get(i).getLocation();
            location.setX(x);
            location.setY(y);
            location.setZ(z);
            location.setYaw((float) deltaYaw);

            carousel.getHorses().get(i).teleport(horseLocation);
        }
        carousel.setAngle(carousel.getAngle() + 0.05);
        time++;
        carousel.setTime(time);
    }
}
