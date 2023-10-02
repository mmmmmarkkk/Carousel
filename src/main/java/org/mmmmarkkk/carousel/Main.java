package org.mmmmarkkk.carousel;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public final class Main extends JavaPlugin implements Listener {

    private static Main main;
    private Carousel carousel;
    private int taskId;

    private ArrayList<String> horseColors;
    private ArrayList<String> horseStyles;
    private int horseCount;
    private double radius;
    private double amplitude;
    private long speed;

    private MongoClient mongoClient;
    private MongoDatabase database;


    @Override
    public void onEnable() {
        main = this;
        initConfig();
        initDatabase();
        initCommands();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        if (carousel != null) {
            carousel.getHorses().forEach(Entity::remove);
            carousel.getArmorStand().remove();
        }
        if (taskId != -1) {
            getServer().getScheduler().cancelTask(taskId);
        }
    }

    public void initConfig() {
        FileConfiguration config = getConfig();
        horseCount = config.getInt("carousel.horse-count", 4);
        radius = config.getDouble("carousel.radius", 2);
        horseColors = new ArrayList<>(config.getStringList("horse.colors"));
        horseStyles = new ArrayList<>(config.getStringList("horse.styles"));
        amplitude = config.getDouble("carousel.amplitude", 0.5);
        speed = config.getLong("carousel.speed", 1);
        saveDefaultConfig();
    }

    public void initDatabase() {
        mongoClient = MongoClients.create("mongodb://root:root@localhost:27017/carousel");
        database = mongoClient.getDatabase("carousel");
    }

    public void initCommands() {
        getCommand("carousel").setExecutor(new CarouselCommand());
    }

    @EventHandler
    public void on(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        if (entity instanceof Horse) {
            if (carousel.getHorses().contains(entity)) {
                if (entity.getPassengers().size() >= 1) {
                    player.sendMessage("§cЭта лошадь уже занята!");
                    return;
                }
                entity.addPassenger(player);
                player.sendMessage("§aУспешно сели на лошадь!");
                MongoCollection<Document> ridesCollection = database.getCollection("player_rides");

                Document rideDocument = new Document()
                        .append("playerName", player.getName())
                        .append("horseInfo", horseToString((Horse) entity))
                        .append("isActive", true)
                        .append("startTime", new Date());
                ridesCollection.insertOne(rideDocument);
            }
        }
    }
    @EventHandler
    public void on(EntityDismountEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (event.getDismounted() instanceof Horse) {
                Horse horse = (Horse) event.getDismounted();
                if (carousel.getHorses().contains(horse)) {
                    MongoCollection<Document> ridesCollection = database.getCollection("player_rides");
                    Bson playerNameFilter = Filters.eq("playerName", player.getName());
                    Bson isActiveFilter = Filters.eq("isActive", true);
                    Bson compositeFilter = Filters.and(playerNameFilter, isActiveFilter);
                    Bson updateEndTime = Updates.set("endTime", new Date());
                    Bson updateActiveStatus = Updates.set("isActive", false);
                    List<Bson> updatesList = Arrays.asList(updateEndTime, updateActiveStatus);
                    Bson combinedUpdates = Updates.combine(updatesList);
                    ridesCollection.updateOne(compositeFilter, combinedUpdates);
                }
            }
        }
    }

    @EventHandler
    public void on(EntityDamageEvent event){
        if (event.getEntity() instanceof Horse) {
            Horse horse = (Horse) event.getEntity();
            if (carousel.getHorses().contains(horse)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Entity entity = player.getVehicle();
        if (entity != null) {
            if (entity instanceof Horse && carousel.getHorses().contains(entity)) {
                entity.removePassenger(player);
            }
        }
    }

    public String horseToString(Horse horse) {
        return "horseId = '" + horse.getUniqueId() + "', horseOwner = '" + horse.getOwner() + "', horseStyle = '" + horse.getStyle().toString() + "', horseColor = '" + horse.getColor().toString() + "'";
    }

    public static Main getInstance () {
        return main;
    }
}
