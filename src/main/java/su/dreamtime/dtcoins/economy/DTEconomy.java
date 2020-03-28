package su.dreamtime.dtcoins.economy;

import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import su.dreamtime.dtcoins.DTCoinsAPI;
import su.dreamtime.dtcoins.Main;
import java.util.List;

public class DTEconomy extends AbstractEconomy
{
    private final String name = "DreamTime Economy";
    private boolean enabled = false;
    public DTEconomy()
    {
        enabled = true;
    }

    @Override
    public boolean isEnabled()
    {
        return enabled;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }

    @Override
    public String format(double amount) {
        return String.valueOf(amount);
    }

    @Override
    public String currencyNamePlural() {
        return "коины";
    }

    @Override
    public String currencyNameSingular() {
        return "коин";
    }

    @Override
    public boolean hasAccount(String playerName) {
        try {
            return DTCoinsAPI.hasPlayer(Bukkit.getPlayer(playerName));
        }catch (IllegalArgumentException e)
        {
            return false;
        }

    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public double getBalance(String playerName) {
        return DTCoinsAPI.getCoins(Bukkit.getPlayer(playerName));
    }

    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(playerName);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return getBalance(playerName) >= amount;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        DTCoinsAPI.takeCoins(Bukkit.getPlayer(playerName), amount);
        double coins = DTCoinsAPI.getCoins(Bukkit.getPlayer(playerName));
        return new EconomyResponse(amount, coins, EconomyResponse.ResponseType.SUCCESS,"");
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        DTCoinsAPI.addCoins(Bukkit.getPlayer(playerName), amount);
        double coins = DTCoinsAPI.getCoins(Bukkit.getPlayer(playerName));
        return new EconomyResponse(amount, coins, EconomyResponse.ResponseType.SUCCESS,"");
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        try
        {
            DTCoinsAPI.addPlayer(Bukkit.getPlayer(playerName));
            return true;
        }
        catch (IllegalArgumentException e )
        {
            return false;
        }
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return createPlayerAccount(playerName);
    }
}
