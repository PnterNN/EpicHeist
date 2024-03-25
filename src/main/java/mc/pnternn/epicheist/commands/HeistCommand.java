package mc.pnternn.epicheist.commands;

import mc.pnternn.epicheist.EpicHeist;
import mc.pnternn.epicheist.config.ConfigurationHandler;
import mc.pnternn.epicheist.game.Match;
import mc.pnternn.epicheist.game.state.WaitingState;
import mc.pnternn.epicheist.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class HeistCommand implements CommandExecutor {
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String str, String[] args) {
        if(args.length> 0){
            switch (args[0]) {
                case "reload" -> {
                    ConfigurationHandler.reloadConfig();
                    sender.sendMessage(ColorUtil.colorize(ConfigurationHandler.getValue("prefix") + "&aConfig reloaded!"));
                }
                case "start" -> {
                    EpicHeist.setMatch(new Match());
                    EpicHeist.getMatch().start();
                }
                case "test" -> {
                    Player p = (Player) sender;
                    p.sendBlockChange(p.getLocation(), Material.GOLD_BLOCK.createBlockData());
                }
                case "info", "version" -> sender.sendMessage(ColorUtil.colorize("\n&fDeveloped by &aPnterNN\n" +
                        "&fCurrent verison: &a" + EpicHeist.getInstance().getDescription().getVersion() + "\n" +
                        "&fDiscord: &aPnterNN" +
                        "\n "));
                case "setSwatSpawn" -> {
                    ConfigurationHandler.getConfig().set("locations.swat-location.x", ((Player) sender).getLocation().getX());
                    ConfigurationHandler.getConfig().set("locations.swat-location.y", ((Player) sender).getLocation().getY());
                    ConfigurationHandler.getConfig().set("locations.swat-location.z", ((Player) sender).getLocation().getY());
                    ConfigurationHandler.getConfig().set("locations.swat-location.world", Objects.requireNonNull(((Player) sender).getLocation().getWorld()).getName());
                    ConfigurationHandler.saveConfig();
                    sender.sendMessage(ColorUtil.colorize(ConfigurationHandler.getValue("prefix") + "&aSwat spawn set!"));
                }
                case "setBankSpawn" -> {
                    ConfigurationHandler.getConfig().set("locations.bank-location.x", ((Player) sender).getLocation().getX());
                    ConfigurationHandler.getConfig().set("locations.bank-location.y", ((Player) sender).getLocation().getY());
                    ConfigurationHandler.getConfig().set("locations.bank-location.z", ((Player) sender).getLocation().getY());
                    ConfigurationHandler.getConfig().set("locations.bank-location.world", Objects.requireNonNull(((Player) sender).getLocation().getWorld()).getName());
                    ConfigurationHandler.saveConfig();
                    sender.sendMessage(ColorUtil.colorize(ConfigurationHandler.getValue("prefix") + "&aBank spawn set!"));
                }
                case "setJailSpawn" -> {
                    ConfigurationHandler.getConfig().set("locations.jail-location.x", ((Player) sender).getLocation().getX());
                    ConfigurationHandler.getConfig().set("locations.jail-location.y", ((Player) sender).getLocation().getY());
                    ConfigurationHandler.getConfig().set("locations.jail-location.z", ((Player) sender).getLocation().getY());
                    ConfigurationHandler.getConfig().set("locations.jail-location.world", Objects.requireNonNull(((Player) sender).getLocation().getWorld()).getName());
                    ConfigurationHandler.saveConfig();
                    sender.sendMessage(ColorUtil.colorize(ConfigurationHandler.getValue("prefix") + "&aJail spawn set!"));
                }
            }
        } else {
            sender.sendMessage(ColorUtil.colorize(ConfigurationHandler.getValue("prefix") + "&cHatalı kullanım!\n" +
                               " &7» &7/heist info\n" +
                               " &7» &7/heist setSwatSpawn\n" +
                               " &7» &7/heist setBankSpawn\n" +
                               " &7» &7/heist reload\n" +
                               " &7» &7/heist timer\n"));
        }
        return false;
    }
}
