package org.splendid.cekilis;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CekilisPlugin extends JavaPlugin implements Listener {

    private boolean cekilisAktif = false;
    private List<String> aktifOyuncular = new ArrayList<>();

    @Override
    public void onEnable() {
        getLogger().info("CekilisPlugin has been enabled!");

        // Listener'ı kaydet
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("CekilisPlugin has been disabled!");
    }

    // /cekilis komutunu işle
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("cekilis")) {
            if (sender.hasPermission("cekilis.kontrol")) {
                if (!cekilisAktif) {
                    cekilisAktif = true;

                    // Aktif oyuncuları al
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        aktifOyuncular.add(player.getName());
                    }

                    if (!aktifOyuncular.isEmpty()) {
                        baslatCekilis();
                        sender.sendMessage(ChatColor.GREEN + "Çekiliş başladı! Kazanan birkaç saniye içinde belirlenecek.");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Çekilişe katılacak oyuncu bulunamadı!");
                        cekilisAktif = false; // Çekiliş iptal edildi
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Çekiliş zaten devam ediyor!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Bu komutu kullanma izniniz yok!");
            }
            return true;
        }
        return false;
    }

    // Oyuncu oyuna girdiğinde olayı dinle
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (cekilisAktif) {
            Player player = event.getPlayer();
            aktifOyuncular.add(player.getName());
        }
    }

    private void baslatCekilis() {
        new BukkitRunnable() {
            int sayac = 10;

            @Override
            public void run() {
                if (sayac > 0) {
                    // Geriye sayımı sohbet kısmında yayınla
                    Bukkit.broadcastMessage(ChatColor.GREEN + "[Çekiliş] Çekiliş sona erene kadar " + sayac + " saniye kaldı!");
                    sayac--;
                } else {
                    // Çekilişi sonlandır ve kazananı belirle
                    if (!aktifOyuncular.isEmpty()) {
                        Random random = new Random();
                        int kazananIndex = random.nextInt(aktifOyuncular.size());
                        String kazananOyuncu = aktifOyuncular.get(kazananIndex);

                        // Kazananı bildir
                        Bukkit.broadcastMessage(ChatColor.GREEN + "[Çekiliş] Çekiliş sona erdi! Kazanan: " + kazananOyuncu);

                        // Çekilişi sıfırla
                        sifirlaCekilis();
                    } else {
                        Bukkit.broadcastMessage(ChatColor.RED + "[Çekiliş] Hiç oyuncu çekilişe katılmadı. Çekiliş iptal edildi.");

                        // Çekilişi sıfırla
                        sifirlaCekilis();
                    }

                    // Görevi durdur
                    cancel();
                }
            }
        }.runTaskTimer(this, 0, 20); // 20 tick (1 saniye) aralıklarla çalıştır
    }

    private void sifirlaCekilis() {
        cekilisAktif = false;
        aktifOyuncular.clear();
    }
}
