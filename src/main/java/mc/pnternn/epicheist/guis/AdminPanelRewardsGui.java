package mc.pnternn.epicheist.guis;

import mc.obliviate.inventory.configurable.ConfigurableGui;
import mc.obliviate.util.string.StringUtil;
import mc.pnternn.epicheist.guis.rewards.FirstCrewGui;
import mc.pnternn.epicheist.guis.rewards.SecondCrewGui;
import mc.pnternn.epicheist.guis.rewards.ThirdCrewGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;

public class AdminPanelRewardsGui extends ConfigurableGui {
    public AdminPanelRewardsGui(@NotNull Player player) {
        super(player, "admin-panel-gui-rewards");
        setTitle(StringUtil.parseColor(getSection().getString("title")));
    }
    private void putContent() {
        putDysfunctionalIcons();
        addConfigIcon("filler-item");
        addConfigIcon("back-menu").onClick(e -> {
            setClosed(true);
            new AdminPanelGui(player).open();
        });
        addConfigIcon("first-crew").onClick(e -> {
            new FirstCrewGui(player).open();
        });
        addConfigIcon("second-crew").onClick(e -> {
            new SecondCrewGui(player).open();
        });
        addConfigIcon("third-crew").onClick(e -> {
            new ThirdCrewGui(player).open();
        });
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        putContent();
    }

}
