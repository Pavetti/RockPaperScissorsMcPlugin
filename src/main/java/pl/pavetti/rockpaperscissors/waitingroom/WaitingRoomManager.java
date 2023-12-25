package pl.pavetti.rockpaperscissors.waitingroom;

import lombok.Getter;
import pl.pavetti.rockpaperscissors.waitingroom.impl.RpsInviteWaitingRoom;
import pl.pavetti.rockpaperscissors.waitingroom.model.WaitingRoom;

@Getter
public class WaitingRoomManager {
    private WaitingRoom rpsInviteWR = new RpsInviteWaitingRoom();
}
