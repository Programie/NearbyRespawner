package com.selfcoders.nearbyrespawner;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class NearbyRespawner extends JavaPlugin implements Listener {
    HashMap<UUID, Location> perPlayerDeathLocation = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        perPlayerDeathLocation.put(player.getUniqueId(), player.getLocation());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        Location deathLocation = perPlayerDeathLocation.get(player.getUniqueId());
        if (deathLocation == null) {
            return;
        }

        perPlayerDeathLocation.remove(player.getUniqueId());

        if (!player.hasPermission("nearbyrespawner.random-respawn")) {
            return;
        }

        World deathWorld = deathLocation.getWorld();
        if (deathWorld == null) {
            return;
        }

        // Do not find a random death location in Nether or End
        // Random location could be a small island where the player could not get away from
        if (deathWorld.getEnvironment() != World.Environment.NORMAL) {
            return;
        }

        double minSpawnDistance = getConfig().getDouble("min-spawn-distance", 1000);

        Location respawnLocation = event.getRespawnLocation();
        if (minSpawnDistance > 0 && deathWorld == respawnLocation.getWorld() && deathLocation.distance(respawnLocation) < minSpawnDistance) {
            return;
        }

        double min = getConfig().getDouble("death-location-distance.min", 0);
        double max = getConfig().getDouble("death-location-distance.max", 1000);
        int maxChunksToTry = getConfig().getInt("max-chunks-to-try", 10);
        int maxTriesInChunk = getConfig().getInt("max-tries-in-chunk", 10);

        respawnLocation = findNearbyLocation(deathLocation, min, max, maxChunksToTry, maxTriesInChunk);
        if (respawnLocation == null) {
            return;
        }

        event.setRespawnLocation(respawnLocation);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        perPlayerDeathLocation.remove(event.getPlayer().getUniqueId());
    }

    private Location findNearbyLocation(Location location, double min, double max, int maxChunksToTry, int maxTriesInChunk) {
        for (int currentTry = 1; currentTry <= maxChunksToTry; currentTry++) {
            Location newLocation = location.clone();

            newLocation.add(getRandom(min, max, true), 0, getRandom(min, max, true));

            Chunk chunk = newLocation.getChunk();

            for (int currentChunkTry = 1; currentChunkTry <= maxTriesInChunk; currentChunkTry++) {
                Block block = getHighestRandomBlock(chunk);

                // Prevent spawning in water or lava
                if (block.isLiquid()) {
                    continue;
                }

                // Set new location on top of the highest block
                newLocation = block.getLocation();
                newLocation.add(0, 1, 0);

                return newLocation;
            }
        }

        return null;
    }

    private Block getHighestRandomBlock(Chunk chunk) {
        // There is no other way to the the world X and Z coordinates of the chunk
        Location chunkLocation = chunk.getBlock(0, 0, 0).getLocation();

        double x = chunkLocation.getX();
        double z = chunkLocation.getZ();

        return getHighestBlock(new Location(chunk.getWorld(), getRandom(x, x + 15, false), 0, getRandom(z, z + 15, false)));
    }

    private Block getHighestBlock(Location location) {
        World world = location.getWorld();
        if (world == null) {
            return null;
        }

        Block block = world.getHighestBlockAt(location);

        // getHighestBlockAt() returns block over highest block? It always is a block of type air
        if (block.getType() == Material.AIR) {
            Location blockLocation = block.getLocation();
            blockLocation.subtract(0, 1, 0);
            block = world.getBlockAt(blockLocation);
        }

        return block;
    }

    private double getRandom(double min, double max, boolean allowNegative) {
        // nextDouble() excludes the max value, therefore add 1 to the max value
        double random = ThreadLocalRandom.current().nextDouble(min, max + 1);

        // Get random negative value if allowed
        if (allowNegative && ThreadLocalRandom.current().nextBoolean()) {
            return -random;
        } else {
            return random;
        }
    }
}
