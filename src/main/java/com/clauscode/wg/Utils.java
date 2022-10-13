package com.clauscode.wg;

import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;

public class Utils {
    public static void sync(Long tick, Runnable func) {
        Bukkit.getScheduler().runTaskLater(Main.plugin, func, tick);
    }
}
