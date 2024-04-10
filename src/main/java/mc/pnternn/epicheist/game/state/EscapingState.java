package mc.pnternn.epicheist.game.state;

import mc.obliviate.util.string.StringUtil;
import mc.pnternn.epicheist.EpicHeist;
import mc.pnternn.epicheist.config.ConfigurationHandler;
import mc.pnternn.epicheist.game.GameState;
import mc.pnternn.epicheist.game.Match;
import mc.pnternn.epicheist.managers.Crew;
import mc.pnternn.epicheist.util.ColorUtil;
import mc.pnternn.epicheist.util.RegionBlockIteration;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EscapingState extends GameState {
    public EscapingState(Match match) {
        super(match);
    }
    @NotNull
    @Override
    public Duration getDuration() {
        return Duration.ofSeconds(Integer.parseInt(ConfigurationHandler.getValue("timer.escaping-state.seconds")))
                .plusMinutes(Integer.parseInt(ConfigurationHandler.getValue("timer.escaping-state.minutes")))
                .plusHours(Integer.parseInt(ConfigurationHandler.getValue("timer.escaping-state.hours")))
                .plusDays(Integer.parseInt(ConfigurationHandler.getValue("timer.escaping-state.days")));
    }
    @Override
    public void onUpdate() {
        getMatch().getDataHolder().day = ((int) getRemainingDuration().toDaysPart());
        getMatch().getDataHolder().hour = (getRemainingDuration().toHoursPart());
        getMatch().getDataHolder().minute = (getRemainingDuration().toMinutesPart());
        getMatch().getDataHolder().second = (getRemainingDuration().toSecondsPart());

        if (getRemainingDuration().toSeconds() == 0) {
            getMatch().getStateseries().end();
        }
    }

    @Override
    protected void onStart() {
        getMatch().getDataHolder().state = this;

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), ConfigurationHandler.getValue("musics.escaping-state"), 1, 1);
            ColorUtil.showTitle(player,
                    ConfigurationHandler.getValue("animated-titles.escaping-state.background-color"),
                    ConfigurationHandler.getValue("animated-titles.escaping-state.title-color"),
                    ConfigurationHandler.getValue("animated-titles.escaping-state.title"),
                    ConfigurationHandler.getValue("animated-titles.escaping-state.subtitle"));
        }
        RegionBlockIteration regionBlockIteration = new RegionBlockIteration();
        for (Location location : regionBlockIteration.getRegionBlocks(ConfigurationHandler.getValue("regions.world-name"), ConfigurationHandler.getValue("regions.exit-door-name"))) {;
            getMatch().getDataHolder().escapeDoorBlocks.add(location.getBlock().getState());
            location.getBlock().setType(Material.AIR);
            Bukkit.getWorld(ConfigurationHandler.getValue("regions.world-name")).createExplosion(location, 0F, false, false);
        }
    }
    @Override
    public void onEnd() {

        EpicHeist.getInstance().getProtocolManager().removePacketListener(getMatch().getDataHolder().goldBreakEvent);
        for (BlockState block : getMatch().getDataHolder().goldBlocks) {
            block.getBlock().setType(block.getType());
        }
        getMatch().getDataHolder().goldBlocks.clear();

        Double gold;
        Double money;
        for (Player player: Bukkit.getOnlinePlayers()){
            player.playSound(player.getLocation(), ConfigurationHandler.getValue("musics.ending-state"), 1, 1);
            if(getMatch().getVaultPlayers().contains(player)){
                gold = getMatch().getDataHolder().collectedGold.getOrDefault(player.getUniqueId(), 0.0)/2;
                money = getMatch().getDataHolder().collectedMoney.getOrDefault(player.getUniqueId(), 0.0)/2;
                getMatch().getDataHolder().collectedMoney.put(player.getUniqueId(), getMatch().getDataHolder().collectedMoney.getOrDefault(player.getUniqueId(), 0.0) -money);
                getMatch().getDataHolder().collectedGold.put(player.getUniqueId(), getMatch().getDataHolder().collectedGold.getOrDefault(player.getUniqueId(), 0.0) -gold);
                getMatch().getDataHolder().crewCollectedGold.put(EpicHeist.getInstance().getCrewManager().getCrewByPlayer(player).getId(), getMatch().getDataHolder().crewCollectedGold.getOrDefault(EpicHeist.getInstance().getCrewManager().getCrewByPlayer(player).getId(), 0.0)- gold);
                getMatch().getDataHolder().crewCollectedMoney.put(EpicHeist.getInstance().getCrewManager().getCrewByPlayer(player).getId(), getMatch().getDataHolder().crewCollectedMoney.getOrDefault(EpicHeist.getInstance().getCrewManager().getCrewByPlayer(player).getId(), 0.0)- money);
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 99, 2));
                EpicHeist.getEconomy().bankWithdraw("vault", money);
                ColorUtil.showTitle(player,
                        ConfigurationHandler.getValue("animated-titles.catch-title.background-color"),
                        ConfigurationHandler.getValue("animated-titles.catch-title.title-color"),
                        ConfigurationHandler.getValue("animated-titles.catch-title.title"),
                        ConfigurationHandler.getValue("animated-titles.catch-title.subtitle"));
                player.teleport(ConfigurationHandler.getJailLocation());
                player.playSound(player.getLocation(), ConfigurationHandler.getValue("musics.catch"), 1, 1);
            }else{
                ColorUtil.showTitle(player,
                        ConfigurationHandler.getValue("animated-titles.ending-state.background-color"),
                        ConfigurationHandler.getValue("animated-titles.ending-state.title-color"),
                        ConfigurationHandler.getValue("animated-titles.ending-state.title"),
                        ConfigurationHandler.getValue("animated-titles.ending-state.subtitle"));
            }
            player.sendMessage("§6You have collected §e"+getMatch().getDataHolder().collectedGold.getOrDefault(player.getUniqueId(), 0.0)+" §6gold and §e"+getMatch().getDataHolder().collectedMoney.getOrDefault(player.getUniqueId(), 0.0)+" §6money.");
        }

        for (List<Zombie> zombieList : getMatch().getDataHolder().getPlayerSwats().values()) {
            for (Zombie zombie : zombieList) {
                zombie.setHealth(0);
            }
        }

        for (BlockState block : getMatch().getDataHolder().escapeDoorBlocks) {
            block.getBlock().setType(block.getType());
        }
        getMatch().getDataHolder().escapeDoorBlocks.clear();

        Crew[] topCrews = new Crew[3];
        String[] topCrewsName = new String[3];
        Double[] topCrewsGold = new Double[3];
        Double[] topCrewsMoney = new Double[3];
        Double[] perPlayerGold = new Double[3];
        double maxvalue;
        double maxvaluePerPlayer = 0;
        UUID maxvalueIndex;
        int x;
        List<Crew> temp = new ArrayList<>(EpicHeist.getInstance().getCrewManager().getCrewList());
        for(int i = 0; i<3;i++){
            maxvalue= 0.0;
            maxvalueIndex = null;
            for(Crew crew : temp){
                x = 0;
                for(OfflinePlayer player : crew.getMembers()){
                    if(player.isOnline()){
                        x++;
                    }
                }
                if(crew.getLeader().isOnline()){
                    x++;
                }
                if(x>0){
                    if(getMatch().getDataHolder().crewCollectedGold.get(crew.getId()) != null){
                        if(getMatch().getDataHolder().crewCollectedGold.get(crew.getId())/x > maxvalue){
                            maxvalue = getMatch().getDataHolder().crewCollectedGold.get(crew.getId());
                            maxvaluePerPlayer = maxvalue/x;
                            maxvalueIndex = crew.getId();
                        }
                    }
                }
            }

            if(maxvalueIndex!=null){
                topCrewsName[i] = EpicHeist.getInstance().getCrewManager().getCrewById(maxvalueIndex).getName();
                topCrewsGold[i] = getMatch().getDataHolder().crewCollectedGold.get(maxvalueIndex);
                perPlayerGold[i] = maxvaluePerPlayer;
                topCrewsMoney[i] = getMatch().getDataHolder().crewCollectedMoney.get(maxvalueIndex);
                topCrews[i] = EpicHeist.getInstance().getCrewManager().getCrewById(maxvalueIndex);
            }else{
                topCrewsName[i] = "Hiç kimse";
                topCrewsGold[i] = 0.0;
                topCrewsMoney[i] = 0.0;
                perPlayerGold[i] = 0.0;
                topCrews[i] = null;
            }
            temp.remove(EpicHeist.getInstance().getCrewManager().getCrewById(maxvalueIndex));
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(EpicHeist.getInstance(), () -> {
            for(int i = 0;i<15;i++){
                Bukkit.broadcastMessage(" ");
            }
            for(Player player : Bukkit.getOnlinePlayers()){
                player.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("messages.gold-stealing-player"))
                        .replace("{gold}", String.valueOf(getMatch().getDataHolder().collectedGold.getOrDefault(player.getUniqueId(), 0.0)))
                        .replace("{money}", String.valueOf(getMatch().getDataHolder().collectedMoney.getOrDefault(player.getUniqueId(), 0.0))));
            }
            ComponentBuilder hovertext = new ComponentBuilder("Top Crews:\n\n");
            for(int i = 0; i<3;i++){
                hovertext.append(StringUtil.parseColor(topCrewsName[i]+"&7's members"));
                if(topCrews[i] != null){
                    hovertext.append(StringUtil.parseColor("\n &7- "+topCrews[i].getLeader().getName()));
                    for(OfflinePlayer player : topCrews[i].getMembers()){
                        hovertext.append(StringUtil.parseColor("\n &7- "+player.getName()));
                    }
                }
                hovertext.append(StringUtil.parseColor("\n\n"));
            }
            for(String message : ConfigurationHandler.getList("messages.most-gold-stealing-announcement")){
                TextComponent msg = new TextComponent( StringUtil.parseColor(message
                        .replace("{crew_1}", topCrewsName[0])
                        .replace("{crew_2}", topCrewsName[1])
                        .replace("{crew_3}", topCrewsName[2])
                        .replace("{gold_1}", String.valueOf(topCrewsGold[0]))
                        .replace("{gold_2}", String.valueOf(topCrewsGold[1]))
                        .replace("{gold_3}", String.valueOf(topCrewsGold[2]))
                        .replace("{money_1}", String.valueOf(topCrewsMoney[0]))
                        .replace("{money_2}", String.valueOf(topCrewsMoney[1]))
                        .replace("{money_3}", String.valueOf(topCrewsMoney[2]))
                        .replace("{percent_1}", String.valueOf(perPlayerGold[0]))
                        .replace("{percent_2}", String.valueOf(perPlayerGold[1]))
                        .replace("{percent_3}", String.valueOf(perPlayerGold[2]))
                ));
                msg.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, hovertext.create()));
                Bukkit.spigot().broadcast(msg);
            }
            for (int i = 0; i < 3; i++) {
                if (topCrews[i] != null && topCrews[i].getLeader().isOnline()) {
                    int z = i;
                    int finalI = i;
                    if(topCrews[i].getLeader().isOnline()){
                        Player leader = (Player) topCrews[i].getLeader();
                        leader.playSound(leader.getLocation(), ConfigurationHandler.getValue("musics.crew-" + (z + 1)), 1, 1);
                        if(ConfigurationHandler.getConfig().getList("rewards." + i) != null){
                            for(ItemStack item : (List<ItemStack>) ConfigurationHandler.getConfig().getList("rewards." + i)){
                                leader.getInventory().addItem(item);
                            }
                        }
                    }
                    topCrews[i].getMembers().stream().filter(OfflinePlayer::isOnline).forEach(player ->{
                        Player member = (Player) player;
                        if(ConfigurationHandler.getConfig().getList("rewards." + finalI) != null){
                            for(ItemStack item : (List<ItemStack>) ConfigurationHandler.getConfig().getList("rewards." + finalI)){
                                member.getInventory().addItem(item);
                            }
                        }
                        member.playSound(member.getLocation(), ConfigurationHandler.getValue("musics.crew-" + (z + 1)), 1, 1);
                    });
                }
            }
        }, 50L);

        EpicHeist.setMatch(new Match());
        EpicHeist.getMatch().start();
    }
}
