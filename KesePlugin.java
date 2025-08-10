package me.ariscript.diamondmoney;

import org.bukkit.plugin.java.JavaPlugin;

public class KesePlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getCommand("kese").setExecutor(new Commands());
        getCommand("iban").setExecutor(new Commands());
        BalanceManager.setup(this);
        getLogger().info("KesePlugin aktif!");
    }

    @Override
    public void onDisable() {
        BalanceManager.save();
        getLogger().info("KesePlugin kapatıldı.");
    }
}
