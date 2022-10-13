package com.clauscode.wg.mobs;

import com.clauscode.wg.Main;
import com.clauscode.wg.pathfinder.PathfinderGoalMoveToEntity;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.Random;

public class FirePig extends EntityPig implements Listener {
    private final Random random = new Random();
    private final int firePigTaskId;


    public FirePig(Location spawnPosition) {
        super(EntityTypes.PIG, ((CraftWorld) Objects.requireNonNull(spawnPosition.getWorld())).getHandle());

        setPosition(spawnPosition.getX(), spawnPosition.getY(), spawnPosition.getZ());
        setBaby(false);
        setCustomName(new ChatComponentText("§6§lÁåêîí"));
        setCustomNameVisible(true);

        goalSelector.a(0, new PathfinderGoalFloat(this));
        goalSelector.a(1, new PathfinderGoalMoveToEntity(this, 1.5, 32, 0, Player.class, () -> {}));
        goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0));
        goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0));
        goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 20.0f));
        goalSelector.a(8, new PathfinderGoalRandomLookaround(this));

        firePigTaskId = firePigThread();

        world.addEntity(this);
    }

    private int firePigThread() {
        return Bukkit.getScheduler().runTaskTimer(Main.plugin, () -> {
            getBukkitEntity().setFireTicks(30);
            if(!isAlive()) return;
            getBukkitEntity().getWorld().spawnParticle(
                Particle.LAVA,
                getBukkitEntity().getLocation(),
                5, 0.5, 0.5, 0.5, 0.5
            );
            for(Entity entity : getBukkitEntity().getWorld().getEntities()) {
                if(entity.getLocation().distance(getBukkitEntity().getLocation()) <= 8) {
                    entity.setFireTicks(
                        entity.getFireTicks() + 50
                    );
                }
            }
        }, 0L, 20L).getTaskId();
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if(!event.getEntity().equals(getBukkitEntity())) return;
        org.bukkit.World world = getBukkitEntity().getWorld();

        for(int index = 0; index < 96; index++) {
            FallingBlock fire = (FallingBlock) world.spawnFallingBlock(getBukkitEntity().getLocation(), new MaterialData(Material.FIRE));
            fire.setVelocity(
                new Vector(
                    0.5 - random.nextDouble(),
                    0.75,
                    0.5 - random.nextDouble()
                )
            );
        }

        Bukkit.getScheduler().cancelTask(firePigTaskId);
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(!event.getEntity().equals(getBukkitEntity())) return;
        if(event.getCause() != EntityDamageEvent.DamageCause.LAVA &&
            event.getCause() != EntityDamageEvent.DamageCause.FIRE &&
            event.getCause() != EntityDamageEvent.DamageCause.FIRE_TICK
        ) return;
        event.setCancelled(true);
    }
}
