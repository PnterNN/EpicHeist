package mc.pnternn.epicheist.game.state;

import mc.obliviate.util.string.StringUtil;
import mc.pnternn.epicheist.EpicHeist;
import mc.pnternn.epicheist.config.ConfigurationHandler;
import mc.pnternn.epicheist.game.GameState;
import mc.pnternn.epicheist.game.Match;
import mc.pnternn.epicheist.util.ColorUtil;
import mc.pnternn.epicheist.util.PacketUtils;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SwatState extends GameState {
    public SwatState(Match match) {
        super(match);
    }
    @NotNull
    @Override
    public Duration getDuration() {
        return Duration.ofSeconds(Integer.parseInt(ConfigurationHandler.getValue("timer.swat-state.seconds")))
                .plusMinutes(Integer.parseInt(ConfigurationHandler.getValue("timer.swat-state.minutes")))
                .plusHours(Integer.parseInt(ConfigurationHandler.getValue("timer.swat-state.hours")))
                .plusDays(Integer.parseInt(ConfigurationHandler.getValue("timer.swat-state.days")));
    }
    @Override
    public void onUpdate(){
        getMatch().getDataHolder().day = ((int) getRemainingDuration().toDaysPart());
        getMatch().getDataHolder().hour = (getRemainingDuration().toHoursPart());
        getMatch().getDataHolder().minute = (getRemainingDuration().toMinutesPart());
        getMatch().getDataHolder().second = (getRemainingDuration().toSecondsPart());

        if(getRemainingDuration().toSeconds() == 0){
            getMatch().getStateseries().skip();
        }
    }
    @Override
    protected void onStart() {
        getMatch().getDataHolder().state = this;
        if(ConfigurationHandler.getValue("main-server").equals("true")){
            List<BlockState> doorBlocks = EpicHeist.getMatch().getDataHolder().entranceDoorBlocks;
            for (Player player: Bukkit.getOnlinePlayers()){
                if(!getMatch().getVaultPlayers().contains(player)){
                    for (BlockState block : doorBlocks) {
                        player.sendBlockChange(block.getLocation(), block.getBlockData());
                    }
                }
            }
            for(Player player : getMatch().getVaultPlayers()){
                player.playSound(player.getLocation(), ConfigurationHandler.getValue("musics.swat-state"), 1, 1);
                ColorUtil.showTitle(player,
                        ConfigurationHandler.getValue("animated-titles.swat-state.background-color"),
                        ConfigurationHandler.getValue("animated-titles.swat-state.title-color"),
                        ConfigurationHandler.getValue("animated-titles.swat-state.title"),
                        ConfigurationHandler.getValue("animated-titles.swat-state.subtitle"));
                getMatch().getDataHolder().playerSwats.put(player.getUniqueId(), new ArrayList<>());
                for(int i = 0;i<Integer.parseInt(ConfigurationHandler.getValue("swat.amount"));i++){
                    Zombie swat = (Zombie)player.getWorld().spawnEntity(ConfigurationHandler.getSwatLocation(), EntityType.ZOMBIE);
                    swat.setAdult();
                    swat.setCustomName(StringUtil.parseColor(ConfigurationHandler.getValue("swat.name")));
                    swat.setCustomNameVisible(true);
                    swat.getEquipment().setHelmet(new ItemStack(Material.getMaterial(ConfigurationHandler.getValue("swat.armor.helmet"))));
                    swat.getEquipment().setChestplate(new ItemStack(Material.getMaterial(ConfigurationHandler.getValue("swat.armor.chestplate"))));
                    swat.getEquipment().setLeggings(new ItemStack(Material.getMaterial(ConfigurationHandler.getValue("swat.armor.leggings"))));
                    swat.getEquipment().setBoots(new ItemStack(Material.getMaterial(ConfigurationHandler.getValue("swat.armor.boots"))));
                    swat.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999999, Integer.parseInt(ConfigurationHandler.getValue("swat.speed-level"))));
                    swat.setMaxHealth(2048);
                    swat.setHealth(2048);

                    swat.setGlowing(true);

                    getMatch().getDataHolder().playerSwats.get(player.getUniqueId()).add(swat);
                    List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                    for(Player member : EpicHeist.getInstance().getCrewManager().getCrewByPlayer(player).getOnlineMembers()){
                        players.remove(member);
                    }
                    players.remove(EpicHeist.getInstance().getCrewManager().getCrewByPlayer(player).getLeader().getPlayer());
                    for(Player p : players){
                        PacketUtils.hideEntity(p, swat.getEntityId());
                    }
                    Bukkit.getScheduler().scheduleSyncRepeatingTask(EpicHeist.getInstance(), () -> {
                        swat.setTarget(player);
                    }, 0, 5);
                    Bukkit.getScheduler().scheduleSyncRepeatingTask(EpicHeist.getInstance(), () -> {
                        swat.setVelocity(player.getLocation().toVector().subtract(swat.getLocation().toVector()).normalize().multiply(Double.parseDouble(ConfigurationHandler.getValue("swat.jump"))));
                    }, 0, 60);
                }
            }
        }
    }
    @Override
    public void onEnd() {
        if(ConfigurationHandler.getValue("main-server").equals("true")){
            for (BlockState block : EpicHeist.getMatch().getDataHolder().entranceDoorBlocks) {
                block.getBlock().setType(block.getType());
            }
            getMatch().getDataHolder().entranceDoorBlocks.clear();
        }
    }
}