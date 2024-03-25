package mc.pnternn.epicheist.Expansions;

import mc.pnternn.epicheist.EpicHeist;
import mc.pnternn.epicheist.config.ConfigurationHandler;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

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
            EpicHeist.getInstance();
            if(params.equalsIgnoreCase("timer_full_time")){
                return EpicHeist.getMatch().getDay() + "gün, " + EpicHeist.getMatch().getHour() + "saat, " + EpicHeist.getMatch().getMinute() + "dakika, " + EpicHeist.getMatch().getSecond() + "saniye";
            }
            if (params.equalsIgnoreCase("timer_time")) {
                return ConfigurationHandler.getTimeFormat(EpicHeist.getMatch().getDay(), EpicHeist.getMatch().getHour(), EpicHeist.getMatch().getMinute(), EpicHeist.getMatch().getSecond());
            }else if(params.equalsIgnoreCase("timer_day")){
                return String.valueOf(EpicHeist.getMatch().getDay());
            }else if(params.equalsIgnoreCase("timer_hour")){
                return String.valueOf(EpicHeist.getMatch().getHour());
            }else if(params.equalsIgnoreCase("timer_minute")){
                return String.valueOf(EpicHeist.getMatch().getMinute());
            }else if(params.equalsIgnoreCase("timer_second")){
                return String.valueOf(EpicHeist.getMatch().getSecond());
            }else if(params.equalsIgnoreCase("state")){
                return EpicHeist.getMatch().getState().getClass().getSimpleName();
            }
        }
        return "...";
    }
}
