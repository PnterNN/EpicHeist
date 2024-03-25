package mc.pnternn.epicheist.game.state;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.scheduler.Task;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import mc.pnternn.epicheist.EpicHeist;
import mc.pnternn.epicheist.config.ConfigurationHandler;
import mc.pnternn.epicheist.game.GameState;
import mc.pnternn.epicheist.game.Match;
import mc.pnternn.epicheist.util.RegionBlockIteration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.*;

public class PlayingState extends GameState {
    private PacketAdapter adapter = new PacketAdapter(EpicHeist.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_DIG) {
        @Override
        public void onPacketReceiving(PacketEvent event) {
            PacketContainer packet = event.getPacket();
            EnumWrappers.PlayerDigType type = packet.getPlayerDigTypes().read(0);
            if (type.toString().equals("STOP_DESTROY_BLOCK")){
                BlockPosition blockPosition = packet.getBlockPositionModifier().read(0);
                Location location = blockPosition.toLocation(event.getPlayer().getWorld());
                if(vaultBlocks.contains(location)){
                    event.getPlayer().sendBlockChange(location, Material.AIR.createBlockData());

                }else{
                    event.setCancelled(true);
                }
            }
        }
    };

    public PlayingState(Match match) {
        super(match);
    }
    private List<Location> vaultBlocks = new ArrayList<>();
    @NotNull
    @Override
    public Duration getDuration() {
        return Duration.ofSeconds(Integer.parseInt(ConfigurationHandler.getValue("timer.playing-state.seconds")))
                .plusMinutes(Integer.parseInt(ConfigurationHandler.getValue("timer.playing-state.minutes")))
                .plusHours(Integer.parseInt(ConfigurationHandler.getValue("timer.playing-state.hours")))
                .plusDays(Integer.parseInt(ConfigurationHandler.getValue("timer.playing-state.days")));
    }

    @Override
    public void onUpdate() {
        getMatch().setDay((int) getRemainingDuration().toDaysPart());
        getMatch().setHour(getRemainingDuration().toHoursPart());
        getMatch().setMinute(getRemainingDuration().toMinutesPart());
        getMatch().setSecond(getRemainingDuration().toSecondsPart());
        if (getRemainingDuration().toSeconds() == 0) {
            getMatch().getStateseries().skip();
        }
    }

    @Override
    public void onEnd(){
        for (Location block : vaultBlocks) {
            block.getBlock().setType(org.bukkit.Material.GOLD_BLOCK);
        }
        vaultBlocks.clear();
        EpicHeist.getInstance().getProtocolManager().removePacketListener(adapter);
    }
    @Override
    protected void onStart() {
        getMatch().setState(this);
        Bukkit.broadcastMessage("The game has started!");
        RegionBlockIteration regionBlockIteration = new RegionBlockIteration();
        List<Location> locations = regionBlockIteration.getRegionBlocks(ConfigurationHandler.getValue("regions.world-name"), ConfigurationHandler.getValue("regions.vault-name"));
        Player[] players = Bukkit.getOnlinePlayers().toArray(new Player[0]);
        for (Location location : locations) {
            if(location.getBlock().getType() == Material.GOLD_BLOCK){
                location.getBlock().setType(Material.AIR);
                vaultBlocks.add(location);
            }
        }
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(EpicHeist.getInstance(), () -> {
            for (Player player : players) {
                for(Location location : vaultBlocks){
                    player.sendBlockChange(location, Material.GOLD_BLOCK.createBlockData());
                }
            }
        }, 5L);


        EpicHeist.getInstance().getProtocolManager().addPacketListener(adapter);

    }
}
