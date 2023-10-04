package org.mmmmarkkk.carousel.managers;

import lombok.Data;
import org.bukkit.configuration.file.FileConfiguration;
import org.mmmmarkkk.carousel.Main;

import java.util.ArrayList;

@Data
public class ConfigManager {

    private final Main plugin;

    private ArrayList<String> horseColors;
    private ArrayList<String> horseStyles;
    private int horseCount;
    private double radius;
    private double amplitude;
    private long speed;

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
    }

    public void initConfig() {
        FileConfiguration config = Main.getInstance().getConfig();
        horseCount = config.getInt("carousel.horse-count", 4);
        radius = config.getDouble("carousel.radius", 2);
        horseColors = new ArrayList<>(config.getStringList("horse.colors"));
        horseStyles = new ArrayList<>(config.getStringList("horse.styles"));
        amplitude = config.getDouble("carousel.amplitude", 0.5);
        speed = config.getLong("carousel.speed", 1);
        Main.getInstance().saveDefaultConfig();
    }


}
