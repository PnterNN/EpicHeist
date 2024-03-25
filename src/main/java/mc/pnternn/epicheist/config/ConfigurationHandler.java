package mc.pnternn.epicheist.config;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.util.WorldEditRegionConverter;
import mc.pnternn.epicheist.EpicHeist;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigurationHandler {
    private static YamlConfiguration config;

    public void init(){
        loadConfigFile();
    }

    private void loadConfigFile(){
        File configFile = new File(EpicHeist.getInstance().getDataFolder().getPath() + File.separator + "config.yml");
        if(!configFile.exists()){
            EpicHeist.getInstance().saveResource("config.yml",true);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }
    public static void reloadConfig(){
        File configFile = new File(EpicHeist.getInstance().getDataFolder().getPath() + File.separator + "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public static YamlConfiguration getConfig() {
        return config;
    }
    public static void saveConfig(){
        File configFile = new File(EpicHeist.getInstance().getDataFolder().getPath() + File.separator + "config.yml");
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Region getVaultRegion() {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(Bukkit.getWorld(getValue("regions.world"))));
        return WorldEditRegionConverter.convertToRegion(regions.getRegion(ConfigurationHandler.getValue("regions.vault-name")));
    }
    public static String getTimeFormat(int day, int hour, int minute, int second) {
        if(day != 0){
            return getValue("time-formats.day")
                    .replace("{day}", String.valueOf(day))
                    .replace("{hour}", String.valueOf(hour))
                    .replace("{minute}", String.valueOf(minute))
                    .replace("{second}", String.valueOf(second));
        }else if(hour != 0) {
            return getValue("time-formats.hour")
                    .replace("{day}", String.valueOf(day))
                    .replace("{hour}", String.valueOf(hour))
                    .replace("{minute}", String.valueOf(minute))
                    .replace("{second}", String.valueOf(second));
        }else if(minute != 0) {
            return getValue("time-formats.minute")
                    .replace("{day}", String.valueOf(day))
                    .replace("{hour}", String.valueOf(hour))
                    .replace("{minute}", String.valueOf(minute))
                    .replace("{second}", String.valueOf(second));
        }else if(second != 0) {
            return getValue("time-formats.second")
                    .replace("{day}", String.valueOf(day))
                    .replace("{hour}", String.valueOf(hour))
                    .replace("{minute}", String.valueOf(minute))
                    .replace("{second}", String.valueOf(second));
        }else{
            return "...";
        }
    }

    public static String getValue(String key){
        return getConfig().getString(key);
    }
}
