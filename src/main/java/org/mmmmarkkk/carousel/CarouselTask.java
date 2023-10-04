package org.mmmmarkkk.carousel;

import org.mmmmarkkk.carousel.managers.HorseManager;

public class CarouselTask implements Runnable {

    private static final CarouselTask instance = new CarouselTask();
    private static final Main plugin = Main.getInstance();
    private final HorseManager horseManager = new HorseManager();

    @Override
    public void run() {
        horseManager.updateHorseLocation(plugin.getCarousel());
    }

    public static CarouselTask getInstance() {
        return instance;
    }
}
