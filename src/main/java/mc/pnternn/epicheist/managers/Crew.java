package mc.pnternn.epicheist.managers;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class Crew {
    private String name;
    private UUID id;
    private Player leader;
    private List<Player> members;
    public Crew(Player paramPlayer, List<Player> paramList) {
        this.name = paramPlayer.getName() + "'s Crew";
        this.id = UUID.randomUUID();
        this.leader = paramPlayer;
        this.members = paramList;
    }
    public UUID getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }

    public Player getLeader() {
        return this.leader;
    }

    public List<Player> getMembers() {
        return this.members;
    }
}
