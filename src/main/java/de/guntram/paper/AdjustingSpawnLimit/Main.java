/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.guntram.paper.AdjustingSpawnLimit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author gbl
 */
public class Main extends JavaPlugin implements Listener {

    private FileConfiguration config;
    private Map<String, Integer> originalLimits;
    
    private int minimumPercent;
    private int stepSize;
    private double decreaseUnderTPS;
    private double increaseOverTPS;
    private boolean extendedLogging;
    private int disablePigmenAtLowerThan;
    private int enablePigmenAtHigherThan;

    private int currentPercent;
    private static Logger LOGGER;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();
        LOGGER=this.getLogger();
        
        minimumPercent = config.getInt("minimumPercent", 50);
        stepSize = config.getInt("stepSize", 5);
        decreaseUnderTPS = config.getDouble("decreaseUnderTPS", 19);
        increaseOverTPS = config.getDouble("increaseOverTPS", 19.5);
        extendedLogging = config.getBoolean("extendedLogging", false);
        disablePigmenAtLowerThan = config.getInt("disablePigmenWhenLower, 50");
        enablePigmenAtHigherThan = config.getInt("enablePigmenWhenHigher, 80");
        
        originalLimits = new HashMap<>();
        List<World> worlds = getServer().getWorlds();
        for (World world: worlds) {
            LOGGER.log(Level.INFO, "{0}: original mob limit is {1}", new Object[]{world.getName(), world.getMonsterSpawnLimit()});
            originalLimits.put(world.getName(), world.getMonsterSpawnLimit());
        }
        currentPercent = 100;

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new SpawnLimitAdjuster(), 20*60, 20*60);
        
    }
    
    class SpawnLimitAdjuster implements Runnable {

        @Override
        public void run() {
            double[] tps = getServer().getTPS();
            int oldPercent = currentPercent;
            if (tps[0] >= increaseOverTPS) {
                currentPercent = Math.min(currentPercent+stepSize, 100);
            } else if (tps[0] <= decreaseUnderTPS) {
                currentPercent = Math.max(currentPercent-stepSize, minimumPercent);
            }
            if (currentPercent != oldPercent | extendedLogging) {
                LOGGER.log(Level.INFO, "TPS was {2}, Adjusting percentage from {0} to {1}",
                        new Object[]{oldPercent, currentPercent, tps[0]});
            }
            
            for (Map.Entry<String, Integer> entry: originalLimits.entrySet()) {
                String worldName = entry.getKey();
                int origLimit = entry.getValue();
                World world = getServer().getWorld(worldName);
                int oldCount = world.getMonsterSpawnLimit();
                int newCount = origLimit * currentPercent / 100;
                world.setMonsterSpawnLimit(newCount);
                if (extendedLogging) {
                    LOGGER.log(Level.INFO, "Change limit from {0} to {1} in world {2}", new Object[]{oldCount, newCount, worldName});
                }
            }
        }
    }
}
