package mc.pnternn.epicheist.util;

import mc.pnternn.epicheist.EpicHeist;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class ColorUtil {
    public static String colorize(String string) {
        if(string== null) return null;
        return string.replace("&", "\u00a7");
    }
    public static void showTitle(Player player, String bcolor, String color, String name, String subname) {
        if (bcolor != color) {
            String[] nameArray = name.split("");
            int length = nameArray.length;
            String[] nameDisplay = new String[(length * 2) + 1];
            for (int r = 0; r < length; r++) {
                String formattedName = "";
                for (int n = 0; n < nameArray.length; n++) {
                    if (n >= r) {
                        formattedName = bcolor + "&l" + formattedName + color + "&l" + nameArray[n];
                    } else {
                        formattedName = bcolor + "&l" + formattedName + nameArray[n];
                    }
                }
                nameDisplay[r] = formattedName;
            }
            for (int r = 0; r < length; r++) {
                String formattedName = "";
                for (int n = 0; n < nameArray.length; n++) {
                    if (n >= r) {
                        formattedName = color + "&l" + formattedName + bcolor + "&l" + nameArray[n];
                    } else {
                        formattedName = color + "&l" + formattedName + nameArray[n];
                    }
                }
                nameDisplay[r + length] = formattedName;
            }
            AtomicInteger x = new AtomicInteger();
            for (int n = 0; n < 100; n++) {
                Bukkit.getScheduler().runTaskLater(EpicHeist.getInstance(), () -> {
                    x.getAndIncrement();
                    if (x.get() > length * 2) {
                        x.set(1);
                    }
                    player.sendTitle(colorize(nameDisplay[x.get()]), colorize(subname), 0, 5, 0);
                }, n);
            }
            player.sendTitle("", "", 0, 5, 0);
        } else {
            player.sendTitle(colorize(color + "&l" + name), colorize(subname), 0, 5, 0);
        }
    }
}
