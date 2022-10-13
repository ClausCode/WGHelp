package com.clauscode.wg.mobs;

import com.clauscode.wg.Main;
import com.clauscode.wg.pathfinder.PathfinderGoalMoveToEntity;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.Random;

public class RainbowSheep extends EntitySheep implements Listener {
    private final Random random = new Random();
    private final char[] nameColors = "46eab9d".toCharArray();
    private final EnumColor[] sheepColors = new EnumColor[] {
        EnumColor.RED,
        EnumColor.ORANGE,
        EnumColor.YELLOW,
        EnumColor.GREEN,
        EnumColor.LIGHT_BLUE,
        EnumColor.BLUE,
        EnumColor.PURPLE
    };
    private final Material[] woolMaterials = new Material[] {
        Material.RED_WOOL,
        Material.ORANGE_WOOL,
        Material.YELLOW_WOOL,
        Material.GREEN_WOOL,
        Material.LIGHT_BLUE_WOOL,
        Material.BLUE_WOOL,
        Material.PURPLE_WOOL
    };
    private int colorIndex = 0;
    private final int sheepColorTaskId;

    public RainbowSheep(Location spawnPosition) {
        super(EntityTypes.SHEEP, ((CraftWorld) Objects.requireNonNull(spawnPosition.getWorld())).getHandle());

        setPosition(spawnPosition.getX(), spawnPosition.getY(), spawnPosition.getZ());
        setBaby(false);
        setCustomNameVisible(true);
        setHealth(100);

        goalSelector.a(0, new PathfinderGoalFloat(this));
        goalSelector.a(2, new PathfinderGoalMoveToEntity(this, 2.0, 16, 2.5, Player.class, () -> {
            org.bukkit.World world = getBukkitEntity().getWorld();
            getBukkitEntity().remove();
            world.createExplosion(getBukkitEntity().getLocation(), 6, false, false);

            for(int index = 0; index < 32; index++) {
                MaterialData data = new MaterialData(woolMaterials[random.nextInt(woolMaterials.length)]);
                FallingBlock fallingBlock = world.spawnFallingBlock(
                    getBukkitEntity().getLocation().clone().add(0, 1, 0),
                    data
                );
                fallingBlock.setVelocity(
                    new Vector(
                        (12 - random.nextInt(25)) / 100d,
                        random.nextInt(15) / 10d,
                        (12 - random.nextInt(25)) / 100d
                    )
                );
            }
        }));
        goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0));
        goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0));
        goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 16.0f));
        goalSelector.a(8, new PathfinderGoalRandomLookaround(this));

        sheepColorTaskId = sheepColorThread();
        world.addEntity(this);
    }

    private int sheepColorThread() {
        return Bukkit.getScheduler().runTaskTimer(Main.plugin, () -> {
            if(!isAlive()) return;
            String nameColor = String.format("§%s", nameColors[colorIndex]);

            setCustomName(new ChatComponentText(nameColor + "Радужная Овечка"));
            setColor(sheepColors[colorIndex]);

            colorIndex++;
            if(colorIndex >= nameColors.length) {
                colorIndex = 0;
            }
        }, 0L, 20L).getTaskId();
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if(!event.getEntity().equals(getBukkitEntity())) return;

        Bukkit.getScheduler().cancelTask(sheepColorTaskId);
        HandlerList.unregisterAll(this);
    }
}
