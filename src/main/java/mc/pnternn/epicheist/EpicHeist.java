package mc.pnternn.epicheist;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import mc.pnternn.epicheist.Expansions.HeistPlaceholder;
import mc.pnternn.epicheist.commands.HeistCommand;
import mc.pnternn.epicheist.config.ConfigurationHandler;
import mc.pnternn.epicheist.game.Match;
import mc.pnternn.epicheist.listeners.onCommand;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class EpicHeist extends JavaPlugin implements Listener {

    private final ConfigurationHandler configurationHandler = new ConfigurationHandler();
    public Economy economy;
    private Permission permission;
    private static Match match;
    private ProtocolManager manager;

    @Override
    public void onEnable() {
        manager = ProtocolLibrary.getProtocolManager();
        configurationHandler.init();
        this.getLogger().info("EpicHeist created by PnterNN");
        this.getLogger().info("Discord: PnterNN#8478");
        if (!this.setupEconomy()) {
            this.getLogger().severe("Vault not found, plugin is closing.");
            this.getServer().getPluginManager().disablePlugin((Plugin)this);
            return;
        }
        this.setupPlaceholderAPI();
        this.setupPermissions();
        getServer().getPluginManager().registerEvents(new onCommand(), this);
        Objects.requireNonNull(super.getCommand("heist")).setExecutor(new HeistCommand());
    }

    private void setupPlaceholderAPI() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Bukkit.getPluginManager().registerEvents(this, this);
            new HeistPlaceholder().register();
        } else {
            getLogger().warning("Could not find PlaceholderAPI! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }
    @Override
    public void onDisable() {
        super.onDisable();
    }
    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("vault") == null) {
            return false;
        }
        RegisteredServiceProvider rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        this.economy = (Economy)rsp.getProvider();
        return this.economy != null;
    }
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        permission = rsp.getProvider();
        return permission != null;
    }


    public static EpicHeist getInstance(){
        return JavaPlugin.getPlugin(EpicHeist.class);
    }
    public static Match getMatch() {
        return match;
    }
    public static void setMatch(Match match) {
        EpicHeist.match = match;
    }
    public Economy getEconomy() {
        return economy;
    }


    public ProtocolManager getProtocolManager() {
        return manager;
    }

    public Permission getPermissions() {
        return permission;
    }

}
