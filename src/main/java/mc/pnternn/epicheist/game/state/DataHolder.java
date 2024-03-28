package mc.pnternn.epicheist.game.state;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import mc.pnternn.epicheist.EpicHeist;
import mc.pnternn.epicheist.config.ConfigurationHandler;
import mc.pnternn.epicheist.game.GameState;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.*;

public class DataHolder {
    protected int day,hour,minute,second;
    protected GameState state;
    protected List<BlockState> goldBlocks = new ArrayList<>();
    protected List<BlockState> specialBlocks = new ArrayList<>();
    protected List<BlockState> doorBlocks = new ArrayList<>();
    protected HashMap<UUID, Double> crewCollectedGold = new HashMap<>();
    protected HashMap<UUID, Double> crewCollectedMoney = new HashMap<>();
    protected HashMap<UUID, Double> collectedGold = new HashMap<UUID, Double>();
    protected HashMap<UUID, Double> collectedMoney = new HashMap<UUID, Double>();
    protected PacketAdapter goldBreakEvent = new PacketAdapter(EpicHeist.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_DIG) {
        @Override
        public void onPacketReceiving(PacketEvent event) {
            PacketContainer packet = event.getPacket();
            EnumWrappers.PlayerDigType type = packet.getPlayerDigTypes().read(0);
            if (type.toString().equals("STOP_DESTROY_BLOCK")){
                Location location = packet.getBlockPositionModifier().read(0).toLocation(event.getPlayer().getWorld());
                for (BlockState block : goldBlocks) {
                    if(block.getLocation().equals(location)){
                        Random random = new Random();
                        Integer money = 0;
                        for(BlockState specialblock : specialBlocks){
                            if(specialblock.getLocation().equals(location)){
                                money = random.nextInt(100,200);
                                event.getPlayer().playSound(event.getPlayer().getLocation(), ConfigurationHandler.getValue("musics.gold-steal-big"), 1, 1);
                                specialBlocks.remove(specialblock);
                                break;
                            }else{
                                money = random.nextInt(10,20);
                                event.getPlayer().playSound(event.getPlayer().getLocation(), ConfigurationHandler.getValue("musics.gold-steal"), 1, 1);
                                break;
                            }
                        }
                        event.getPlayer().sendBlockChange(location, Material.AIR.createBlockData());
                        collectedGold.put(event.getPlayer().getUniqueId(), collectedGold.getOrDefault(event.getPlayer().getUniqueId(), 0.0) + 1);
                        collectedMoney.put(event.getPlayer().getUniqueId(), collectedMoney.getOrDefault(event.getPlayer().getUniqueId(), 0.0) + money);
                        event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6+"+ money+ " Money"));
                        EpicHeist.getEconomy().depositPlayer(event.getPlayer(), money);
                        if(EpicHeist.getInstance().getCrewManager().isInCrew(event.getPlayer())){
                            crewCollectedGold.put(EpicHeist.getInstance().getCrewManager().getCrewByPlayer(event.getPlayer()).getId(),
                                    crewCollectedGold.getOrDefault(EpicHeist.getInstance().getCrewManager().getCrewByPlayer(event.getPlayer()).getId(), 0.0) + 1);
                            crewCollectedMoney.put(EpicHeist.getInstance().getCrewManager().getCrewByPlayer(event.getPlayer()).getId(),
                                    crewCollectedMoney.getOrDefault(EpicHeist.getInstance().getCrewManager().getCrewByPlayer(event.getPlayer()).getId(), 0.0) + money);
                        }
                        Bukkit.getScheduler().scheduleSyncDelayedTask(EpicHeist.getInstance(), () -> {
                            event.getPlayer().sendBlockChange(location, Material.GOLD_BLOCK.createBlockData());
                        }, 20*30);
                    }
                }
            }
        }
    };
    public void uninstall(){
        EpicHeist.getInstance().getProtocolManager().removePacketListener(goldBreakEvent);

        for (BlockState block : goldBlocks) {
            block.getBlock().setType(block.getType());
        }
        goldBlocks.clear();

        for (BlockState block : doorBlocks) {
            block.getBlock().setType(block.getType());
        }
        doorBlocks.clear();
    }

    public GameState getState() {
        return state;
    }
    public List<BlockState> getDoorBlocks() {
        return doorBlocks;
    }
    public int getDay() {
        return day;
    }
    public int getHour() {
        return hour;
    }
    public int getMinute() {
        return minute;
    }
    public int getSecond() {
        return second;
    }
}
