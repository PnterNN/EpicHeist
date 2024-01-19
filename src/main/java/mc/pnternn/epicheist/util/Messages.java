package mc.pnternn.epicheist.util;

import org.bukkit.configuration.file.FileConfiguration;

public class Messages {
    public static Messages instance;

    public Messages(FileConfiguration fc) {
        instance = this;
    }

    private String colorize(String str) {
        str = str.replace("&", "\u00a7");
        return str;
    }
}