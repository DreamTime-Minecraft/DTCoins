package su.dreamtime.dtcoins.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import su.dreamtime.dtcoins.DTCoinsAPI;

public class DTCoinPlaceholder extends PlaceholderExpansion {
    @Override
    public String getIdentifier() {
        return "dt";
    }

    @Override
    public String onRequest(OfflinePlayer p, String params) {
        if (params.equalsIgnoreCase("coins"))
            return String.valueOf(DTCoinsAPI.getCoins(p));
        return "§cdt_error";
    }

    @Override
    public String getAuthor() {
        return "SteveGrKek";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }
}
