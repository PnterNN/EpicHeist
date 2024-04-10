package mc.pnternn.epicheist.guis;

import mc.obliviate.inventory.configurable.ConfigurableGui;
import mc.obliviate.util.string.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;

public class AdminPanelGui extends ConfigurableGui {
    public AdminPanelGui(@NotNull Player player) {
        super(player, "admin-panel-gui");
        setTitle(StringUtil.parseColor(getSection().getString("title")));
    }
    private void putContent() {
        putDysfunctionalIcons();
        addConfigIcon("filler-item");
        addConfigIcon("locations").onClick(e -> {
            new AdminPanelLocationsGui(player).open();
        });
        addConfigIcon("rewards").onClick(e -> {
            new AdminPanelRewardsGui(player).open();
        });
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        putContent();
    }

}
