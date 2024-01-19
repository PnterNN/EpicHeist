package mc.pnternn.epicheist.commands;

import mc.pnternn.epicheist.config.ConfigurationHandler;
import mc.pnternn.epicheist.game.GameState;
import mc.pnternn.epicheist.game.Match;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HeistCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {
        Player p = null;
        if (sender instanceof Player) {
            p = (Player)sender;
        }else{
            sender.sendMessage("You must be a player to use this command!");
            return false;
        }
        if(args.length> 0){
            if(args[0].equalsIgnoreCase("setspawn")){
                p.sendMessage("You have set the heist spawn!");
                ConfigurationHandler.getConfig().set("locations.spawn.world", p.getLocation().getWorld().getName());
                ConfigurationHandler.getConfig().set("locations.spawn.x", p.getLocation().getX());
                ConfigurationHandler.getConfig().set("locations.spawn.y", p.getLocation().getY());
                ConfigurationHandler.getConfig().set("locations.spawn.z", p.getLocation().getZ());
                ConfigurationHandler.getConfig().set("locations.spawn.yaw", p.getLocation().getYaw());
                ConfigurationHandler.getConfig().set("locations.spawn.pitch", p.getLocation().getPitch());
            } else if (args[0].equalsIgnoreCase("setjailspawn")) {
                p.sendMessage("You have set the heist jail spawn!");
                ConfigurationHandler.getConfig().set("locations.jail.world", p.getLocation().getWorld().getName());
                ConfigurationHandler.getConfig().set("locations.jail.x", p.getLocation().getX());
                ConfigurationHandler.getConfig().set("locations.jail.y", p.getLocation().getY());
                ConfigurationHandler.getConfig().set("locations.jail.z", p.getLocation().getZ());
                ConfigurationHandler.getConfig().set("locations.jail.yaw", p.getLocation().getYaw());
                ConfigurationHandler.getConfig().set("locations.jail.pitch", p.getLocation().getPitch());
            } else if (args[0].equalsIgnoreCase("setpolicespawn")) {
                p.sendMessage("You have set the heist police spawn!");
                ConfigurationHandler.getConfig().set("locations.police.world", p.getLocation().getWorld().getName());
                ConfigurationHandler.getConfig().set("locations.police.x", p.getLocation().getX());
                ConfigurationHandler.getConfig().set("locations.police.y", p.getLocation().getY());
                ConfigurationHandler.getConfig().set("locations.police.z", p.getLocation().getZ());
                ConfigurationHandler.getConfig().set("locations.police.yaw", p.getLocation().getYaw());
                ConfigurationHandler.getConfig().set("locations.police.pitch", p.getLocation().getPitch());
            }else if(args[0].equalsIgnoreCase("start")){
                Match match = new Match();
                match.start();
            }else if(args[0].equalsIgnoreCase("test")) {
                Bukkit.broadcastMessage("test");
            } else {
                p.sendMessage("You have entered an invalid argument!");
            }
        } else {
            p.sendMessage("You have entered an invalid argument!");
        }
        return false;
    }
}
