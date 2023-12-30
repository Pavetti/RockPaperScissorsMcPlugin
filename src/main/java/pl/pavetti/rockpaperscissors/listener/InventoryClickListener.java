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
            RpsGameManager manager = RpsGameManager.getInstance();
            Optional<RpsPlayer> rpsPlayerOptional = manager.getRpsPlayer(player);
            if(rpsPlayerOptional.isPresent()){
                RpsPlayer rpsPlayer = rpsPlayerOptional.get();
                if(rpsPlayer.getChoice() != null){
                    event.setCancelled(true);
                    return;
                }
                int rockSlot = 11;
                int paperSlot = 13;
                int scissorsSlot = 15;

                if(event.getSlot() == rockSlot){
                    rpsPlayer.choose(Choice.ROCK);
                } else if (event.getSlot() == paperSlot) {
                    rpsPlayer.choose(Choice.PAPER);
                } else if (event.getSlot() == scissorsSlot) {
                    rpsPlayer.choose(Choice.SCISSORS);
                }
            }
            event.setCancelled(true);
        }
    }
}
