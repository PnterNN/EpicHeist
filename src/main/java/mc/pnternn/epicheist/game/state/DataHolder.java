package mc.pnternn.epicheist.game.state;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import mc.obliviate.util.string.StringUtil;
import mc.pnternn.epicheist.EpicHeist;
import mc.pnternn.epicheist.config.ConfigurationHandler;
import mc.pnternn.epicheist.game.GameState;
import mc.pnternn.epicheist.util.ColorUtil;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import java.util.*;

public class DataHolder {
    protected int day,hour,minute,second;
    protected GameState state;
    protected List<BlockState> goldBlocks = new ArrayList<>();
    protected List<BlockState> specialBlocks = new ArrayList<>();
    protected List<BlockState> entranceDoorBlocks = new ArrayList<>();
    protected List<BlockState> escapeDoorBlocks = new ArrayList<>();
    protected HashMap<UUID, Double> crewCollectedGold = new HashMap<>();
    protected HashMap<UUID, Double> crewCollectedMoney = new HashMap<>();
    protected HashMap<UUID, Double> collectedGold = new HashMap<UUID, Double>();
    protected HashMap<UUID, Double> collectedMoney = new HashMap<UUID, Double>();
    protected HashMap<UUID, List<Zombie>> playerSwats = new HashMap<>();
    protected PacketAdapter goldBreakEvent = new PacketAdapter(EpicHeist.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.BLOCK_DIG) {
        @Override
        public void onPacketReceiving(PacketEvent event) {
            PacketContainer packet = event.getPacket();
            EnumWrappers.PlayerDigType type = packet.getPlayerDigTypes().read(0);
            if (type.toString().equals("STOP_DESTROY_BLOCK")){
                Location location = packet.getBlockPositionModifier().read(0).toLocation(event.getPlayer().getWorld());
                for (BlockState block : goldBlocks) {
                    if(block.getLocation().equals(location)){

                        event.getPlayer().sendBlockChange(location, Material.AIR.createBlockData());
                        if(EpicHeist.getInstance().getCrewManager().getCrewByPlayer(event.getPlayer()).getLeader().getPlayer() != null){
                            EpicHeist.getInstance().getCrewManager().getCrewByPlayer(event.getPlayer()).getLeader().getPlayer().sendBlockChange(location, Material.AIR.createBlockData());
                        }
                        for(OfflinePlayer members : EpicHeist.getInstance().getCrewManager().getCrewByPlayer(event.getPlayer()).getMembers()){
                            if(members.getPlayer() != null){
                                members.getPlayer().sendBlockChange(location, Material.AIR.createBlockData());
                            }
                        }
                        Random random = new Random();
                        Integer money = 0;
                        String sound = ConfigurationHandler.getValue("gold.small-gold.sound");
                        for (String key : ConfigurationHandler.getKeys("gold.small-gold.money")) {
                            ConfigurationSection section = ConfigurationHandler.getSection("gold.small-gold.money." + key);
                            if (event.getPlayer().hasPermission(section.getString("permission"))) {
                                money = random.nextInt(section.getInt("min"), section.getInt("max"));
                            }
                        }
                        List<BlockState> specialBlocksCopy = new ArrayList<>(specialBlocks);
                        for(BlockState specialblock : specialBlocksCopy){
                            if(specialblock.getLocation().equals(location)){
                                specialBlocks.remove(specialblock);
                                sound = ConfigurationHandler.getValue("gold.big-gold.sound");
                                for (String key : ConfigurationHandler.getKeys("gold.big-gold.money")) {
                                    ConfigurationSection section = ConfigurationHandler.getSection("gold.big-gold.money." + key);
                                    if (event.getPlayer().hasPermission(section.getString("permission"))) {
                                        money = random.nextInt(section.getInt("min"), section.getInt("max"));
                                    }
                                }
                                if(EpicHeist.getInstance().getCrewManager().getCrewByPlayer(event.getPlayer()).getLeader().getPlayer() !=null){
                                    EpicHeist.getInstance().getCrewManager().getCrewByPlayer(event.getPlayer()).getLeader().getPlayer().sendMessage(StringUtil.parseColor("&6" + event.getPlayer().getName() + " &ehas found a big gold block!"));
                                }
                                for(OfflinePlayer members : EpicHeist.getInstance().getCrewManager().getCrewByPlayer(event.getPlayer()).getMembers()){
                                    if(members.getPlayer() != null){
                                        members.getPlayer().sendMessage(StringUtil.parseColor("&6" + event.getPlayer().getName() + " &ehas found a big gold block!"));
                                    }
                                }
                            }
                        }
                        event.getPlayer().playSound(event.getPlayer().getLocation(), sound, 1, 1);
                        collectedGold.put(event.getPlayer().getUniqueId(), collectedGold.getOrDefault(event.getPlayer().getUniqueId(), 0.0) + 1);
                        collectedMoney.put(event.getPlayer().getUniqueId(), collectedMoney.getOrDefault(event.getPlayer().getUniqueId(), 0.0) + money);

                        EpicHeist.getEconomy().depositPlayer(event.getPlayer(), money);
                        if (EpicHeist.getInstance().getCrewManager().isInCrew(event.getPlayer())) {
                            crewCollectedGold.put(EpicHeist.getInstance().getCrewManager().getCrewByPlayer(event.getPlayer()).getId(),
                                    crewCollectedGold.getOrDefault(EpicHeist.getInstance().getCrewManager().getCrewByPlayer(event.getPlayer()).getId(), 0.0) + 1);
                            crewCollectedMoney.put(EpicHeist.getInstance().getCrewManager().getCrewByPlayer(event.getPlayer()).getId(),
                                    crewCollectedMoney.getOrDefault(EpicHeist.getInstance().getCrewManager().getCrewByPlayer(event.getPlayer()).getId(), 0.0) + money);
                        }
                        event.getPlayer().sendTitle(StringUtil.parseColor("&6+ "+money+"$"), StringUtil.parseColor("&7Toplamda "+collectedMoney.getOrDefault(event.getPlayer().getUniqueId(), 0.0)+" altın toparladın!"), 0, 10, 0);
                        Bukkit.getScheduler().scheduleSyncDelayedTask(EpicHeist.getInstance(), () -> {
                            event.getPlayer().sendBlockChange(location, Material.getMaterial(ConfigurationHandler.getValue("gold.small-gold.block")).createBlockData());
                            if(EpicHeist.getInstance().getCrewManager().getCrewByPlayer(event.getPlayer()).getLeader().getPlayer() != null){
                                EpicHeist.getInstance().getCrewManager().getCrewByPlayer(event.getPlayer()).getLeader().getPlayer().sendBlockChange(location, Material.getMaterial(ConfigurationHandler.getValue("gold.small-gold.block")).createBlockData());
                            }
                            for(OfflinePlayer members : EpicHeist.getInstance().getCrewManager().getCrewByPlayer(event.getPlayer()).getMembers()){
                                if(members.getPlayer() != null){
                                    members.getPlayer().sendBlockChange(location, Material.getMaterial(ConfigurationHandler.getValue("gold.small-gold.block")).createBlockData());

                                }
                            }
                        }, 20 * 30);
                    }
                }
            }
        }
    };
    public void uninstall(){
        for(Player player : EpicHeist.getMatch().getVaultPlayers()){
            for(Player players : Bukkit.getOnlinePlayers()){
                player.showPlayer(players);
            }
            player.teleport(ConfigurationHandler.getBankLocation());
            ColorUtil.showTitle(player,
                    ConfigurationHandler.getValue("animated-titles.heist-cancel.background-color"),
                    ConfigurationHandler.getValue("animated-titles.heist-cancel.title-color"),
                    ConfigurationHandler.getValue("animated-titles.heist-cancel.title"),
                    ConfigurationHandler.getValue("animated-titles.heist-cancel.subtitle"));
        }
        EpicHeist.getInstance().getProtocolManager().removePacketListener(goldBreakEvent);

        for (BlockState block : goldBlocks) {
            block.getBlock().setType(block.getType());
        }
        for (BlockState block : entranceDoorBlocks) {
            block.getBlock().setType(block.getType());
        }
        for (BlockState block : escapeDoorBlocks) {
            block.getBlock().setType(block.getType());
        }
        goldBlocks.clear();
        entranceDoorBlocks.clear();
        escapeDoorBlocks.clear();

        playerSwats.forEach((uuid, zombies) -> {
            zombies.forEach(Entity::remove);
        });
    }
    public HashMap<UUID, List<Zombie>> getPlayerSwats() {
        return playerSwats;
    }
    public GameState getState() {
        return state;
    }
    public List<BlockState> getEntranceDoorBlocks() {
        return entranceDoorBlocks;
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
    public String getTime(){
        DataHolder dataHolder = EpicHeist.getMatch().getDataHolder();
        if(dataHolder.getDay()>0){
            return ConfigurationHandler.getValue("time-formats.day").replace("{day}", String.valueOf(dataHolder.getDay()))
                    .replace("{hour}", String.valueOf(dataHolder.getHour()))
                    .replace("{minute}", String.valueOf(dataHolder.getMinute()))
                    .replace("{second}", String.valueOf(dataHolder.getSecond()));
        }
        else if(dataHolder.getHour()> 0){
            return ConfigurationHandler.getValue("time-formats.hour").replace("{day}", String.valueOf(dataHolder.getDay()))
                    .replace("{hour}", String.valueOf(dataHolder.getHour()))
                    .replace("{minute}", String.valueOf(dataHolder.getMinute()))
                    .replace("{second}", String.valueOf(dataHolder.getSecond()));
        }
        else if(dataHolder.getMinute()>0){
            return ConfigurationHandler.getValue("time-formats.minute").replace("{day}", String.valueOf(dataHolder.getDay()))
                    .replace("{hour}", String.valueOf(dataHolder.getHour()))
                    .replace("{minute}", String.valueOf(dataHolder.getMinute()))
                    .replace("{second}", String.valueOf(dataHolder.getSecond()));
        }
        else if(dataHolder.getSecond()>0){
            return ConfigurationHandler.getValue("time-formats.second").replace("{day}", String.valueOf(dataHolder.getDay()))
                    .replace("{hour}", String.valueOf(dataHolder.getHour()))
                    .replace("{minute}", String.valueOf(dataHolder.getMinute()))
                    .replace("{second}", String.valueOf(dataHolder.getSecond()));
        }
        return "...";
    }
    public HashMap<UUID, Double> getCrewCollectedGold() {
        return crewCollectedGold;
    }

    public HashMap<UUID, Double> getCrewCollectedMoney() {
        return crewCollectedMoney;
    }

    public HashMap<UUID, Double> getCollectedGold() {
        return collectedGold;
    }

    public HashMap<UUID, Double> getCollectedMoney() {
        return collectedMoney;
    }

}
