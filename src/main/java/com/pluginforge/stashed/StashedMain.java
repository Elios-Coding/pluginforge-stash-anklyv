package com.pluginforge.stashed;

import org.bukkit.plugin.java.JavaPlugin;
import com.pluginforge.stashed.gen.StashEngine;
import com.pluginforge.stashed.cmd.StashTrigger;

public class StashedMain extends JavaPlugin {

    private StashEngine engine;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.engine = new StashEngine(this);
        getCommand("stash").setExecutor(new StashTrigger(engine));
    }

    @Override
    public void onDisable() {
        // Cleanup if necessary
    }
}
