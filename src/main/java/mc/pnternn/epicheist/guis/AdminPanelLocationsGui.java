package mc.pnternn.epicheist.guis;

import mc.obliviate.inventory.configurable.ConfigurableGui;
import mc.obliviate.util.string.StringUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;

public class AdminPanelLocationsGui extends ConfigurableGui {
    public AdminPanelLocationsGui(@NotNull Player player) {
        super(player, "admin-panel-gui-locations");
        setTitle(StringUtil.parseColor(getSection().getString("title")));
    }
    private void putContent() {
        putDysfunctionalIcons();
        addConfigIcon("filler-item");
        addConfigIcon("bank-spawn-location").onClick(e -> {
            player.performCommand("heist setBankSpawn");
            player.closeInventory();
        });
        addConfigIcon("jail-spawn-location").onClick(e -> {
            player.performCommand("heist setJailSpawn");
            player.closeInventory();
        });
        addConfigIcon("swat-spawn-location").onClick(e -> {
            player.performCommand("heist setSwatSpawn");
            player.closeInventory();
        });
        addConfigIcon("back-menu").onClick(e -> {
            setClosed(true);
            new AdminPanelGui(player).open();
        });
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        putContent();
    }

}
