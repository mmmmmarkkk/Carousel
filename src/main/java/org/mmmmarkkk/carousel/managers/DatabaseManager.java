package org.mmmmarkkk.carousel.managers;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import lombok.Data;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.mmmmarkkk.carousel.Main;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Data
public class DatabaseManager {

    private final Main plugin;

    public DatabaseManager(Main plugin) {
        this.plugin = plugin;
    }

    private MongoClient mongoClient;
    private MongoDatabase database;

    public void initDatabase() {
        mongoClient = MongoClients.create("mongodb://root:root@localhost:27017/carousel");
        database = mongoClient.getDatabase("carousel");
    }

    public void createDocument(Player player, Entity entity) {

        MongoCollection<Document> ridesCollection = database.getCollection("player_rides");

        Document rideDocument = new Document()
                .append("playerName", player.getName())
                .append("horseInfo", horseToString((Horse) entity))
                .append("isActive", true)
                .append("startTime", new Date());
        ridesCollection.insertOne(rideDocument);
    }

    public void updateDocument(Player player) {
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

    public String horseToString(Horse horse) {
        return "horseId = '" + horse.getUniqueId() + "', horseOwner = '" + horse.getOwner() + "', horseStyle = '" + horse.getStyle().toString() + "', horseColor = '" + horse.getColor().toString() + "'";
    }

}
