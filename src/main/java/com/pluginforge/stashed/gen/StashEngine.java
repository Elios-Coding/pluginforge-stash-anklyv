package com.pluginforge.stashed.gen;

import com.pluginforge.stashed.StashedMain;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class StashEngine {

    private final StashedMain plugin;
    private final Random random = new Random();

    public StashEngine(StashedMain plugin) {
        this.plugin = plugin;
    }

    public StashedMain getPlugin() {
        return plugin;
    }

    public boolean deploy(Player player) {
        Location origin = player.getLocation();
        int floorY = origin.getBlockY() - 6;

        if (floorY < origin.getWorld().getMinHeight() + 5) {
            return false;
        }

        // Room dimensions: 3x3x3 interior
        // We clear a 5x5x5 area to account for walls
        
        // 1. Build the vault box (Obsidian/Stone Mix for SMP feel)
        for (int x = -2; x <= 2; x++) {
            for (int y = -1; y <= 3; y++) {
                for (int z = -2; z <= 2; z++) {
                    Location loc = origin.clone().add(x, -5 + y, z);
                    Block block = loc.getBlock();

                    // If it's the outer shell
                    if (x == -2 || x == 2 || y == -1 || y == 3 || z == -2 || z == 2) {
                        // Blend shell with stone/deepslate or obsidian
                        if (random.nextDouble() < 0.15) {
                            block.setType(Material.OBSIDIAN);
                        } else {
                            block.setType(loc.getY() < 0 ? Material.DEEPSLATE : Material.STONE);
                        }
                    } else {
                        // Interior air
                        block.setType(Material.AIR);
                    }
                }
            }
        }

        // 2. Add Utility / Interior
        Location interiorBase = origin.clone().add(0, -5, 0);
        
        // Floor
        interiorBase.clone().add(0, 0, 0).getBlock().setType(Material.CHISELED_STONE_BRICKS);
        
        // Storage
        Block chestBlock = interiorBase.clone().add(1, 1, 0).getBlock();
        chestBlock.setType(Material.CHEST);
        if (chestBlock.getState() instanceof Chest) {
            Chest chest = (Chest) chestBlock.getState();
            Inventory inv = chest.getInventory();
            inv.addItem(new ItemStack(Material.BREAD, 16));
            inv.addItem(new ItemStack(Material.TORCH, 8));
        }

        // Crafting/Utility
        interiorBase.clone().add(-1, 1, 0).getBlock().setType(Material.CRAFTING_TABLE);
        interiorBase.clone().add(0, 1, 1).getBlock().setType(Material.FURNACE);
        interiorBase.clone().add(0, 2, 0).getBlock().setType(Material.LANTERN);

        // 3. Concealed Entrance
        // We use a "gravity-block" concealment or a simple matching surface block.
        // The entrance is a single vertical drop via a Scaffolding block hidden under a natural block.
        Location surfaceEntry = origin.clone();
        Material surfaceMat = surfaceEntry.getBlock().getType();
        if (surfaceMat == Material.AIR) surfaceMat = Material.GRASS_BLOCK;

        // Create the drop shaft
        for (int y = -4; y <= -1; y++) {
            origin.clone().add(0, y, 0).getBlock().setType(Material.SCAFFOLDING);
        }

        // The "Trapdoor" - we use a Carpet or a block that looks natural
        Block cover = surfaceEntry.getBlock();
        if (cover.getType().isAir() || cover.getType() == Material.GRASS || cover.getType() == Material.TALL_GRASS) {
            cover.setType(Material.MOSS_CARPET); // Harder to see on grass
        }

        player.playSound(player.getLocation(), Sound.BLOCK_DEEPSLATE_BRICKS_PLACE, 1f, 0.5f);
        return true;
    }
}
