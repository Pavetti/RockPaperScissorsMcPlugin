package pl.pavetti.rockpaperscissors.waitingroom.impl;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.pavetti.rockpaperscissors.Main;
import pl.pavetti.rockpaperscissors.config.Settings;
import pl.pavetti.rockpaperscissors.game.RpsGameManager;
import pl.pavetti.rockpaperscissors.game.model.RpsGame;
import pl.pavetti.rockpaperscissors.waitingroom.model.Waiter;
import pl.pavetti.rockpaperscissors.waitingroom.model.WaitingRoom;

import java.util.List;

public class RpsChooseWaitingRoom extends WaitingRoom {
    private final int duration = Settings.getInstance().getChooseTime();
    @Override
    public void addWaiter(Waiter waiter) {
        waiters.add(waiter);

        RpsGame rpsGame = (RpsGame) waiter.getInstance();

        new BukkitRunnable() {
            @Override
            public void run() {
                if(waiters.contains(waiter)) {
                    removeWaiter(waiter);
                    RpsGameManager.getInstance().endGameByTimesUp(rpsGame);
                }
            }
        }.runTaskLater(Main.getInstance(),duration*20L);
    }


    @Override
    public List<Waiter> getWaiterList(Player player) {
        throw new UnsupportedOperationException();
    }
}
