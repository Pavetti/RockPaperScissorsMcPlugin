package pl.pavetti.rockpaperscissors.waitingroom.model;

import org.bukkit.entity.Player;


import java.util.ArrayList;
import java.util.List;

public abstract class WaitingRoom {
    public List<Waiter> waiters = new ArrayList<>();

    public abstract void addWaiter(Waiter waiter);
    public void removeWaiter(Waiter waiter){
        waiters.remove(waiter);
    }

    public List<Waiter> getWaiterListOfPlayer(Player player){
        throw new UnsupportedOperationException();
    }
}