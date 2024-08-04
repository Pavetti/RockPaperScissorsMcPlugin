package pl.pavetti.rockpaperscissors.game;

import pl.pavetti.rockpaperscissors.config.Settings;
import pl.pavetti.rockpaperscissors.game.model.RpsInvitation;

import java.time.LocalDateTime;
import java.util.*;

/**
 * This class manages the invitations for the Rock Paper Scissors game.
 * It handles adding, retrieving, removing, and validating invitations.
 */
public class RequestManager {

    // A map to store the invitations, with the invitee's UUID as the key and a set of invitations as the value
    private final Map<UUID, Set<RpsInvitation>> invitations = new HashMap<>();

    /**
     * Adds an invitation to the map.
     * @param invitation the invitation to be added
     */
    public void addInvitation(RpsInvitation invitation){
        if(!invitations.containsKey(invitation.getInvitee().getUniqueId())){
            Set<RpsInvitation> invitationsSet = new HashSet<>();
            invitationsSet.add(invitation);
            invitations.put(invitation.getInvitee().getUniqueId(), invitationsSet);
        }else {
            invitations.get(invitation.getInvitee().getUniqueId()).add(invitation);
        }
    }

    /**
     * Retrieves the invitations for a specific invitee.
     * @param inviteeUUID the UUID of the invitee
     * @return a set of invitations for the invitee
     */
    public Set<RpsInvitation> getInvitations(UUID inviteeUUID){
        if(invitations.get(inviteeUUID) == null)
            return new HashSet<>();
        return invitations.get(inviteeUUID);
    }

    /**
     * Removes an invitation from the map.
     * @param invitation the invitation to be removed
     */
    public void removeInvitation(RpsInvitation invitation){
        if(invitations.containsKey(invitation.getInvitee().getUniqueId()))
            invitations.get(invitation.getInvitee().getUniqueId()).remove(invitation);
    }

    /**
     * Checks if an invitation is still valid.
     * @param invitation the invitation to be checked
     * @return true if the invitation is still valid, false otherwise
     */
    public boolean isInvitationValid(RpsInvitation invitation){
        LocalDateTime localDateTime = LocalDateTime.now();
        return invitation.getCreationTime().plusSeconds(Settings.getInstance().getAcceptTime()).isAfter(localDateTime);
    }

    /**
     * Removes all invalid invitations for a specific invitee.
     * @param inviteeUUID the UUID of the invitee
     */
    public void clearInvalidInvitationsOf(UUID inviteeUUID){
        Set<RpsInvitation> invitationsSet = invitations.get(inviteeUUID);
        if(invitationsSet == null)
            return;
        invitationsSet.removeIf(invitation -> !isInvitationValid(invitation));
    }
}