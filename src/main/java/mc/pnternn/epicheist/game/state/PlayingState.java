package mc.pnternn.epicheist.game.state;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.google.common.base.Preconditions;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.lone.itemsadder.api.CustomStack;
import mc.pnternn.epicheist.EpicHeist;
import mc.pnternn.epicheist.config.ConfigurationHandler;
import mc.pnternn.epicheist.game.GameState;
import mc.pnternn.epicheist.game.Match;
import mc.pnternn.epicheist.game.entities.PacketEntity;
import mc.pnternn.epicheist.util.PacketUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minikloon.fsmgasm.StateSwitch;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PlayingState extends GameState {

    private static final World world = Bukkit.getWorld("spawn");
    private static final ProtectedRegion region = Objects.requireNonNull(WorldGuard.getInstance().getPlatform()
            .getRegionContainer().get(BukkitAdapter.adapt(Objects.requireNonNull(world)))).getRegion("kasa");
    private static final Location policeloc = new Location(world, 0, 72, 0);
    private static final Location tntloc1 = new Location(world, 10, 72, 0);
    private static final Location tntloc2 = new Location(world, 20, 72, 0);
    private static final Location jail = new Location(world, 0, 80, 0);
    private static final Particle goldparticle = Particle.VILLAGER_HAPPY;

    private PacketEntity packetEntity;

    private void spawnPacketEntity(@Nullable Location location, String name, Player player) {
        this.packetEntity = new PacketEntity();
        packetEntity.setEntityType(EntityType.ZOMBIE);
        packetEntity.setLocation(location);
        packetEntity.setName(name);
        packetEntity.spawn(player);
    }



    private Zombie[] police = new Zombie[50];

    public PlayingState(Match match) {
        super(match);
    }

    @NotNull
    @Override
    public Duration getDuration() {
        return Duration.ofSeconds(40);
    }

    @Override
    public void onUpdate() {
        Bukkit.broadcastMessage(getRemainingDuration().toSeconds()+ "seconds left");
        if (getRemainingDuration().toSeconds() == 35) {
            Bukkit.broadcastMessage("§c§lThe police is coming!");
            TNTPrimed tnt = (TNTPrimed) tntloc1.getWorld().spawnEntity(tntloc2, EntityType.PRIMED_TNT);
            tnt.setFuseTicks(101);
        }
        /*if (getRemainingDuration().toSeconds() == 30) {
            for (int i = 0; i < 50; i++) {
                world.spawnParticle(Particle.EXPLOSION_NORMAL, tntloc2, 5);
                world.playSound(tntloc2, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                Zombie zombie = (Zombie) policeloc.getWorld().spawnEntity(policeloc, EntityType.ZOMBIE);
                policeProperties(zombie);
                police[i] = zombie;
            }
        }*/
        if(getRemainingDuration().toSeconds()== 30){
            spawnPacketEntity(policeloc, "§c§lPolice", Bukkit.getPlayer("PnterNN"));
        }
        if(getRemainingDuration().toSeconds() == 0){
            end();
            StateSwitch stateSwitch = new StateSwitch();
            stateSwitch.changeState(new UninstallState(getMatch()));
        }
    }
    @Override
    public void onEnd(){
        for (int i = 0; i < 50; i++) {
            police[i].remove();
        }
    }


    /*private ItemStack getZombieItem(String type){
        ItemStack item;
        if(ConfigurationHandler.getConfig().get("police.equipment." + type + ".item.custom") != null){
             item = CustomStack.getInstance(ConfigurationHandler.getConfig().get("police.equipment." + type + ".item.custom").toString()).getItemStack();
        }else{
             item = new ItemStack(Material.getMaterial(ConfigurationHandler.getConfig().get("police.equipment." + type + ".item.material").toString()));
        }
        return item;
    }*/

   /* private void policeProperties(Zombie zombie){
        zombie.setCustomName(ConfigurationHandler.getConfig().get("police.name").toString());
        zombie.setCustomNameVisible(true);
        zombie.setCanPickupItems(false);
        zombie.setAdult();
        zombie.setRemoveWhenFarAway(false);
        zombie.setHealth(Integer.parseInt(ConfigurationHandler.getConfig().get("police.health").toString()));

        ItemStack helmet = getZombieItem("helmet");
        Preconditions.checkNotNull(helmet, "Helmet not found (" + helmet.getItemMeta().getDisplayName() + ")");
        zombie.getEquipment().setHelmet(helmet);

        ItemStack chestplate = getZombieItem("chestplate");
        Preconditions.checkNotNull(chestplate, "Chestplate not found (" + chestplate.getItemMeta().getDisplayName() + ")");
        zombie.getEquipment().setChestplate(chestplate);

        ItemStack leggings = getZombieItem("leggings");
        Preconditions.checkNotNull(leggings, "Leggings not found (" + leggings.getItemMeta().getDisplayName() + ")");
        zombie.getEquipment().setLeggings(leggings);

        ItemStack boots = getZombieItem("boots");
        Preconditions.checkNotNull(boots, "Boots not found (" + boots.getItemMeta().getDisplayName() + ")");
        zombie.getEquipment().setBoots(boots);

        ItemStack hand = getZombieItem("hand");
        Preconditions.checkNotNull(hand, "Hand not found (" + hand.getItemMeta().getDisplayName() + ")");
        zombie.getEquipment().setItemInMainHand(hand);

        ItemStack offhand = getZombieItem("offhand");
        Preconditions.checkNotNull(offhand, "Offhand not found (" + offhand.getItemMeta().getDisplayName() + ")");
        zombie.getEquipment().setItemInOffHand(offhand);
    }*/

    @Override
    protected void onStart() {
        getMatch().getMatchTaskManager().repeatTask("heart-beat", () -> update(),20);
        Bukkit.broadcastMessage("§a§lThe heist has started!");
        world.spawnParticle(Particle.EXPLOSION_NORMAL, tntloc1, 5);
        world.playSound(tntloc1, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);

        register("block-break", new Listener() {
            @EventHandler
            public void onClick(PlayerInteractEvent e) {
                if (e.getClickedBlock() == null) return;
                if (e.getClickedBlock().getType() != Material.GOLD_BLOCK) return;
                Preconditions.checkNotNull(region, "Region cannot be null");
                if (!region.contains(e.getClickedBlock().getX(), e.getClickedBlock().getY(), e.getClickedBlock().getZ())) return;
                EpicHeist.getInstance().getEconomy().depositPlayer(e.getPlayer(), 20);
                e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("20 Sıkke kazandın."));
                world.spawnParticle(goldparticle, e.getClickedBlock().getLocation(), 1);
                e.getClickedBlock().setType(Material.AIR, true);
                delayedTask("regen " + UUID.randomUUID(), () -> e.getClickedBlock().setType(Material.GOLD_BLOCK, true), 200);
            }
        });

        /*register("EntityDamageByEntityEvent", new Listener() {
            @EventHandler
            public void onPlayerDamage(EntityDamageByEntityEvent e){
                Preconditions.checkNotNull(region, "Region cannot be null");
                if (region.contains(e.getEntity().getLocation().getBlockX(), e.getEntity().getLocation().getBlockY(),e.getEntity().getLocation().getBlockZ()) == true  && e.getDamager().getType()!=EntityType.ZOMBIE && e.getDamager().getCustomName()!=ConfigurationHandler.getConfig().get("police.name").toString()) return;
                Player player = (Player) e.getEntity();
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
                Bukkit.getScheduler().runTaskLater(EpicHeist.getInstance(), () -> e.getEntity().teleport(jail), 20);
                e.getEntity().sendMessage("§c§lYou have been jailed!");
            }
        });*/
    }
}
