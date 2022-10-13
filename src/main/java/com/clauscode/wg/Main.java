package com.clauscode.wg;

import com.clauscode.wg.mobs.FirePig;
import com.clauscode.wg.mobs.RainbowSheep;
import com.clauscode.wg.mobs.TntChicken;
import java.util.Random;

public class Main extends JavaPlugin {
    public static Plugin plugin;
    private final Random random = new Random();

    @Override
    public void onEnable() {
        NMSUtils
        plugin = this;



        getServer().getScheduler().runTaskTimer(this, () -> {
            spawnSheep();
            spawnChicken();
            spawnPig();
        }, 0, 100);
    }

    private void spawnSheep() {
        List<Sheep> list = (List<Sheep>) Bukkit.getWorld("world").getEntitiesByClass(Sheep.class);
        if(list.size() == 0) return;

        Sheep sheep = list.get(random.nextInt(list.size()));
        if(sheep.isCustomNameVisible()) return;
        Location spawnPosition = sheep.getLocation().clone();
        sheep.remove();

        Bukkit.getPluginManager().registerEvents(new RainbowSheep(spawnPosition), Main.plugin);
    }

    private void spawnChicken() {
        List<Chicken> chickenList = (List<Chicken>) Bukkit.getWorld("world").getEntitiesByClass(Chicken.class);
        if(chickenList.size() == 0) return;

        Chicken chicken = chickenList.get(random.nextInt(chickenList.size()));
        if(chicken.isCustomNameVisible()) return;
        Location spawnPosition = chicken.getLocation().clone();
        chicken.remove();

        Bukkit.getPluginManager().registerEvents(new TntChicken(spawnPosition), Main.plugin);
    }

    private void spawnPig() {
        List<Pig> pigList = (List<Pig>) Bukkit.getWorld("world").getEntitiesByClass(Pig.class);
        if(pigList.size() == 0) return;

        Pig pig = pigList.get(random.nextInt(pigList.size()));
        if(pig.isCustomNameVisible()) return;
        Location spawnPosition = pig.getLocation().clone();
        pig.remove();

        Bukkit.getPluginManager().registerEvents(new FirePig(spawnPosition), Main.plugin);
    }

    @Override
    public void onDisable() {
        for(Entity sheep : Bukkit.getWorld("world").getEntitiesByClasses(Sheep.class)) {
            if(sheep.isCustomNameVisible()) {
                sheep.remove();
            }
        }
        for(Entity chicken : Bukkit.getWorld("world").getEntitiesByClasses(Chicken.class)) {
            if(chicken.isCustomNameVisible()) {
                chicken.remove();
            }
        }
        for(Entity pig : Bukkit.getWorld("world").getEntitiesByClasses(Pig.class)) {
            if(pig.isCustomNameVisible()) {
                pig.remove();
            }
        }
    }
}
