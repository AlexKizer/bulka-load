package com.planetarypvp.bulka.loader.example;

import com.planetarypvp.bulka.loader.BukkitLoader;
import com.planetarypvp.bulka.loader.LoadSettingsException;
import org.bukkit.plugin.java.JavaPlugin;

public class BulkaLoaderPlugin extends JavaPlugin
{
    public void onEnable()
    {
        getLogger().info("BulkaLoader test enabled.");

        try {
            BukkitLoader.load(this, "/settings/bulkaloader-settings/");
        } catch (LoadSettingsException e) {
            e.printStackTrace();
            return;
        }

        Test test = new Test();
    }

    public void onDisable()
    {
        getLogger().info("BulkaLoader test disabled.");
    }
}
