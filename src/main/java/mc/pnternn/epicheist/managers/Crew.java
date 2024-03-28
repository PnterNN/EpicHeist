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
    public Crew(OfflinePlayer paramPlayer, List<OfflinePlayer> paramList) {
        this.name = paramPlayer.getName() + "'s Crew";
        this.id = UUID.randomUUID();
        this.leader = paramPlayer;
        this.members = paramList;
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
        this.crewFile.set("name", paramString);
        saveConfig();
    }

    public OfflinePlayer getLeader() {
        return this.leader;
    }

    public List<OfflinePlayer> getMembers() {
        return this.members;
    }
}
