package com.clauscode.wg.mobs;

import com.clauscode.wg.pathfinder.PathfinderGoalMoveToEntity;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.Random;

public class TntChicken extends EntityChicken implements Listener {
    private final Random random = new Random();


    public TntChicken(Location spawnPosition) {
        super(EntityTypes.CHICKEN, ((CraftWorld) Objects.requireNonNull(spawnPosition.getWorld())).getHandle());

        setPosition(spawnPosition.getX(), spawnPosition.getY(), spawnPosition.getZ());
        setBaby(false);
        setCustomName(new ChatComponentText("§c§lКо-ко-ко-ко-ко"));
        setCustomNameVisible(true);

        goalSelector.a(0, new PathfinderGoalFloat(this));
        goalSelector.a(2, new PathfinderGoalMoveToEntity(this, 1.8, 24, 12, Player.class, () -> {
            org.bukkit.World world = getBukkitEntity().getWorld();

            TNTPrimed tnt = (TNTPrimed) world.spawnEntity(getBukkitEntity().getLocation(), EntityType.PRIMED_TNT);
            tnt.setYield(4);
            tnt.setFuseTicks(30);
            tnt.setVelocity(getBukkitEntity().getLocation().getDirection().add(new Vector(0, 0.35, 0)).multiply(1));
        }));
        goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0));
        goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0));
        goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 20.0f));
        goalSelector.a(8, new PathfinderGoalRandomLookaround(this));

        world.addEntity(this);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if(!event.getEntity().equals(getBukkitEntity())) return;
        org.bukkit.World world = getBukkitEntity().getWorld();

        for(int index = 0; index < 8; index++) {
            TNTPrimed tnt = (TNTPrimed) world.spawnEntity(getBukkitEntity().getLocation(), EntityType.PRIMED_TNT);
            tnt.setYield(4);
            tnt.setFuseTicks(30);
            tnt.setVelocity(
                new Vector(
                    0.5 - random.nextDouble(),
                    0.5,
                    0.5 - random.nextDouble()
                )
            );
        }

        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(!event.getEntity().equals(getBukkitEntity())) return;
        if(event.getCause() != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION &&
            event.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
        ) return;
        event.setCancelled(true);
    }
}
