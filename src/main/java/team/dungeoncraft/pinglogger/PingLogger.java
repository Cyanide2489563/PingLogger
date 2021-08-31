package team.dungeoncraft.pinglogger;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class PingLogger extends JavaPlugin {
    
    private PingLogger plugin;
    private LocalDateTime localDateTime = LocalDateTime.now();
    
    @Override
    public void onLoad() {
        plugin = this;
    }
    
    @Override
    public void onEnable() {
        var pluginDir = plugin.getDataFolder();
        if (!pluginDir.exists()) {
            pluginDir.mkdirs();
        }
        
        new BukkitRunnable() {
            @Override
            public void run() {
                if (isAlreadyPastMidnight(localDateTime)) {
                    localDateTime = LocalDateTime.now();
                }
                var logFile = new File(pluginDir, localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE) + ".csv");
                var players = plugin.getServer().getOnlinePlayers();
                players.forEach(player -> logInfo(player, logFile));
            }
        }.runTaskTimerAsynchronously(plugin, 0, 5*60*20);
    }
    
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    
    private void logInfo(Player player, File logFile) {
        var playerId = player.getDisplayName();
        int ping = ((CraftPlayer) player).getHandle().ping;
        var localDateTime = LocalDateTime.now();
        
        try (var bufferedWriter = new BufferedWriter(new FileWriter(logFile, true))) {
            bufferedWriter.write(
                    playerId + "," +
                    localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd-hh-mm")) + "," +
                    ping);
            bufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private boolean isAlreadyPastMidnight(LocalDateTime localDateTime) {
        return this.localDateTime.equals(localDateTime);
    }
}
