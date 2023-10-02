package org.mmmmarkkk.carousel;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Horse;

import java.util.ArrayList;

@Data
public class Carousel {

    private ArrayList<Horse> horses;
    private ArmorStand armorStand;
    private Location center;
    private double angle;
    private double time;

    public Carousel(Location center) {
        this.center = center;
        horses = new ArrayList<>();
        angle = 0.05;
        time = 0;
    }

}
