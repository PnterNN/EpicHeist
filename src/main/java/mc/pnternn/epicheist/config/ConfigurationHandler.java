package mc.pnternn.epicheist.config;

import mc.pnternn.epicheist.EpicHeist;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

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

    public static YamlConfiguration getConfig() {
        return config;
    }
}
