package su.dreamtime.dtcoins.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.sgk.dreamtimeapi.io.ConfigManager;
import su.dreamtime.dtcoins.DTCoinsAPI;
import su.dreamtime.dtcoins.Main;

public class MoneyCommand implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equalsIgnoreCase("money"))
        {
            if (args.length <= 0)
            {
                if (sender.hasPermission("dtcoins.get") || sender.hasPermission("dtcoins.get.other"))
                {
                    if (!isPlayer(sender))
                        return true;
                    Player player = (Player) sender;
                    double coins = DTCoinsAPI.getCoins(player);
                    String msgGet = Main.getConfigManager().getMainConfig().getString("messages.get.message");
                    String word = Main.getConfigManager().getMainConfig().getString("messages.get.currency.word");
                    String a = Main.getConfigManager().getMainConfig().getString("messages.get.currency.a");
                    String b = Main.getConfigManager().getMainConfig().getString("messages.get.currency.b");
                    String c = Main.getConfigManager().getMainConfig().getString("messages.get.currency.c");
                    msgGet = ChatColor.translateAlternateColorCodes(
                            '&',
                            msgGet.replaceAll("%coins%", coins + "")
                            .replaceAll("%curr_name%", DTCoinsAPI.padezh(word, a, b, c, coins))
                    );
                    player.sendMessage(msgGet);
                }
            }
            else if (args.length == 1)
            {
                if (args[0].equalsIgnoreCase("help"))
                {
                    sendHelp(sender);
                    return true;
                }
                else if (args[0].equalsIgnoreCase("reload"))
                {
                    if (!sender.hasPermission("dtcoions.reload"))
                    {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                Main.getConfigManager().getMainConfig().
                                        getString("messages.no-perm"))
                        );
                        return true;
                    }
                    else
                    {
                        Main.getConfigManager().loadMainConfig();

                        Main.initDB();

                        Main.getInstance().initEconomy();

                        Main.unregisterPlaceholderAPI();
                        Main.initPlaceholderAPI();
                        return true;
                    }
                }
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);

                if (!sender.hasPermission("dtcoions.get.other"))
                {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            Main.getConfigManager().getMainConfig().
                                    getString("messages.no-perm"))
                    );
                    return true;
                }
                if (offlinePlayer == null)
                {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        Main.getConfigManager().getMainConfig().
                                getString("messages.player-not-found"))
                    );
                    return true;
                }
                double coins = DTCoinsAPI.getCoins(offlinePlayer);
                String msg = Main.getConfigManager().getMainConfig().getString("messages.get-other.message");

                String word = Main.getConfigManager().getMainConfig().getString("messages.get-other.currency.word");
                String a = Main.getConfigManager().getMainConfig().getString("messages.get-other.currency.a");
                String b = Main.getConfigManager().getMainConfig().getString("messages.get-other.currency.b");
                String c = Main.getConfigManager().getMainConfig().getString("messages.get-other.currency.c");

                msg = ChatColor.translateAlternateColorCodes(
                        '&',
                        msg .replaceAll("%coins%", coins + "")
                            .replaceAll("%player%", offlinePlayer.getName())
                            .replaceAll("%curr_name%", DTCoinsAPI.padezh(word, a, b, c, coins))
                );

                sender.sendMessage(msg);
            }
            else if (args.length > 1) {
                try
                {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                    Player player = offlinePlayer.getPlayer();
                    if (args[1].equalsIgnoreCase("set")) {
                        if (!sender.hasPermission("dtcoions.set")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    Main.getConfigManager().getMainConfig().
                                            getString("messages.player-no-perm"))
                            );
                            return true;
                        }
                        if (offlinePlayer == null) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    Main.getConfigManager().getMainConfig().
                                            getString("messages.player-not-found"))
                            );
                            return true;
                        }
                        try {
                            double count = Integer.parseInt(args[2]);
                            count = DTCoinsAPI.setCoins(offlinePlayer, count);

                            String msgFrom = Main.getConfigManager().getMainConfig().getString("messages.set.from.message", "");

                            String word = Main.getConfigManager().getMainConfig().getString("messages.set.currency.word");
                            String a = Main.getConfigManager().getMainConfig().getString("messages.set.currency.a");
                            String b = Main.getConfigManager().getMainConfig().getString("messages.set.currency.b");
                            String c = Main.getConfigManager().getMainConfig().getString("messages.set.currency.c");

                            msgFrom = ChatColor.translateAlternateColorCodes(
                                    '&',
                                    msgFrom.replaceAll("%coins%", count + "")
                                            .replaceAll("%player%", offlinePlayer.getName())
                                            .replaceAll("%curr_name%", DTCoinsAPI.padezh(word, a, b, c, count))
                            );
                            String msgTo = Main.getConfigManager().getMainConfig().getString("messages.set.to.message", "");


                            msgTo = ChatColor.translateAlternateColorCodes('&',
                                    msgTo.replaceAll("%coins%", count + "")
                                            .replaceAll("%player%", sender.getName())
                                            .replaceAll("%curr_name%", DTCoinsAPI.padezh(word, a, b, c, count))
                            );
                            sender.sendMessage(msgFrom);
                            if (player != null)
                                player.sendMessage(msgTo);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    Main.getConfigManager().getMainConfig().
                                            getString("messages.wrong-args"))
                            );
                        } catch (IllegalArgumentException e) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    Main.getConfigManager().getMainConfig().
                                            getString("messages.player-not-found"))
                            );
                        }
                    } else if (args[1].equalsIgnoreCase("add")) {
                        if (!sender.hasPermission("dtcoions.add")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    Main.getConfigManager().getMainConfig().
                                            getString("messages.no-perm"))
                            );
                            return true;
                        }
                        if (offlinePlayer == null) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    Main.getConfigManager().getMainConfig().
                                            getString("messages.player-not-found"))
                            );
                            return true;
                        }
                        try {
                            double count = Integer.parseInt(args[2]);
                            if (args.length >= 4)
                            {
                                double factor = Integer.parseInt(args[3]);
                                count *= factor;
                            }
                            count = DTCoinsAPI.addCoins(offlinePlayer, count);
                            String msgFrom = Main.getConfigManager().getMainConfig().getString("messages.add.from.message", "");

                            String word = Main.getConfigManager().getMainConfig().getString("messages.add.currency.word");
                            String a = Main.getConfigManager().getMainConfig().getString("messages.add.currency.a");
                            String b = Main.getConfigManager().getMainConfig().getString("messages.add.currency.b");
                            String c = Main.getConfigManager().getMainConfig().getString("messages.add.currency.c");

                            msgFrom = ChatColor.translateAlternateColorCodes(
                                    '&',
                                    msgFrom.replaceAll("%coins%", count + "")
                                            .replaceAll("%player%", offlinePlayer.getName())
                                            .replaceAll("%curr_name%", DTCoinsAPI.padezh(word, a, b, c, count))
                            );
                            String msgTo = Main.getConfigManager().getMainConfig().getString("messages.add.to.message", "");


                            msgTo = ChatColor.translateAlternateColorCodes('&',
                                    msgTo.replaceAll("%coins%", count + "")
                                            .replaceAll("%player%", sender.getName())
                                            .replaceAll("%curr_name%", DTCoinsAPI.padezh(word, a, b, c, count))
                            );
                            sender.sendMessage(msgFrom);
                            if (player != null)
                                player.sendMessage(msgTo);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    Main.getConfigManager().getMainConfig().
                                            getString("messages.wrong-args"))
                            );
                        } catch (IllegalArgumentException e) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    Main.getConfigManager().getMainConfig().
                                            getString("messages.player-not-found"))
                            );
                        }
                    } else if (args[1].equalsIgnoreCase("reset")) {
                        if (!sender.hasPermission("dtcoions.reset")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    Main.getConfigManager().getMainConfig().
                                            getString("messages.no-perm"))
                            );
                            return true;
                        }
                        if (offlinePlayer == null) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    Main.getConfigManager().getMainConfig().
                                            getString("messages.player-not-found"))
                            );
                            return true;
                        }
                        try {
                            String msgFrom = Main.getConfigManager().getMainConfig().getString("messages.reset.from.message", "");

                            String word = Main.getConfigManager().getMainConfig().getString("messages.reset.currency.word");
                            String a = Main.getConfigManager().getMainConfig().getString("messages.reset.currency.a");
                            String b = Main.getConfigManager().getMainConfig().getString("messages.reset.currency.b");
                            String c = Main.getConfigManager().getMainConfig().getString("messages.reset.currency.c");

                            msgFrom = ChatColor.translateAlternateColorCodes(
                                    '&',
                                    msgFrom.replaceAll("%player%", offlinePlayer.getName())
                            );
                            String msgTo = Main.getConfigManager().getMainConfig().getString("messages.reset.to.message", "");


                            msgTo = ChatColor.translateAlternateColorCodes('&',
                                    msgTo.replaceAll("%player%", sender.getName())
                            );
                            DTCoinsAPI.resetCoins(player);
                            sender.sendMessage(msgFrom);
                            if (player != null)
                                player.sendMessage(msgTo);
                        } catch (IllegalArgumentException e) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    Main.getConfigManager().getMainConfig().
                                            getString("messages.player-not-found"))
                            );
                        }
                    } else if (args[1].equalsIgnoreCase("take")) {
                        if (!sender.hasPermission("dtcoions.take")) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    Main.getConfigManager().getMainConfig().
                                            getString("messages.no-perm"))
                            );
                            return true;
                        }
                        if (offlinePlayer == null) {

                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    Main.getConfigManager().getMainConfig().
                                            getString("messages.player-not-found"))
                            );
                            return true;
                        }
                        try {
                            double count = Integer.parseInt(args[2]);
                            count = DTCoinsAPI.takeCoins(offlinePlayer, count);

                            String msgFrom = Main.getConfigManager().getMainConfig().getString("messages.take.from.message", "");

                            String word = Main.getConfigManager().getMainConfig().getString("messages.take.currency.word");
                            String a = Main.getConfigManager().getMainConfig().getString("messages.take.currency.a");
                            String b = Main.getConfigManager().getMainConfig().getString("messages.take.currency.b");
                            String c = Main.getConfigManager().getMainConfig().getString("messages.take.currency.c");

                            msgFrom = ChatColor.translateAlternateColorCodes(
                                    '&',
                                    msgFrom.replaceAll("%coins%", count + "")
                                            .replaceAll("%player%", offlinePlayer.getName())
                                            .replaceAll("%curr_name%", DTCoinsAPI.padezh(word, a, b, c, count))
                            );
                            String msgTo = Main.getConfigManager().getMainConfig().getString("messages.take.to.message", "");


                            msgTo = ChatColor.translateAlternateColorCodes('&',
                                    msgTo.replaceAll("%coins%", count + "")
                                            .replaceAll("%player%", sender.getName())
                                            .replaceAll("%curr_name%", DTCoinsAPI.padezh(word, a, b, c, count))
                            );
                            sender.sendMessage(msgFrom);
                            if (player != null)
                                player.sendMessage(msgTo);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    Main.getConfigManager().getMainConfig().
                                            getString("messages.value-error"))
                            );
                        } catch (IllegalArgumentException e) {
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                                    Main.getConfigManager().getMainConfig().
                                            getString("messages.player-not-found"))
                            );
                        }
                    } else {
                        sendHelp(sender);
                    }
                }
                catch (ArrayIndexOutOfBoundsException e)
                {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            Main.getConfigManager().getMainConfig().
                                    getString("messages.wrong-args"))
                    );
                }
            }
        }

        return true;
    }

    private static boolean isPlayer(CommandSender sender)
    {
        if (sender instanceof Player)
        {
            return true;
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                Main.getConfigManager().getMainConfig().
                getString("messages.not-a-player"))
        );
        return false;
    }

    private static void sendHelp(CommandSender sender)
    {
        if (sender.hasPermission("dtcoins.get.other")){
            sender.sendMessage("§e/money §7[ник] §8- узнать количество коинов.");
        }
        else if (sender.hasPermission("dtcoins.get")) {
            sender.sendMessage("§e/money §8- узнать количество коинов.");
        }
        if (sender.hasPermission("dtcoions.set")) {
            sender.sendMessage("§e/money §7<ник> §eset §7<количество> §8- установить количество коинов игроку.");
        }
        if (sender.hasPermission("dtcoions.add")) {
            sender.sendMessage("§e/money §7<ник> §eadd <количество> §7[множитель] §8- выдать коины игроку.");
        }
        if (sender.hasPermission("dtcoions.reset")) {
            sender.sendMessage("§e/money §7<ник> §ereset §8- сбросить коины у игрока.");
        }
        if (sender.hasPermission("dtcoions.take")) {
            sender.sendMessage("§e/money §7<ник> §etake §7<количество> §8- забрать коины у игрока.");
        }
        if (sender.hasPermission("dtcoions.admin")) {
            sender.sendMessage("§с/money reload §8- перезагрузить плагин (перезагрузка конфига, переподключение к бд и т.д.");
        }
    }
}
