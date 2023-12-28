package pl.pavetti.rockpaperscissors.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import pl.pavetti.rockpaperscissors.game.RpsGameManager;
import pl.pavetti.rockpaperscissors.game.model.RpsPlayer;
import pl.pavetti.rockpaperscissors.inventoryholder.RpsMenuInventoryHolder;

import java.util.Optional;

public class InventoryCloseListener implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        if(!(event.getPlayer() instanceof Player)){
            return;
        }
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();
        if (inventory.getHolder() instanceof RpsMenuInventoryHolder) {
            RpsGameManager rpsGameManager = RpsGameManager.getInstance();
            Optional<RpsPlayer> rpsPlayerOptional = rpsGameManager.getRpsPlayer(player);

            if(rpsPlayerOptional.isPresent()){
                RpsPlayer rpsPlayer = rpsPlayerOptional.get();
                if(rpsPlayer.getChoice() == null){
                    rpsGameManager.endGameByPlayerLeave(rpsPlayer);
                }
            }
        }
    }
}
