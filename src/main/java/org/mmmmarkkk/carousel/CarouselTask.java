package org.mmmmarkkk.carousel;

import org.bukkit.Location;

public class CarouselTask implements Runnable {

    private static final CarouselTask instance = new CarouselTask();
    private static final Main plugin = Main.getInstance();

    @Override
    public void run() {
        Carousel carousel = plugin.getCarousel();
        double amplitude = plugin.getAmplitude();
        double angleBetweenHorses = 2 * Math.PI / plugin.getHorseCount();
        double time = carousel.getTime();

        for (int i = 0; i < carousel.getHorses().size(); i++) {

            double x = carousel.getCenter().getX() + plugin.getRadius() * Math.cos(i * angleBetweenHorses + carousel.getAngle());
            double z = carousel.getCenter().getZ() + plugin.getRadius() * Math.sin(i * angleBetweenHorses + carousel.getAngle());
            double y = carousel.getCenter().getY();

            y = y + amplitude * Math.sin(2 * 0.25 * time);

            Location horseLocation = new Location(carousel.getCenter().getWorld(), x, y, z);

            double nextAngle = 2 * Math.PI * (i + 1) / plugin.getHorseCount();
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

    public static CarouselTask getInstance() {
        return instance;
    }
}
