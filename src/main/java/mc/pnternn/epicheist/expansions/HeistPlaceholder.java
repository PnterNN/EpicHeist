package mc.pnternn.epicheist.expansions;

import mc.pnternn.epicheist.EpicHeist;
import mc.pnternn.epicheist.config.ConfigurationHandler;
import mc.pnternn.epicheist.game.state.DataHolder;
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
            }
            if (params.equalsIgnoreCase("timer_time")) {
                return ConfigurationHandler.getTimeFormat(dataHolder.getDay(), dataHolder.getHour(), dataHolder.getMinute(), dataHolder.getSecond());
            }else if(params.equalsIgnoreCase("timer_day")){
                return String.valueOf(dataHolder.getDay());
            }else if(params.equalsIgnoreCase("timer_hour")){
                return String.valueOf(dataHolder.getHour());
            }else if(params.equalsIgnoreCase("timer_minute")){
                return String.valueOf(dataHolder.getMinute());
            }else if(params.equalsIgnoreCase("timer_second")){
                return String.valueOf(dataHolder.getSecond());
            }else if(params.equalsIgnoreCase("state")){
                return dataHolder.getState().getClass().getSimpleName();
            }
        }
        return "...";
    }
}
