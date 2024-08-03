package pl.pavetti.rockpaperscissors.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.pavetti.rockpaperscissors.game.RpsGameManager;
import pl.pavetti.rockpaperscissors.game.RpsGame;
import pl.pavetti.rockpaperscissors.waitingroom.WaitingRoomManager;

import java.util.List;

public class PlayerLeaveListener implements Listener {


    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();


    }
}