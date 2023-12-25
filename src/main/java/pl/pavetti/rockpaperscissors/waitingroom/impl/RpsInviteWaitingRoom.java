package pl.pavetti.rockpaperscissors.waitingroom.impl;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.pavetti.rockpaperscissors.Main;
import pl.pavetti.rockpaperscissors.config.Settings;
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

        Player player = waiter.getPlayer();

        new BukkitRunnable() {
            @Override
            public void run() {
                if(isPlayerWaiter(player)){
                    waiters.remove(waiter);
                }
            }
        }.runTaskLater(Main.getInstance(),duration*20L);
    }

    @Override
    public boolean isPlayerWaiter(Player player) {
        return waiters.stream()
                .map(Waiter::getPlayer)
                .anyMatch(p -> PlayerUtil.compare(p,player));
    }

    @Override
    public List<Waiter> getWaiterList(Player player) {
        return waiters.stream()
                .filter(waiter -> PlayerUtil.compare(waiter.getPlayer(),player))
                .collect(Collectors.toList());
    }
}
