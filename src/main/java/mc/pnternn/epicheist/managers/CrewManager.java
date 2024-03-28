package mc.pnternn.epicheist.managers;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.Optional;
public class CrewManager {
    private List<Crew> crewList = new ArrayList<>();
    private HashMap<UUID, Crew> pendingInvites = new HashMap<>();

    public List<Crew> getCrewList() {
        return this.crewList;
    }

    public void addCrew(Crew paramCrew) {
        if (!this.crewList.contains(paramCrew))
            this.crewList.add(paramCrew);
    }

    public void removeCrew(Crew paramCrew) {
        this.crewList.remove(paramCrew);
    }

    public void addPendingInvite(Player paramPlayer, Crew paramCrew) {
        this.pendingInvites.put(paramPlayer.getUniqueId(), paramCrew);
    }

    public void removePendingInvite(Player paramPlayer, Crew paramCrew) {
        Optional<Map.Entry<UUID, Crew>> optional = this.pendingInvites.entrySet().stream().filter(paramEntry -> (((UUID)paramEntry.getKey()).equals(paramPlayer.getUniqueId()) && ((Crew)paramEntry.getValue()).equals(paramCrew))).findFirst();
        if (optional.isPresent()) {
            Map.Entry entry = optional.get();
            this.pendingInvites.remove(entry.getKey(), entry.getValue());
        }
    }
    public HashMap<UUID, Crew> getPendingInvites() {
        return this.pendingInvites;
    }

    public boolean isInCrew(Player paramPlayer) {
        Optional optional = this.crewList.stream().filter(paramCrew -> (paramCrew.getLeader().equals(paramPlayer) || paramCrew.getMembers().contains(paramPlayer))).findFirst();
        return optional.isPresent();
    }

    public boolean isFullCrew(Crew paramCrew) {
        return (paramCrew.getMembers().size() >= 5);
    }

    public boolean isValidCrew(Crew paramCrew) {
        return this.crewList.contains(paramCrew);
    }

    public Crew getCrewByPlayer(Player paramPlayer) {
        Optional<Crew> optional = this.crewList.stream().filter(paramCrew -> (paramCrew.getLeader().equals(paramPlayer) || paramCrew.getMembers().contains(paramPlayer))).findFirst();
        return optional.orElse(null);
    }
    public Crew getCrewById(UUID paramUUID) {
        Optional<Crew> optional = this.crewList.stream().filter(paramCrew -> paramCrew.getId().equals(paramUUID)).findFirst();
        return optional.orElse(null);
    }

    public boolean hasPendingInvite(Player paramPlayer, Crew paramCrew) {
        Optional optional = this.pendingInvites.entrySet().stream().filter(paramEntry -> (((UUID)paramEntry.getKey()).equals(paramPlayer.getUniqueId()) && ((Crew)paramEntry.getValue()).equals(paramCrew))).findFirst();
        return optional.isPresent();
    }

}
