package org.mmmmarkkk.carousel;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.mmmmarkkk.carousel.managers.ConfigManager;
import org.mmmmarkkk.carousel.managers.DatabaseManager;
import org.mmmmarkkk.carousel.managers.HorseManager;

@Getter
@Setter
public final class Main extends JavaPlugin implements Listener {

    private static Main main;
    private Carousel carousel;
    private int taskId;

    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private HorseManager horseManager;

    @Override
    public void onEnable() {
        main = this;
        configManager = new ConfigManager(this);
        databaseManager = new DatabaseManager(this);
        horseManager = new HorseManager();
        configManager.initConfig();
        databaseManager.initDatabase();
        getCommand("carousel").setExecutor(new CarouselCommand());
        Bukkit.getPluginManager().registerEvents(new CarouselListener(), this);
    }

    @Override
    public void onDisable() {
        if (carousel != null) {
            carousel.getArmorStand().remove();
            carousel.getHorses().forEach(Entity::remove);
        }
        if (taskId != -1) {
            getServer().getScheduler().cancelTask(taskId);
        }
    }

    public static Main getInstance () {
        return main;
    }
}
