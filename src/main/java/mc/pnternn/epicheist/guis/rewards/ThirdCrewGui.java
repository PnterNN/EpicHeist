package mc.pnternn.epicheist.guis.rewards;

import mc.obliviate.inventory.Icon;
import mc.obliviate.inventory.configurable.ConfigIcon;
import mc.obliviate.inventory.configurable.ConfigurableGui;
import mc.obliviate.util.string.StringUtil;
import mc.pnternn.epicheist.config.ConfigurationHandler;
import mc.pnternn.epicheist.guis.AdminPanelGui;
import mc.pnternn.epicheist.guis.AdminPanelRewardsGui;
import mc.pnternn.epicheist.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ThirdCrewGui extends ConfigurableGui {

    public ThirdCrewGui(@NotNull Player player) {
        super(player, "admin-panel-gui-rewards-third");
        setTitle(StringUtil.parseColor(getSection().getString("title")));
    }

    @Override
    public boolean onClick(InventoryClickEvent event) {
        if(event.getSlot() > 9){
            return true;
        }
        return false;
    }

    private void putrewards(){
        List<ItemStack> items = (List<ItemStack>) ConfigurationHandler.getConfig().getList("rewards.3");
        for(ItemStack item : items){
            Icon icon = new Icon(item);
            icon.onClick(e -> {
                Util.giveOrDrop(player, item);
                ConfigurationHandler.getConfig().getList("rewards.3").remove(item);
                ConfigurationHandler.saveConfig();
                getInventory().remove(item);
            });
            addItem(icon);
        }
    }

    private void putContent() {
        putDysfunctionalIcons();
        addConfigIcon("filler-item");
        addConfigIcon("save-item").onClick(e -> {
            Bukkit.broadcastMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&7all items saved!"));
            List<ItemStack> items = new ArrayList<>();
            for(int i = 9; i < 54; i++) {
                ItemStack item = getInventory().getItem(i);
                if (Objects.nonNull(item) && item.getType() != Material.AIR) {
                    items.add(item);
                }
            }
            ConfigurationHandler.getConfig().set("rewards.3", items);
            ConfigurationHandler.saveConfig();
        });
        addConfigIcon("back-menu").onClick(e -> {
            new AdminPanelRewardsGui(player).open();
        });
    }
    @Override
    public void onOpen(InventoryOpenEvent event) {
        putContent();
        putrewards();
    }
}
