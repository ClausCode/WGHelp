package com.clauscode.wg.pathfinder;

import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.PathfinderGoal;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.EnumSet;

public class PathfinderGoalMoveToEntity extends PathfinderGoal {

    private final EntityInsentient entity;
    private final double speed;
    private final double maxRadius;
    private final double minRadius;
    private final Class<? extends Entity> targetClass;
    private final Runnable callback;
    private Entity currentTarget;


    public PathfinderGoalMoveToEntity(EntityInsentient entity, double speed, double maxRadius, double minRadius, Class<? extends Entity> targetClass, Runnable callback) {
        this.entity = entity;
        this.speed = speed;
        this.maxRadius = maxRadius;
        this.minRadius = minRadius;
        this.targetClass = targetClass;
        this.callback = callback;

        a(EnumSet.of(Type.MOVE));
    }

    @Override
    public boolean a() {
        World world = entity.getBukkitEntity().getWorld();

        for(Entity entityTarget : world.getEntitiesByClass(targetClass)) {
            Location position = entity.getBukkitEntity().getLocation();
            Location target = entityTarget.getLocation();

            if(position.distance(target) <= maxRadius && position.distance(target) > minRadius && currentTarget == null) {
                if(entityTarget instanceof Player) {
                    Player player = (Player) entityTarget;
                    if(player.getGameMode() != GameMode.SURVIVAL && player.getGameMode() != GameMode.ADVENTURE) continue;
                }
                currentTarget = entityTarget;
                break;
            }
        }

        return checkCurrentTarget();
    }

    private boolean checkCurrentTarget() {
        if(currentTarget == null) return false;

        Location position = entity.getBukkitEntity().getLocation();
        Location target = currentTarget.getLocation();

        if(currentTarget instanceof Player) {
            Player player = (Player) currentTarget;
            if(player.getGameMode() != GameMode.SURVIVAL && player.getGameMode() != GameMode.ADVENTURE) {
                currentTarget = null;
                stop();
                return false;
            }
        }

        if(
            !entity.isAlive()
            ||
            currentTarget.isDead()
            ||
            position.distance(target) > maxRadius
        ) {
            stop();
            currentTarget = null;
            return false;
        }

        if(position.distance(target) <= minRadius) {
            callback.run();
            currentTarget = null;
        } else {
            entity.getNavigation().a(
                    target.getX(),
                    target.getY(),
                    target.getZ(),
                    speed
            );
        }

        return true;
    }

    private void stop() {
        Location position = entity.getBukkitEntity().getLocation();
        entity.getNavigation().a(
                position.getX(),
                position.getY(),
                position.getZ(),
                speed
        );
    }
}
