package su.dreamtime.dreamiki.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import su.dreamtime.dreamiki.DTCoinsAPI;
import su.dreamtime.dreamiki.data.PurchasePlayer;

import java.util.List;

public class CartCommand implements CommandExecutor {

    private boolean hasSlots(Player p) {
        for(ItemStack item : p.getInventory().getContents()) {
            if(item == null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return false;
        }

        Player p = (Player)sender;
        List<PurchasePlayer> list = DTCoinsAPI.getPurchases(p.getName());

        int slots = 0;
        int given = 0;
        int already = 0;
        for(PurchasePlayer pp : list) {
            if(pp.item) {
                if(!hasSlots(p)) {
                    slots++;
                } else {
                    if(pp.given) {
                        already++;
                    } else {
                        given++;
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), pp.command);
                        DTCoinsAPI.setGiven(p.getName(), pp.command, true);
                    }
                }
            }
        }

        p.sendMessage("§2Успех! §aВы получили §2"+given+" §aпредметов!");
        if(slots > 0) {
            p.sendMessage("§cУ Вас в инвентаре не хватило §4"+slots+" §cслотов!");
        }
        if(already > 0) {
            p.sendMessage("§4"+already+" §cпредметов было выдано Вам раньше!");
        }

        return true;
    }
}
