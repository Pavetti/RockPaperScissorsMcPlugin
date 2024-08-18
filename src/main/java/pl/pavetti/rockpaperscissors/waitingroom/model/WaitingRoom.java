package pl.pavetti.rockpaperscissors.waitingroom.model;


import java.util.ArrayList;
import java.util.List;

public abstract class WaitingRoom {
    public List<Waiter> waiters = new ArrayList<>();

    public abstract void addWaiter(Waiter waiter);
    public void removeWaiter(Waiter waiter){
        waiters.remove(waiter);
    }
}