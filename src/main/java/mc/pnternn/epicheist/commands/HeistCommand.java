package mc.pnternn.epicheist.commands;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.gson.Gson;
import com.sk89q.worldedit.Countable;
import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import com.sk89q.worldguard.util.jsonsimple.JSONObject;
import mc.obliviate.util.string.StringUtil;
import mc.pnternn.epicheist.EpicHeist;
import mc.pnternn.epicheist.config.ConfigurationHandler;
import mc.pnternn.epicheist.guis.AdminPanelGui;
import mc.pnternn.epicheist.managers.Crew;
import mc.pnternn.epicheist.game.Match;
import mc.pnternn.epicheist.util.ColorUtil;
import mc.pnternn.epicheist.util.PacketUtils;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;
import org.bukkit.Color;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HeistCommand implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String str, String[] args) {
        if(args.length> 0){
            if(ConfigurationHandler.getValue("main-server").equals("true")){
                switch (args[0].toLowerCase()) {
                    case "reload" -> {
                        if(sender.hasPermission("heist.admin")){

                            ConfigurationHandler.reloadConfig();
                            EpicHeist.getMatch().stop();
                            sender.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&aConfig reloaded!"));
                            EpicHeist.setMatch(new Match());
                            EpicHeist.getMatch().start();
                        }
                    }
                    case "start" -> {
                        if(sender.hasPermission("heist.admin")){
                            EpicHeist.setMatch(new Match());
                            EpicHeist.getMatch().start();
                        }
                    }case "stop" -> {
                        if(sender.hasPermission("heist.admin")) {
                            EpicHeist.getMatch().stop();
                        }
                    }case "panel" -> {
                        if (sender.hasPermission("heist.admin"))
                            new AdminPanelGui((Player) sender).open();
                    }case "test" ->{
                        EpicHeist.getInstance().getCrewManager().getCrewList().forEach(crew -> {
                            Bukkit.broadcastMessage(crew.getName());
                        });
                    }case "crew" -> {
                        if(args.length > 1){
                            if (sender instanceof Player) {
                                Player p = (Player) sender;
                                switch (args[1].toLowerCase()) {
                                    case "rename" ->{
                                        if (EpicHeist.getInstance().getCrewManager().isInCrew(p)) {
                                            if (EpicHeist.getInstance().getCrewManager().getCrewByPlayer(p).getLeader().equals(p)) {
                                                if(!args[1].contains("'")){
                                                    EpicHeist.getInstance().getCrewManager().getCrewByPlayer(p).setName(args[2]);
                                                    p.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&aCrew renamed!"));
                                                }else{
                                                    p.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&cInvalid name!"));
                                                }
                                            }else {
                                                p.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&cYou are not the leader of the crew!"));
                                            }
                                        } else {
                                            p.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&cYou are not in a crew!"));
                                        }

                                    }case "delete" ->{
                                        if (EpicHeist.getInstance().getCrewManager().isInCrew(p)) {
                                            if (EpicHeist.getInstance().getCrewManager().getCrewByPlayer(p).getLeader().equals(p)) {
                                                EpicHeist.getInstance().getCrewManager().removeCrew(EpicHeist.getInstance().getCrewManager().getCrewByPlayer(p));
                                                p.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&aCrew deleted!"));
                                            } else {
                                                p.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&cYou are not the leader of the crew!"));
                                            }
                                        } else {
                                            p.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&cYou are not in a crew!"));
                                        }
                                    }
                                    case "create" -> {
                                        if (!EpicHeist.getInstance().getCrewManager().isInCrew(p)) {
                                            EpicHeist.getInstance().getCrewManager().addCrew(new Crew(p, new ArrayList()));
                                            p.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&aCrew created!"));
                                        } else {
                                            p.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&cYou are already in a crew!"));
                                        }
                                    }case "invite" -> {
                                        if (EpicHeist.getInstance().getCrewManager().isInCrew(p)) {
                                            if (EpicHeist.getInstance().getCrewManager().getCrewByPlayer(p).getLeader().equals(p)) {
                                                if(EpicHeist.getInstance().getCrewManager().getCrewByPlayer(p).getMembers().size() <5){
                                                    Player target = Bukkit.getPlayer(args[2]);
                                                    if(EpicHeist.getInstance().getCrewManager().getCrewByPlayer(p).getMembers().contains(target)){
                                                        p.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&cPlayer is already in the crew!"));
                                                        return false;
                                                    }
                                                    if (target != null && target != p) {
                                                        EpicHeist.getInstance().getCrewManager().addPendingInvite(target, EpicHeist.getInstance().getCrewManager().getCrewByPlayer(p));
                                                        target.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&aYou have been invited to a crew!"));
                                                        p.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&aInvite sent!"));
                                                    } else {
                                                        p.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&cPlayer not found!"));
                                                    }
                                                }else{
                                                    p.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&cCrew is full!"));
                                                }
                                            } else {
                                                p.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&cYou are not the leader of the crew!"));
                                            }
                                        } else {
                                            p.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&cYou are not in a crew!"));
                                        }
                                    }case "kick" ->{
                                        if (EpicHeist.getInstance().getCrewManager().isInCrew(p)) {
                                            if (EpicHeist.getInstance().getCrewManager().getCrewByPlayer(p).getLeader().equals(p)) {
                                                Player target = Bukkit.getPlayer(args[2]);
                                                if (target != null) {
                                                    EpicHeist.getInstance().getCrewManager().removeMember(target, EpicHeist.getInstance().getCrewManager().getCrewByPlayer(p));
                                                    target.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&cYou have been kicked from the crew!"));
                                                    p.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&aPlayer kicked!"));
                                                } else {
                                                    p.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&cPlayer not found!"));
                                                }
                                            } else {
                                                p.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&cYou are not the leader of the crew!"));
                                            }
                                        } else {
                                            p.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&cYou are not in a crew!"));
                                        }
                                    }case "accept" -> {
                                        if(!EpicHeist.getInstance().getCrewManager().isInCrew(p)){
                                            if (EpicHeist.getInstance().getCrewManager().hasPendingInvite(p, EpicHeist.getInstance().getCrewManager().getPendingInvites().get(p.getUniqueId()))){
                                                if(EpicHeist.getInstance().getCrewManager().getPendingInvites().get(p.getUniqueId()).getMembers().size() <5){
                                                    EpicHeist.getInstance().getCrewManager().addMember(p, EpicHeist.getInstance().getCrewManager().getPendingInvites().get(p.getUniqueId()));
                                                    EpicHeist.getInstance().getCrewManager().getPendingInvites().remove(p.getUniqueId());
                                                    p.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&aYou have joined the crew!"));
                                                }else{
                                                    p.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&cCrew is full!"));
                                                }
                                            } else {
                                                p.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&cYou have no pending invites!"));
                                            }
                                        }else{
                                            p.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&cYou are already in a crew!"));
                                        }
                                    }case "leave" -> {
                                        if (EpicHeist.getInstance().getCrewManager().isInCrew(p)) {
                                            if(EpicHeist.getInstance().getCrewManager().getCrewByPlayer(p).getLeader() != p){
                                                EpicHeist.getInstance().getCrewManager().removeMember(p, EpicHeist.getInstance().getCrewManager().getCrewByPlayer(p));
                                                p.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&aYou have left the crew!"));
                                            }else{
                                                p.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&cYou are the leader of the crew!"));
                                            }
                                        } else {
                                            p.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&cYou are not in a crew!"));
                                        }
                                    }case "list" -> {
                                        if (EpicHeist.getInstance().getCrewManager().isInCrew(p)) {
                                            Crew crew = EpicHeist.getInstance().getCrewManager().getCrewByPlayer(p);
                                            p.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&aCrew members:"));
                                            p.sendMessage(StringUtil.parseColor("&7» &aLeader: &f" + crew.getLeader().getName()));
                                            crew.getMembers().forEach(member -> p.sendMessage(StringUtil.parseColor("&7» &aMember: &f" + member.getName())));
                                        } else {
                                            p.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&cYou are not in a crew!"));
                                        }
                                    }
                                    default -> p.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&cHatalı kullanım!\n" +
                                            " &7» &7/heist crew create\n" +
                                            " &7» &7/heist crew invite <player>\n" +
                                            " &7» &7/heist crew accept\n" +
                                            " &7» &7/heist crew leave\n" +
                                            " &7» &7/heist crew delete\n" +
                                            " &7» &7/heist crew list\n"));
                                }
                            }
                        }else{
                            sender.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&cHatalı kullanım!\n" +
                                    " &7» &7/heist crew create\n" +
                                    " &7» &7/heist crew invite <player>\n" +
                                    " &7» &7/heist crew accept\n" +
                                    " &7» &7/heist crew leave\n" +
                                    " &7» &7/heist crew delete\n" +
                                    " &7» &7/heist crew list\n"));
                        }
                    }
                    case "info", "version" -> {
                        sender.sendMessage(StringUtil.parseColor("\n&fDeveloped by &aPnterNN\n" +
                                "&fCurrent verison: &a" + EpicHeist.getInstance().getDescription().getVersion() + "\n" +
                                "&fDiscord: &aPnterNN" +
                                "\n "));
                    }
                    case "setswatspawn" -> {
                        if(sender.hasPermission("heist.admin")) {
                            ConfigurationHandler.getConfig().set("locations.swat-location.x", ((Player) sender).getLocation().getX());
                            ConfigurationHandler.getConfig().set("locations.swat-location.y", ((Player) sender).getLocation().getY());
                            ConfigurationHandler.getConfig().set("locations.swat-location.z", ((Player) sender).getLocation().getZ());
                            ConfigurationHandler.getConfig().set("locations.swat-location.world", Objects.requireNonNull(((Player) sender).getLocation().getWorld()).getName());
                            ConfigurationHandler.saveConfig();
                            sender.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&aSwat spawn set!"));

                        }
                    }
                    case "setbankspawn" -> {
                        if(sender.hasPermission("heist.admin")) {
                            ConfigurationHandler.getConfig().set("locations.bank-location.x", ((Player) sender).getLocation().getX());
                            ConfigurationHandler.getConfig().set("locations.bank-location.y", ((Player) sender).getLocation().getY());
                            ConfigurationHandler.getConfig().set("locations.bank-location.z", ((Player) sender).getLocation().getZ());
                            ConfigurationHandler.getConfig().set("locations.bank-location.world", Objects.requireNonNull(((Player) sender).getLocation().getWorld()).getName());
                            ConfigurationHandler.saveConfig();
                            sender.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&aBank spawn set!"));
                        }
                    }
                    case "setjailspawn" -> {
                        if(sender.hasPermission("heist.admin")) {
                            ConfigurationHandler.getConfig().set("locations.jail-location.x", ((Player) sender).getLocation().getX());
                            ConfigurationHandler.getConfig().set("locations.jail-location.y", ((Player) sender).getLocation().getY());
                            ConfigurationHandler.getConfig().set("locations.jail-location.z", ((Player) sender).getLocation().getZ());
                            ConfigurationHandler.getConfig().set("locations.jail-location.world", Objects.requireNonNull(((Player) sender).getLocation().getWorld()).getName());
                            ConfigurationHandler.saveConfig();
                            sender.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&aJail spawn set!"));

                        }
                    }
                    default -> {
                        if(sender.hasPermission("heist.admin")){
                            sender.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&cHatalı kullanım!\n" +
                                    " &7» &7/heist info\n" +
                                    " &7» &7/heist setSwatSpawn\n" +
                                    " &7» &7/heist setJailSpawn\n" +
                                    " &7» &7/heist setBankSpawn\n" +
                                    " &7» &7/heist reload\n" +
                                    " &7» &7/heist timer\n" +
                                    " &7» &7/heist crew\n"));
                        }else{
                            sender.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&cHatalı kullanım!\n" +
                                    " &7» &7/heist info\n" +
                                    " &7» &7/heist crew\n"));
                        }
                    }
                }
            }else{
                if(args.length > 0){
                    switch (args[0].toLowerCase()){
                        default -> {
                            sender.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&cBu komut sadece spawnda ve bankada çalışır!"));
                        }
                    }
                }

            }
        } else {
            if(sender.hasPermission("heist.admin")) {
                sender.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&cHatalı kullanım!\n" +
                        " &7» &7/heist info\n" +
                        " &7» &7/heist setSwatSpawn\n" +
                        " &7» &7/heist setJailSpawn\n" +
                        " &7» &7/heist setBankSpawn\n" +
                        " &7» &7/heist reload\n" +
                        " &7» &7/heist timer\n" +
                        " &7» &7/heist crew\n"));
            }else{
                sender.sendMessage(StringUtil.parseColor(ConfigurationHandler.getValue("prefix") + "&cHatalı kullanım!\n" +
                        " &7» &7/heist info\n" +
                        " &7» &7/heist crew\n"));
            }
        }
        return false;
    }
}
