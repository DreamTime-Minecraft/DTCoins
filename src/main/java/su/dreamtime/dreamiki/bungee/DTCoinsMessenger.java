package su.dreamtime.dreamiki.bungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import su.dreamtime.dreamiki.DTCoinsAPI;
import su.dreamtime.dreamiki.data.DTCoinsData;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

public class DTCoinsMessenger implements PluginMessageListener
{

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message)
    {
        if (channel.equalsIgnoreCase("BungeeCord") || channel.equalsIgnoreCase("bungeecord:main"))
        {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            String subChannel = in.readUTF();
            if (subChannel.equalsIgnoreCase("dreamiki"))
            {
                short len = in.readShort();
                byte[] msgbytes = new byte[len];
                in.readFully(msgbytes);

                try (DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));){
                    String msg = msgin.readUTF();
                    if (msg.equalsIgnoreCase("update")) {

                        long least = 0;
                            least = msgin.readLong();
                        long most = msgin.readLong();
                        String uuid = new UUID(most, least).toString();

                        updateUserData(uuid);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void updateUserData(String uuid)
    {

        OfflinePlayer p = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
        if (p != null && p.isOnline())
        {
            DTCoinsData.remove(uuid);
            DTCoinsAPI.getCoins(p);
        }
    }

}
