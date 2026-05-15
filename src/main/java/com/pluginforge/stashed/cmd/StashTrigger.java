package com.pluginforge.stashed.cmd;

import com.pluginforge.stashed.gen.StashEngine;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StashTrigger implements CommandExecutor {

    private final StashEngine engine;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public StashTrigger(StashEngine engine) {
        this.engine = engine;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can create stashes.");
            return true;
        }

        Player player = (Player) sender;
        
        if (!player.hasPermission("stashed.use")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to create a hidden stash.");
            return true;
        }

        long now = System.currentTimeMillis();
        long cooldownTime = engine.getPlugin().getConfig().getLong("cooldown-seconds", 60) * 1000;
        
        if (cooldowns.containsKey(player.getUniqueId())) {
            long remaining = (cooldowns.get(player.getUniqueId()) + cooldownTime) - now;
            if (remaining > 0) {
                player.sendMessage(ChatColor.RED + "You must wait " + (remaining / 1000) + "s before stashing again.");
                return true;
            }
        }

        boolean success = engine.deploy(player);
        
        if (success) {
            cooldowns.put(player.getUniqueId(), now);
            player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "STASH DEPLOYED. " + ChatColor.GRAY + "A concealed vault has been integrated into the terrain below you.");
        } else {
            player.sendMessage(ChatColor.RED + "Stash deployment failed. Are you too deep or is the area obstructed?");
        }

        return true;
    }
}
