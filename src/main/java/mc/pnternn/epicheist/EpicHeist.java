package mc.pnternn.epicheist;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import mc.obliviate.inventory.InventoryAPI;
import mc.obliviate.inventory.configurable.GuiConfigurationTable;
import mc.pnternn.epicheist.game.GameState;
import mc.pnternn.epicheist.game.state.EscapingState;
import mc.pnternn.epicheist.game.state.PlayingState;
import mc.pnternn.epicheist.game.state.SwatState;
import mc.pnternn.epicheist.game.state.WaitingState;
import mc.pnternn.epicheist.listeners.RegionEventsListener;
import mc.pnternn.epicheist.listeners.HeistListeners;
import mc.pnternn.epicheist.expansions.HeistPlaceholder;
import mc.pnternn.epicheist.commands.HeistCommand;
import mc.pnternn.epicheist.config.ConfigurationHandler;
import mc.pnternn.epicheist.game.Match;
import mc.pnternn.epicheist.listeners.OnCommand;
import mc.pnternn.epicheist.managers.Crew;
import mc.pnternn.epicheist.managers.CrewManager;
import mc.pnternn.epicheist.managers.RedisManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import java.io.File;
import java.util.Objects;

public class EpicHeist extends JavaPlugin implements Listener {

    private final ConfigurationHandler configurationHandler = new ConfigurationHandler();
    public static Economy economy;
    private Permission permission;
    private static Match match;
    private ProtocolManager manager;
    private CrewManager crewManager;
    private RedisManager redisManager;

    @Override
    public void onEnable() {
        this.crewManager = new CrewManager();
        File folder = new File(getDataFolder()+File.separator+"crews");
        if(!folder.exists()) folder.mkdirs();
        for (File file : folder.listFiles()) {
            if (file.isFile()) {
                crewManager.addCrew(new Crew(file));
            }
        }

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
        if(ConfigurationHandler.getValue("main-server").equals("true")){
            getServer().getPluginManager().registerEvents(new OnCommand(), this);
            getServer().getPluginManager().registerEvents(new RegionEventsListener(), this);
            getServer().getPluginManager().registerEvents(new HeistListeners(), this);
        }

        Objects.requireNonNull(super.getCommand("heist")).setExecutor(new HeistCommand());
        loadGuis();
        if(ConfigurationHandler.getValue("redis.enabled").equalsIgnoreCase("true")){
            redisManager = new RedisManager(ConfigurationHandler.getValue("redis.host"), Integer.parseInt(ConfigurationHandler.getValue("redis.port")));
        }
        if(ConfigurationHandler.getValue("main-server").equals("true")){
            match = new Match();
            match.start();
        }else{
            JSONObject obj = new JSONObject();
            obj.put("type", "SEND_TIMER");
            redisManager.publish(ConfigurationHandler.getValue("redis.channel"), obj);
        }
    }

    private void loadGuis() {
        new InventoryAPI(this).init();

        YamlConfiguration section = YamlConfiguration.loadConfiguration(new File(getDataFolder() + File.separator + "menus.yml"));
        if (section.getKeys(false).isEmpty()) {
            saveResource("menus.yml", true);
            section = YamlConfiguration.loadConfiguration(new File(getDataFolder() + File.separator + "menus.yml"));
        }
        GuiConfigurationTable.setDefaultConfigurationTable(new GuiConfigurationTable(section));
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
        if(redisManager != null){
            redisManager.close();
        }
        if(match != null){
            EpicHeist.getMatch().stop();
        }
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


    public RedisManager getRedisManager() {
        return redisManager;
    }
    public CrewManager getCrewManager() {
        return crewManager;
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
    public static Economy getEconomy() {
        return economy;
    }
    public ProtocolManager getProtocolManager() {
        return manager;
    }

    public Permission getPermissions() {
        return permission;
    }

}
