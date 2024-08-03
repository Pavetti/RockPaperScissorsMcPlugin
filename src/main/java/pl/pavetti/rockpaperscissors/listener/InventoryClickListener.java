package pl.pavetti.rockpaperscissors.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import pl.pavetti.rockpaperscissors.game.RpsGameManager;
import pl.pavetti.rockpaperscissors.game.model.Choice;
import pl.pavetti.rockpaperscissors.game.model.RpsPlayer;
import pl.pavetti.rockpaperscissors.inventoryholder.RpsMenuInventoryHolder;

import java.util.Optional;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){

        if(!(event.getWhoClicked() instanceof Player)){
            return;
        }
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        if (inventory == null) {
            return;
        }
        if (inventory.getHolder() instanceof RpsMenuInventoryHolder) {
            //Block hand swap and clicking beyond inventory
            if (event.getHotbarButton() != player.getInventory().getHeldItemSlot()) {
                event.setCancelled(true);
            }
            //Check if player is rspPlayer (should be always)
            RpsGameManager rpsGameManager = RpsGameManager.getInstance();
            Optional<RpsPlayer> rpsPlayerOptional = rpsGameManager.getRpsPlayer(player);
            if(rpsPlayerOptional.isPresent()){
                RpsPlayer rpsPlayer = rpsPlayerOptional.get();
                // If player has already chosen, cancel the event
                if(rpsPlayer.getChoice() != null){
                    event.setCancelled(true);
                    return;
                }
                switch (event.getSlot()) {
                    case 11: // rock
                        rpsPlayer.choose(Choice.ROCK);
                        break;
                    case 13: // paper
                        rpsPlayer.choose(Choice.PAPER);
                        break;
                    case 15: // scissors
                        rpsPlayer.choose(Choice.SCISSORS);
                        break;
                }

            }
            event.setCancelled(true);
        }
    }
}
