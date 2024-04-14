package mc.pnternn.epicheist.managers;

import mc.pnternn.epicheist.EpicHeist;
import mc.pnternn.epicheist.config.ConfigurationHandler;
import mc.pnternn.epicheist.util.ColorUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Crew {
    FileConfiguration crewFile;
    private String name;
    private UUID id;
    private OfflinePlayer leader;
    private List<OfflinePlayer> members;
    public Crew(File file){
        this.crewFile = YamlConfiguration.loadConfiguration(file);
        this.name = this.crewFile.getString("name");
        this.id = UUID.fromString(this.crewFile.getString("id"));
        this.leader = EpicHeist.getInstance().getServer().getOfflinePlayer(UUID.fromString(this.crewFile.getString("leader")));
        this.members = (List<OfflinePlayer>) this.crewFile.getList("members");
    }
    public Crew(UUID id, OfflinePlayer leader, String name, List<OfflinePlayer> members){
        this.id = id;
        this.leader = leader;
        this.name = name;
        this.members = members;
    }
    public Crew(OfflinePlayer paramPlayer, List<OfflinePlayer> paramList) {
        this.name = paramPlayer.getName() + "s Crew";
        this.id = UUID.randomUUID();
        this.leader = paramPlayer;
        this.members = paramList;
        if(!ConfigurationHandler.getValue("sql.enabled").equals("true")){
            File crewFile = new File(EpicHeist.getInstance().getDataFolder()+File.separator+"crews"+File.separator+this.id+".yml");
            if(!crewFile.exists()) {
                try {
                    crewFile.createNewFile();
                    this.crewFile = YamlConfiguration.loadConfiguration(crewFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            this.crewFile.set("name", this.name);
            this.crewFile.set("id", this.id.toString());
            this.crewFile.set("leader", this.leader.getUniqueId().toString());
            this.crewFile.set("members", this.members);
            saveConfig();
        }else{
            EpicHeist.getInstance().getMySql().createCrew(this.id.toString(), this.name, this.leader.getUniqueId().toString());
            for(OfflinePlayer player : this.members){
                EpicHeist.getInstance().getMySql().addMember(this.id.toString(), player.getUniqueId().toString());
            }
        }
    }
    public void saveConfig(){
        try {
            File crewFile = new File(EpicHeist.getInstance().getDataFolder()+File.separator+"crews"+File.separator+this.id+".yml");
            this.crewFile.save(crewFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public UUID getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String paramString) {
        this.name = paramString;
        if(!ConfigurationHandler.getValue("sql.enabled").equals("true")) {
            this.crewFile.set("name", paramString);
            saveConfig();
        }else{
            EpicHeist.getInstance().getMySql().renameCrew(this.id.toString(), paramString);
        }
    }

    public OfflinePlayer getLeader() {
        return this.leader;
    }
    public void setLeader(OfflinePlayer paramPlayer) {
        this.leader = paramPlayer;
        if(!ConfigurationHandler.getValue("sql.enabled").equals("true")) {
            this.crewFile.set("leader", paramPlayer.getUniqueId().toString());
            saveConfig();
        }else{
            EpicHeist.getInstance().getMySql().setLeader(this.id.toString(), paramPlayer.getUniqueId().toString());
        }
    }

    public List<Player> getOnlineMembers() {
        List<Player> onlineMembers = new ArrayList<>();
        for(OfflinePlayer player : this.members){
            if(player.isOnline()){
                onlineMembers.add(player.getPlayer());
            }
        }
        return onlineMembers;
    }
    public List<OfflinePlayer> getMembers() {
        return this.members;
    }
}
