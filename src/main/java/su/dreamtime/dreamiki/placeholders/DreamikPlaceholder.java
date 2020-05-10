package su.dreamtime.dreamiki.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import su.dreamtime.dreamiki.DTCoinsAPI;

public class DreamikPlaceholder extends PlaceholderExpansion {
    @Override
    public String getIdentifier() {
        return "dreamiki";
    }

    @Override
    public String onRequest(OfflinePlayer p, String params) {
        if (params.equalsIgnoreCase("bal"))
            return String.valueOf(DTCoinsAPI.getCoins(p));
        return params;
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
