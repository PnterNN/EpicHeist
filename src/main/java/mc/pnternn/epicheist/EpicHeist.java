package mc.pnternn.epicheist;

import mc.pnternn.epicheist.commands.HeistCommand;
import mc.pnternn.epicheist.config.ConfigurationHandler;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class EpicHeist extends JavaPlugin {

    private final ConfigurationHandler configurationHandler = new ConfigurationHandler();
    public Economy economy;
    private Permission permission;

    @Override
    public void onEnable() {
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
        Objects.requireNonNull(super.getCommand("heist")).setExecutor(new HeistCommand());
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private void setupPlaceholderAPI(){
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Bukkit.getPluginManager().registerEvents((Listener) this, this);
        } else {
            getLogger().warning("Could not find PlaceholderAPI! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
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

    public Economy getEconomy() {
        return economy;
    }

    public Permission getPermissions() {
        return permission;
    }

}
