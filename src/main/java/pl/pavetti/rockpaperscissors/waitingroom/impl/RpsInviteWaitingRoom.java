package pl.pavetti.rockpaperscissors.waitingroom.impl;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.pavetti.rockpaperscissors.Main;
import pl.pavetti.rockpaperscissors.config.Settings;
import pl.pavetti.rockpaperscissors.game.RpsGameManager;
import pl.pavetti.rockpaperscissors.game.model.RpsPlayer;
import pl.pavetti.rockpaperscissors.util.PlayerUtil;
import pl.pavetti.rockpaperscissors.waitingroom.model.Waiter;
import pl.pavetti.rockpaperscissors.waitingroom.model.WaitingRoom;

import java.util.List;
import java.util.stream.Collectors;

public class RpsInviteWaitingRoom extends WaitingRoom {
    private final int duration = Settings.getInstance().getAcceptTime();

    @Override
    public void addWaiter(Waiter waiter) {
        waiters.add(waiter);

        new BukkitRunnable() {
            @Override
            public void run() {
                if(waiters.contains(waiter)){
                    removeWaiter(waiter);
                    RpsGameManager.getInstance().deregisterGame(((RpsPlayer) waiter.getInstance()).getRpsGame());
                }
            }
        }.runTaskLater(Main.getInstance(),duration*20L);
    }


    @Override
    public List<Waiter> getWaiterListOfPlayer(Player player) {
        return waiters.stream()
                .filter(waiter -> PlayerUtil.compare(getPlayer(waiter),player))
                .collect(Collectors.toList());
    }


    private Player getPlayer(Waiter waiter){
        return ((RpsPlayer) waiter.getInstance()).getPlayer();
    }
}