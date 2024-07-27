package pl.pavetti.rockpaperscissors.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.pavetti.rockpaperscissors.game.RpsGameManager;
import pl.pavetti.rockpaperscissors.game.model.RpsGame;
import pl.pavetti.rockpaperscissors.waitingroom.WaitingRoomManager;

import java.util.List;

public class PlayerLeaveListener implements Listener {
    private final RpsGameManager rpsGameManager = RpsGameManager.getInstance();
    private final WaitingRoomManager waitingRoomManager;

    public PlayerLeaveListener(WaitingRoomManager waitingRoomManager) {
        this.waitingRoomManager = waitingRoomManager;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();

        List<RpsGame> rpsPlayerList = rpsGameManager.getAllGamesWherePlayerPerform(player);
        for (RpsGame rpsGame : rpsPlayerList) {
            rpsGameManager.deregisterGame(rpsGame);
            waitingRoomManager.getRpsInviteWR().removeWaiter(rpsGame.getOpponent());
        }
    }
}