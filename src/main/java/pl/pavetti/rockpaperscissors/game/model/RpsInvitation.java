package pl.pavetti.rockpaperscissors.game.model;

import lombok.Builder;
import lombok.Data;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
@Data
@Builder
public class RpsInvitation {
    private final Player initiator;
    private final Player invitee;
    private final LocalDateTime creationTime;
    private final double bet;
}
