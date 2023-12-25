package pl.pavetti.rockpaperscissors.waitingroom.model;

import org.bukkit.entity.Player;


import java.util.ArrayList;
import java.util.List;

public abstract class WaitingRoom {
    protected List<Waiter> waiters = new ArrayList<>();

    public abstract void addWaiter(Waiter waiter);
    public abstract boolean isPlayerWaiter(Player player);
    public abstract List<Waiter> getWaiterList(Player player);

    public void removeWaiter(Waiter waiter){
        waiters.remove(waiter);
    }
}
