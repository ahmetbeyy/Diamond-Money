package me.ariscript.diamondmoney;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Bu komutu sadece oyuncular kullanabilir.");
            return true;
        }

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("kese")) {
            // /kese → bakiye göster
            if (args.length == 0) {
                int balance = BalanceManager.getBalance(player.getUniqueId());
                player.sendMessage("💰 Bakiyen: " + balance + " para");
                return true;
            }

            // /kese <miktar>
            if (args.length == 1) {
                int count;

                try {
                    count = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    player.sendMessage("❌ Geçerli bir sayı gir.");
                    return true;
                }

                if (count <= 0) {
                    player.sendMessage("❌ Sıfırdan büyük bir sayı gir.");
                    return true;
                }

                ItemStack diamonds = new ItemStack(Material.DIAMOND);
                int playerDiamondCount = 0;

                // Elmas sayısını bul
                for (ItemStack item : player.getInventory().getContents()) {
                    if (item != null && item.getType() == Material.DIAMOND) {
                        playerDiamondCount += item.getAmount();
                    }
                }

                if (playerDiamondCount < count) {
                    player.sendMessage("❌ Yeterli elmasın yok. Sahip olduğun: " + playerDiamondCount);
                    return true;
                }

                // Elmasları azalt
                int toRemove = count;
                for (int i = 0; i < player.getInventory().getSize(); i++) {
                    ItemStack item = player.getInventory().getItem(i);
                    if (item != null && item.getType() == Material.DIAMOND) {
                        int amount = item.getAmount();
                        if (amount <= toRemove) {
                            player.getInventory().setItem(i, null);
                            toRemove -= amount;
                        } else {
                            item.setAmount(amount - toRemove);
                            break;
                        }
                    }
                }

                BalanceManager.addBalance(player.getUniqueId(), count);
                player.sendMessage("✅ " + count + " elmas başarıyla " + count + " paraya çevrildi.");
                return true;
            }

            player.sendMessage("❌ Kullanım: /kese veya /kese <miktar>");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("iban")) {
            if (args.length != 2) {
                player.sendMessage("❌ Kullanım: /iban <oyuncu> <miktar>");
                return true;
            }

            Player hedef = Bukkit.getPlayerExact(args[0]);
            if (hedef == null || !hedef.isOnline()) {
                player.sendMessage("❌ Oyuncu çevrimdışı.");
                return true;
            }

            int miktar;
            try {
                miktar = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage("❌ Lütfen geçerli bir sayı gir.");
                return true;
            }

            if (miktar <= 0) {
                player.sendMessage("❌ Pozitif bir miktar gir.");
                return true;
            }

            if (!BalanceManager.removeBalance(player.getUniqueId(), miktar)) {
                player.sendMessage("❌ Yeterli bakiyen yok.");
                return true;
            }

            BalanceManager.addBalance(hedef.getUniqueId(), miktar);
            player.sendMessage("📤 " + hedef.getName() + " adlı kişiye " + miktar + " para yolladın.");
            hedef.sendMessage("📥 " + player.getName() + " sana " + miktar + " para yolladı!");
            return true;
        }

        return false;
    }
}

