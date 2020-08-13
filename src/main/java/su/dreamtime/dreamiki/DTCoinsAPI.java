package su.dreamtime.dreamiki;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import ru.sgk.dreamtimeapi.data.Database;
import su.dreamtime.dreamiki.bungee.DTCoinsMessenger;
import su.dreamtime.dreamiki.data.DTCoinsData;
import su.dreamtime.dreamiki.data.PurchasePlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
        if (p == null)
        {
            return false;
        }
        String uuid = p.getUniqueId().toString();

        Database db = Main.getDB();
        try (ResultSet rs = db.query("SELECT * FROM `dreamiki` WHERE `uuid`= ?", uuid))
        {
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void addPlayer(OfflinePlayer p, long initCoins)
    {
        if (p == null)
        {
            return;
        }
        String uuid = p.getUniqueId().toString();
        Main.getDB().execute(
                "INSERT INTO `dreamiki` (`uuid`, `coins`) " +
                        "SELECT * FROM (SELECT ?, ?) tmp " +
                        "WHERE NOT EXISTS ( " +
                        "SELECT `uuid` FROM `dreamiki` WHERE `uuid` = ? " +
                        ") LIMIT 1;",
                uuid,
                initCoins,
                uuid);
        sendUpdate(p);
    }
    /**
     * @return coins
     */
    public static long setCoins(OfflinePlayer p, long count) throws IllegalArgumentException
    {
        if (p == null)
        {
            return 0;
        }
        if (Main.getDB().execute("UPDATE `dreamiki` SET coins = ? WHERE `uuid` = ?", count, p.getUniqueId().toString()) == 0) {
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
    public static long addCoins(OfflinePlayer p, long count) throws IllegalArgumentException
    {
        if (p == null)
        {
            return 0;
        }
        if (Main.getDB().execute("UPDATE `dreamiki` SET `coins` = `coins` + ? WHERE `uuid` = ?", count, p.getUniqueId().toString()) == 0)
        {
            throw new IllegalArgumentException("Player not found!");
        }
        sendUpdate(p);
        return count;
    }
    /**
     * @return coins was taked
     */
    public static long takeCoins(OfflinePlayer p, long count) throws IllegalArgumentException
    {
        if (p == null)
        {
            return 0;
        }
        if (Main.getDB().execute("UPDATE `dreamiki` SET `coins` = `coins` - ? WHERE `uuid` = ?", count, p.getUniqueId().toString()) == 0)
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
    public static long getCoins(OfflinePlayer p) throws IllegalArgumentException
    {
        // TODO: getCoins

        if (p == null)
        {
            return 0;
        }
        String uuid = p.getUniqueId().toString();
        if (DTCoinsData.has(uuid))
        {
            return DTCoinsData.getCoins(uuid);
        }
        Database db = Main.getDB();
        try (ResultSet rs = db.query("SELECT * FROM `dreamiki` WHERE `uuid`= ?", uuid))
        {
            if (!rs.next())
                throw new IllegalArgumentException("Player not found!");
            long coins = rs.getLong("coins");
            DTCoinsData.putNew(uuid, coins);
            return coins;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        catch (NullPointerException ignored) { }
        return 0;
    }

    /**
     *
     * @param username player's nickname
     * @param command command to dispatch
     * @param item is player buy an item or not
     */
    public static void addPurchase(String username, String command, boolean item) {
        Main.getDB().execute("INSERT INTO `dreamiki_buys` (`username`,`command`,`item`)" +
                "VALUES (?, ?, ?)", username, command, item);
    }

    public static PurchasePlayer getPurchase(long id) {
        Database db = Main.getDB();
        try (ResultSet rs = db.query("SELECT * FROM `dreamiki_buys` WHERE `id`= ?",id)) {
            if(!rs.next()) return null;
            return new PurchasePlayer(rs.getString("username"), rs.getString("command"),
                    rs.getBoolean("given"), rs.getBoolean("item"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<PurchasePlayer> getPurchases(String username) {
        List<PurchasePlayer> list = new ArrayList<>();
        Database db = Main.getDB();
        try (ResultSet rs = db.query("SELECT * FROM `dreamiki_buys` WHERE `username`= ? AND `server`= ?",
                username, Main.getConfigManager().getMainConfig().getString("server"))) {
            if(!rs.next()) return list;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * @return coins was added
     */
    public static long addMultCoins(OfflinePlayer p, long count, double mult) throws IllegalArgumentException
    {
        if (p == null)
        {
            return 0;
        }
        long result = Math.round(count * mult);
        if (Main.getDB().execute("UPDATE `dreamiki` SET `coins` = `coins` + ? WHERE `uuid` = ?", result, p.getUniqueId().toString()) == 0)
        {
            throw new IllegalArgumentException("Player not found!");
        }
        sendUpdate(p);
        return result;
    }

    private static void sendUpdate(OfflinePlayer p)
    {
        if (p == null)
        {
            return;
        }
        UUID UniqueId = p.getUniqueId();
        String uuid = p.getUniqueId().toString();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Forward"); // So BungeeCord knows to forward it
        out.writeUTF("ALL");
        out.writeUTF("dreamiki");

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
    public static String padezh(String ed, String a, String b, String c, long n) {
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
