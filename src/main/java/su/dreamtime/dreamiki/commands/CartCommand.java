package su.dreamtime.dreamiki.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CartCommand implements CommandExecutor {

    private boolean hasSlots(Player p, int slots) {
        int svobodno = 0;
        for(ItemStack item : p.getInventory().getContents()) {
            if(item == null) {
                svobodno++;
            }
        }

        if(svobodno >= slots) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return false;
        }

        Player p = (Player)sender;



        return true;
    }
}
