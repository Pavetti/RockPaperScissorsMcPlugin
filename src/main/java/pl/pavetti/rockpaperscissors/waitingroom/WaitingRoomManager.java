package pl.pavetti.rockpaperscissors.waitingroom;

import lombok.Getter;
import pl.pavetti.rockpaperscissors.waitingroom.impl.RpsChooseWaitingRoom;
import pl.pavetti.rockpaperscissors.waitingroom.impl.RpsInviteWaitingRoom;
import pl.pavetti.rockpaperscissors.waitingroom.model.WaitingRoom;

@Getter
public class WaitingRoomManager {
    private final WaitingRoom rpsInviteWR = new RpsInviteWaitingRoom();
    private final WaitingRoom rpsChooseWR = new RpsChooseWaitingRoom();
}
