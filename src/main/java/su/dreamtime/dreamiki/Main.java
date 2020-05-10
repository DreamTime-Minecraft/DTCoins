package su.dreamtime.dreamiki;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.sgk.dreamtimeapi.data.Database;
import ru.sgk.dreamtimeapi.io.ConfigManager;
import su.dreamtime.dreamiki.bungee.DTCoinsMessenger;
import su.dreamtime.dreamiki.commands.MoneyCommand;
import su.dreamtime.dreamiki.data.DTCoinsData;
import su.dreamtime.dreamiki.events.DTCoinsEventListener;
import su.dreamtime.dreamiki.placeholders.DreamikPlaceholder;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin
{
    private static Main instance;
    private static Database db;
    private static ConfigManager configManager;
    private static Economy economy = null;
    private static List<PlaceholderExpansion> papiExpansions;
    public static void initDB()
    {
        if (db!= null)
        {
            try {
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String host = configManager.getMainConfig().getString("database.host");
        int port = configManager.getMainConfig().getInt("database.port");
        String login = configManager.getMainConfig().getString("database.login");
        String password = configManager.getMainConfig().getString("database.password");
        String database = configManager.getMainConfig().getString("database.database");

        db = new Database(host, port, login, password, database);
        db.execute("CREATE TABLE IF NOT EXISTS `dreamiki` (" +
                    "`id` SERIAL PRIMARY KEY," +
                    "`uuid` VARCHAR(255)," +
                    "`coins` DOUBLE DEFAULT 0," +
                    "unique(`uuid`)," +
                    "index(`uuid`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_general_ci");
        getInstance().getLogger().info("db was initialized");
    }
    public static void initPlaceholderAPI()
    {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            papiExpansions = new ArrayList<>();
            PlaceholderExpansion exp = new DreamikPlaceholder();
            exp.register();
            papiExpansions.add(exp);
        }
    }
    private boolean init()
    {
        configManager = new ConfigManager(this);
        initPlaceholderAPI();
        initDB();
        return true;
    }

    private void registerEvents()
    {
        // TODO: register events
        getServer().getPluginManager().registerEvents(new DTCoinsEventListener(), this);
    }
    @Override
    public void onEnable()
    {

        instance = this;
        if (!init()) return;
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new DTCoinsMessenger());
        registerCommands();
        registerEvents();

        for (Player p : Bukkit.getOnlinePlayers())
        {
            DTCoinsAPI.addPlayer(p);
        }
    }

    private void registerCommands()
    {
        getCommand("donatewallet").setExecutor(new MoneyCommand());
    }

    @Override
    public void onDisable()
    {

        papiExpansions.clear();
        for (Player p : Bukkit.getOnlinePlayers())
        {
            DTCoinsData.remove(p.getUniqueId().toString());
        }
        try {
            if (db != null)
                db.close();
            db = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        getServer().getServicesManager().unregisterAll(this);
    }

    public static void unregisterPlaceholderAPI()
    {
        if (papiExpansions == null)
        {
            return;
        }
        for (PlaceholderExpansion exp : papiExpansions)
        {
            PlaceholderAPI.unregisterExpansion(exp);

        }
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    public static Main getInstance() {
        return instance;
    }

    public static Database getDB() {
        if (db == null)
        {
            initDB();
        }
        return db;
    }

    public static Economy getEconomy() {
        return economy;
    }
}
