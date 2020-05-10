package su.dreamtime.dreamiki.data;

import org.bukkit.Bukkit;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DTCoinsData
{
    private static Map<String, Integer> coinsData = new ConcurrentHashMap<>();

    public static synchronized Integer getCoins(String uuid)
    {
        return coinsData.get(uuid);
    }

    public static synchronized void rewrite(String uuid, Integer coins)
    {
        coinsData.remove(uuid);
        putNew(uuid, coins);
    }

    public static synchronized void remove(String uuid)
    {
        coinsData.remove(uuid);
    }

    public static synchronized void putNew(String uuid, Integer coins)
    {
        if (!coinsData.containsKey(uuid)) {
            coinsData.put(uuid, coins);

        }
        if (Bukkit.getPlayer(UUID.fromString(uuid)) == null || !(Bukkit.getPlayer(UUID.fromString(uuid)).isOnline()))
        {
            remove(uuid);
        }
    }

    public static synchronized boolean has(String uuid)
    {
        return coinsData.containsKey(uuid);
    }
}
