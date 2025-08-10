package me.ariscript.diamondmoney;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class BalanceManager {
    private static final HashMap<UUID, Integer> balances = new HashMap<>();
    private static File dataFile;
    private static FileConfiguration dataConfig;

    public static void setup(KesePlugin plugin) {
        dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.getParentFile().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("data.yml dosyası oluşturulamadı!");
            }
        }

        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        // YAML'den belleğe yükle
        for (String key : dataConfig.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            int balance = dataConfig.getInt(key);
            balances.put(uuid, balance);
        }
    }

    public static void save() {
        for (UUID uuid : balances.keySet()) {
            dataConfig.set(uuid.toString(), balances.get(uuid));
        }

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            System.out.println("data.yml kaydedilemedi!");
        }
    }

    public static int getBalance(UUID uuid) {
        return balances.getOrDefault(uuid, 0);
    }

    public static void addBalance(UUID uuid, int amount) {
        balances.put(uuid, getBalance(uuid) + amount);
    }

    public static boolean removeBalance(UUID uuid, int amount) {
        if (getBalance(uuid) >= amount) {
            balances.put(uuid, getBalance(uuid) - amount);
            return true;
        }
        return false;
    }
}
