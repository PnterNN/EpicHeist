package mc.pnternn.epicheist.util;

import mc.pnternn.epicheist.EpicHeist;
import mc.pnternn.epicheist.managers.Crew;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.UUID;

public class Util {
    public static void giveOrDrop(Player player, ItemStack item) {
        if (Util.inventoryHasSpace(player.getInventory())) {
            player.getInventory().addItem(item);
        } else {
            player.getWorld().dropItem(player.getLocation(), item);
        }
    }
    public static int getCrewOnlineMembers(Crew crew) {
        int x = 0;
        for (OfflinePlayer player : crew.getMembers()) {
            if (player.isOnline()) {
                x++;
            }
        }
        if (crew.getLeader().isOnline()) {
            x++;
        }
        return x;
    }
    public static Crew bestCrew() {
        Crew best = null;
        double maxvaluePerPlayer = 0.0;
        int x= 0;
        for(Crew crew : EpicHeist.getInstance().getCrewManager().getCrewList()){
            x = 0;
            for(OfflinePlayer player : crew.getMembers()){
                if(player.isOnline()){
                    x++;
                }
            }
            if(crew.getLeader().isOnline()){
                x++;
            }
            if(x>0){
                if(EpicHeist.getMatch().getDataHolder().getCrewCollectedGold().get(crew.getId())/x > maxvaluePerPlayer){
                    maxvaluePerPlayer = EpicHeist.getMatch().getDataHolder().getCrewCollectedGold().get(crew.getId())/x;
                    best = crew;
                }
            }
        }
        return best;
    }

    public static boolean inventoryHasSpace(Inventory inventory) {
        return Arrays.stream(inventory.getContents())
                .anyMatch(item -> item == null || item.getType() == Material.AIR);
    }
}
