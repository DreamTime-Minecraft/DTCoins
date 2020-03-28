package su.dreamtime.dtcoins;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import ru.sgk.dreamtimeapi.data.Database;
import su.dreamtime.dtcoins.bungee.DTCoinsMessenger;
import su.dreamtime.dtcoins.data.DTCoinsData;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.UUID;

public class DTCoinsAPI
{

    // TODO: Написать функционал..
    public static void addPlayer(OfflinePlayer p)
    {
        addPlayer(p, 0);
    }
    public static boolean hasPlayer(OfflinePlayer p)
    {
        Database db = Main.getDB();
        try (ResultSet rs = db.query("SELECT * FROM `dtcoins` WHERE `uuid`= ?", p.getUniqueId().toString()))
        {
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void addPlayer(OfflinePlayer p, int initCoins)
    {
        String uuid = p.getUniqueId().toString();
        Main.getDB().execute(
                "INSERT INTO `dtcoins` (`uuid`, `coins`) " +
                        "SELECT * FROM (SELECT ?, ?) tmp " +
                        "WHERE NOT EXISTS ( " +
                        "SELECT `uuid` FROM `dtcoins` WHERE `uuid` = ? " +
                        ") LIMIT 1;",
                uuid,
                initCoins,
                uuid);
        sendUpdate(p);
    }
    /**
     * @return coins
     */
    public static double setCoins(OfflinePlayer p, double count) throws IllegalArgumentException
    {
        if (Main.getDB().execute("UPDATE `dtcoins` SET coins = ? WHERE `uuid` = ?", count, p.getUniqueId().toString()) == 0) {
            throw new IllegalArgumentException("Player not found!");
        }
        sendUpdate(p);
        return count;
    }
    public static void resetCoins(OfflinePlayer p) throws IllegalArgumentException
    {
        setCoins(p, 0);
    }
    /**
     * @return coins was added
     */
    public static double addCoins(OfflinePlayer p, double count) throws IllegalArgumentException
    {
        if (Main.getDB().execute("UPDATE `dtcoins` SET `coins` = `coins` + ? WHERE `uuid` = ?", count, p.getUniqueId().toString()) == 0)
        {
            throw new IllegalArgumentException("Player not found!");
        }
        sendUpdate(p);
        return count;
    }
    /**
     * @return coins was taked
     */
    public static double takeCoins(OfflinePlayer p, double count) throws IllegalArgumentException
    {
        if (Main.getDB().execute("UPDATE `dtcoins` SET `coins` = `coins` - ? WHERE `uuid` = ?", count, p.getUniqueId().toString()) == 0)
        {
            throw new IllegalArgumentException("Player not found!");
        }
        sendUpdate(p);
        return count;
    }

    /**
     * @return player's coins
     * @throws IllegalArgumentException if player not found
     */
    public static double getCoins(OfflinePlayer p) throws IllegalArgumentException
    {
        // TODO: getCoins

        String uuid = p.getUniqueId().toString();
        if (DTCoinsData.has(uuid))
        {
            return DTCoinsData.getCoins(uuid);
        }
        Database db = Main.getDB();
        try (ResultSet rs = db.query("SELECT * FROM `dtcoins` WHERE `uuid`= ?", uuid))
        {
            if (!rs.next())
                throw new IllegalArgumentException("Player not found!");
            double coins = rs.getDouble("coins");
            DTCoinsData.putNew(uuid, (int)coins);
            return coins;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @return coins was added
     */
    public static double addMultCoins(OfflinePlayer p, double count, double mult) throws IllegalArgumentException
    {
        double result = count * mult;
        if (Main.getDB().execute("UPDATE `dtcoins` SET `coins` = `coins` + ? WHERE `uuid` = ?", result, p.getUniqueId().toString()) == 0)
        {
            throw new IllegalArgumentException("Player not found!");
        }
        sendUpdate(p);
        return result;
    }

    private static void sendUpdate(OfflinePlayer p)
    {
        UUID UniqueId = p.getUniqueId();
        String uuid = p.getUniqueId().toString();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward"); // So BungeeCord knows to forward it
        out.writeUTF("ALL");
        out.writeUTF("dtcoins");

        try (ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
             DataOutputStream msgout = new DataOutputStream(msgbytes);){
            msgout.writeUTF("update");
            msgout.writeLong(UniqueId.getLeastSignificantBits());
            msgout.writeLong(UniqueId.getMostSignificantBits());

            out.writeShort(msgbytes.toByteArray().length);
            out.write(msgbytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Bukkit.getOnlinePlayers().size() > 0)
        {
            for (Player pl : Bukkit.getOnlinePlayers()) {

                pl.sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());

                break;
            }

        }
        else {
            Bukkit.getServer().sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
        }
        DTCoinsMessenger.updateUserData(uuid);
    }
    /**
     * Склоняем слова правильно
     * @param ed неизменяемая часть слова, которую нужно просклонять
     * @param a окончание для слова, в случае если число оканчивается на 1
     * @param b окончание для слова, в случае если число оканчивается на 2, 3 или 4
     * @param c окончание для слова, в случае если число оканчивается на 0, 5...9 и 11...19
     * @param n число, по которому идёт склонение
     * @return правильно просклонённое слово по числу
     */
    public static String padezh(String ed, String a, String b, String c, double n) {
        if (n < 0) n = -n;
        double last = n % 100;
        if (last > 10 && last < 21) return ed + c;
        last = n % 10;
        if (last == 0 || last > 4) return ed + c;
        if (last == 1) return ed + a;
        if (last < 5) return ed + b;
        return ed + c;
    }
}
