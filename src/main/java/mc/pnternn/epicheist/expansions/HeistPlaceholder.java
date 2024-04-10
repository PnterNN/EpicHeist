package mc.pnternn.epicheist.expansions;

import mc.pnternn.epicheist.EpicHeist;
import mc.pnternn.epicheist.config.ConfigurationHandler;
import mc.pnternn.epicheist.game.GameState;
import mc.pnternn.epicheist.game.state.*;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import javax.xml.crypto.Data;

public class HeistPlaceholder extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "epicheist";
    }

    @Override
    public @NotNull String getAuthor() {
        return "PnterNN";
    }

    @Override
    public @NotNull String getVersion() {
        return EpicHeist.getInstance().getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        EpicHeist.getInstance();
        if (EpicHeist.getMatch() != null){
            DataHolder dataHolder = EpicHeist.getMatch().getDataHolder();
            if(params.equalsIgnoreCase("timer_full_time")){
                return dataHolder.getDay() + "gün, " + dataHolder.getHour() + "saat, " + dataHolder.getMinute() + "dakika, " + dataHolder.getSecond() + "saniye";
            }else if (params.equalsIgnoreCase("timer_time")) {
                return ConfigurationHandler.getTimeFormat(dataHolder.getDay(), dataHolder.getHour(), dataHolder.getMinute(), dataHolder.getSecond());
            }else if(params.equalsIgnoreCase("timer_day")) {
                return String.valueOf(dataHolder.getDay());
            }else if(params.equalsIgnoreCase("event_state")){
                if(EpicHeist.getMatch().getDataHolder().getState().getClass().equals(PlayingState.class) ||
                        EpicHeist.getMatch().getDataHolder().getState().getClass().equals(SwatState.class) ||
                        EpicHeist.getMatch().getDataHolder().getState().getClass().equals(EscapingState.class) ||
                        EpicHeist.getMatch().getDataHolder().getState().getClass().equals(StartingState.class)){
                    return "True";
                }else{
                    return "False";
                }
            }else if(params.equalsIgnoreCase("timer_hour")){
                return String.valueOf(dataHolder.getHour());
            }else if(params.equalsIgnoreCase("timer_minute")){
                return String.valueOf(dataHolder.getMinute());
            }else if(params.equalsIgnoreCase("timer_second")){
                return String.valueOf(dataHolder.getSecond());
            }else if(params.equalsIgnoreCase("state")){
                return dataHolder.getState().getClass().getSimpleName();
            }else if (params.equalsIgnoreCase("formatted_time")){
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
            }
        }
        return "...";
    }
}
