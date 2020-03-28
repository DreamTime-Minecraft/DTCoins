package su.dreamtime.dtcoins.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import su.dreamtime.dtcoins.DTCoinsAPI;
import su.dreamtime.dtcoins.data.DTCoinsData;

public class DTCoinsEventListener implements Listener
{
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        DTCoinsAPI.addPlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e)
    {
        DTCoinsData.remove(e.getPlayer().getUniqueId().toString());
    }
}
