package pl.pavetti.rockpaperscissors.game;

import pl.pavetti.rockpaperscissors.config.Settings;
import pl.pavetti.rockpaperscissors.game.model.RpsInvitation;

import java.time.LocalDateTime;
import java.util.*;

public class RequestManager {

    private final Map<UUID, Set<RpsInvitation>> invitations = new HashMap<>();

    public void addInvitation(RpsInvitation invitation){
        if(!invitations.containsKey(invitation.getInvitee().getUniqueId())){
            Set<RpsInvitation> invitationsSet = new HashSet<>();
            invitationsSet.add(invitation);
            invitations.put(invitation.getInvitee().getUniqueId(), invitationsSet);
        }else {
            invitations.get(invitation.getInvitee().getUniqueId()).add(invitation);
        }

    }

    public Set<RpsInvitation> getInvitations(UUID inviteeUUID){
        if(invitations.get(inviteeUUID) == null)
            return new HashSet<>();
        return invitations.get(inviteeUUID);
    }

    public void removeInvitation(RpsInvitation invitation){
        if(invitations.containsKey(invitation.getInvitee().getUniqueId()))
            invitations.get(invitation.getInvitee().getUniqueId()).remove(invitation);
    }

    public boolean isInvitationValid(RpsInvitation invitation){
        LocalDateTime localDateTime = LocalDateTime.now();
        return invitation.getCreationTime().plusSeconds(Settings.getInstance().getAcceptTime()).isAfter(localDateTime);
    }

    public void clearInvalidInvitationsOf(UUID inviteeUUID){
        Set<RpsInvitation> invitationsSet = invitations.get(inviteeUUID);
        if(invitationsSet == null)
            return;
        invitationsSet.removeIf(invitation -> !isInvitationValid(invitation));
    }


}
