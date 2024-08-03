package pl.pavetti.rockpaperscissors.waitingroom.impl;

import org.bukkit.scheduler.BukkitRunnable;
import pl.pavetti.rockpaperscissors.Main;
import pl.pavetti.rockpaperscissors.config.Settings;
import pl.pavetti.rockpaperscissors.game.RpsGame;
import pl.pavetti.rockpaperscissors.game.model.RpsPlayer;
import pl.pavetti.rockpaperscissors.waitingroom.model.Waiter;
import pl.pavetti.rockpaperscissors.waitingroom.model.WaitingRoom;


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
                    RpsPlayer losser;
                    //founds player who didnt choose
                    if(rpsGame.getInitiator().getChoice() == null){
                        losser = rpsGame.getInitiator();
                    }else {
                        losser = rpsGame.getOpponent();
                    }
                    losser.getPlayer().closeInventory();
                }
            }
        }.runTaskLater(Main.getInstance(),duration*20L);
    }
}