package pl.pavetti.rockpaperscissors.game;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.pavetti.rockpaperscissors.Main;
import pl.pavetti.rockpaperscissors.config.Settings;
import pl.pavetti.rockpaperscissors.datatransporter.GameResult;
import pl.pavetti.rockpaperscissors.game.model.Choice;
import pl.pavetti.rockpaperscissors.game.model.RpsGame;
import pl.pavetti.rockpaperscissors.game.model.RpsPlayer;
import pl.pavetti.rockpaperscissors.service.PlaceholderService;
import pl.pavetti.rockpaperscissors.util.PlayerUtil;
import pl.pavetti.rockpaperscissors.waitingroom.WaitingRoomManager;

import java.util.*;

/**
 * Singleton class that manages Rock-Paper-Scissors games.
 */
public class RpsGameManager {
    private static RpsGameManager instance;
    private final Set<RpsGame> activeGames = new HashSet<>();
    private final Set<String> playersWithBlockedInvitaionsSet = new HashSet<>();

    private final WaitingRoomManager waitingRoomManager;
    private final Settings settings = Settings.getInstance();
    private final GameGUI gameGUI;
    private final Economy economy;

    /**
     * Private constructor for the singleton class.
     * Initializes the game GUI, economy, and waiting room manager.
     */
    private RpsGameManager(){
        gameGUI = new GameGUI();
        economy = Main.getInstance().getEconomy();
        waitingRoomManager = Main.getInstance().getWaitingRoomManager();
    }

    /**
     * Returns the singleton instance of the RpsGameManager.
     * If the instance does not exist, it is created.
     *
     * @return the singleton instance of the RpsGameManager
     */
    public static RpsGameManager getInstance(){
        if(instance == null){
            instance = new RpsGameManager();
        }
        return instance;
    }

    /**
     * Registers a new game.
     * Adds the game to the set of active games.
     *
     * @param rpsGame the game to register
     */
    public void registerGame(RpsGame rpsGame){
        activeGames.add(rpsGame);
    }

    /**
     * Deregisters a game.
     * Removes the game from the set of active games.
     *
     * @param rpsGame the game to deregister
     */
    public void deregisterGame(RpsGame rpsGame){
        activeGames.remove(rpsGame);
    }

    /**
     * Adds a player to the set of players who have blocked invitations.
     *
     * @param uuid the unique identifier of the player to add
     */
    private void addBlockingInvitationToPlayer(String uuid){
        playersWithBlockedInvitaionsSet.add(uuid);
    }

    /**
     * Removes a player from the set of players who have blocked invitations.
     *
     * @param uuid the unique identifier of the player to remove
     */
    private void removeBlockingInvitationToPlayer(String uuid){
        playersWithBlockedInvitaionsSet.remove(uuid);
    }

    /**
     * Toggles the invitation blocking status of a player.
     * If the player has blocked invitations, this method will unblock them and return false.
     * If the player has not blocked invitations, this method will block them and return true.
     *
     * @param uuid the unique identifier of the player to toggle
     * @return true if the player has blocked invitations, false if they have unblocked
     */
    public boolean toggleBlockingInvitationToPlayer(String uuid){
        if(playersWithBlockedInvitaionsSet.contains(uuid)){
            removeBlockingInvitationToPlayer(uuid);
            return false;
        }else {
            addBlockingInvitationToPlayer(uuid);
            return true;
        }
    }

    /**
     * Checks if a player has blocked invitations.
     *
     * @param uuid the unique identifier of the player to check
     * @return true if the player has blocked invitations, false otherwise
     */
    public boolean isPlayerBlockingInvitation(String uuid){
        return playersWithBlockedInvitaionsSet.contains(uuid);
    }

    /**
     * Deregisters all other games with the players of the given game.
     * This method is used to ensure that a player is only in one game at a time.
     *
     * @param rpsGame the game to use for deregistration
     */
    private void deregisterAllOtherGamesWithPlayersOf(RpsGame rpsGame){
        Player properOpponent = rpsGame.getOpponent().getPlayer();
        Player properInitiator = rpsGame.getInitiator().getPlayer();

        Iterator<RpsGame> iterator = activeGames.iterator();
        while (iterator.hasNext()){
            RpsGame game = iterator.next();
            if(game != rpsGame){
                Player opponent = game.getOpponent().getPlayer();
                Player initiator = game.getInitiator().getPlayer();

                if(PlayerUtil.compare(initiator, properInitiator))
                    iterator.remove();
                else if (PlayerUtil.compare(initiator, properOpponent))
                    iterator.remove();
                else if (PlayerUtil.compare(opponent, properInitiator))
                    iterator.remove();
                else if (PlayerUtil.compare(opponent, properOpponent))
                    iterator.remove();
            }
        }
    }

    /**
     * Starts a game.
     * Deregisters all other games with the players of the given game, opens the game GUI for both players, and starts the game.
     *
     * @param rpsGame the game to start
     */
    public void startGame(RpsGame rpsGame) {
        if(activeGames.contains(rpsGame)){
            deregisterAllOtherGamesWithPlayersOf(rpsGame);
            rpsGame.getOpponent().getPlayer().openInventory(gameGUI.getMainInventory());
            rpsGame.getInitiator().getPlayer().openInventory(gameGUI.getMainInventory());
            rpsGame.start();
        }
    }

    /**
     * Starts a timer to end a game.
     * Adds the game to the waiting room and starts a timer.
     *
     * @param rpsGame the game to end
     */
    public void startTimeToEnd(RpsGame rpsGame){
        waitingRoomManager.getRpsChooseWR().addWaiter(rpsGame);
        //TODO add timer
    }

    /**
     * Ends a game.
     * Determines the winner of the game, settles the bet, and removes the game from the set of active games and the waiting room.
     *
     * @param rpsGame the game to end
     */
    public void endGame(RpsGame rpsGame){
        if(activeGames.contains(rpsGame)){
            RpsPlayer winner = getWinner(rpsGame);
            if(winner != null){
                RpsPlayer loser = rpsGame.getOtherPlayer(winner);
                GameResult gameResult = GameResult.builder()
                        .rpsGame(rpsGame)
                        .winner(winner)
                        .loser(loser)
                        .build();
                settleBet(gameResult);
            }else {
                doDraw(rpsGame);
            }
        }
        activeGames.remove(rpsGame);
        waitingRoomManager.getRpsChooseWR().removeWaiter(rpsGame);
    }

    /**
     * Ends a game due to a player leaving.
     * The player who left the game is considered the loser, and the bet is settled accordingly.
     *
     * @param loser the player who left the game
     */
    public void endGameByPlayerLeave(RpsPlayer loser){
        RpsGame rpsGame = loser.getRpsGame();
        RpsPlayer winner = rpsGame.getOtherPlayer(loser);
        GameResult gameResult = GameResult.builder()
                .rpsGame(rpsGame)
                .winner(winner)
                .loser(loser)
                .build();
        activeGames.remove(rpsGame);
        winner.getPlayer().closeInventory();
        settleBet(gameResult);
    }

    /**
     * Determines the winner of a game.
     * The winner is determined based on the choices of the players.
     *
     * @param rpsGame the game to determine the winner of
     * @return the winning player, or null if there is a draw
     */
    private RpsPlayer getWinner (RpsGame rpsGame){
        RpsPlayer player1 = rpsGame.getInitiator();
        RpsPlayer player2 = rpsGame.getOpponent();
        Choice choice1 = player1.getChoice();
        Choice choice2 = player2.getChoice();

        if(choice1 == Choice.ROCK && choice2 == Choice.PAPER) return player2;
        else if (choice1 == Choice.ROCK && choice2 == Choice.SCISSORS) return player1;
        else if (choice1 == Choice.PAPER && choice2 == Choice.ROCK) return player1;
        else if (choice1 == Choice.PAPER && choice2 == Choice.SCISSORS) return player2;
        else if (choice1 == Choice.SCISSORS && choice2 == Choice.ROCK) return player2;
        else if (choice1 == Choice.SCISSORS && choice2 == Choice.PAPER) return player1;

        return null;
    }

    /**
     * Settles the bet of a game.
     * Withdraws the bet from the loser and deposits it to the winner.
     * Sends messages to the players about the result of the game.
     *
     * @param gameResult the result of the game
     */
    private void settleBet(GameResult gameResult){
        economy.withdrawPlayer(gameResult.getWinner().getPlayer(),gameResult.getRpsGame().getBet());
        economy.depositPlayer(gameResult.getWinner().getPlayer(),gameResult.getRpsGame().getBet());

        sendMessagesAfterWinGame(gameResult);
    }

    /**
     * Sends messages to the players after a game has been won.
     * Sends messages to the winner and loser of the game, and, if the settings allow, sends a global message about the result of the game.
     *
     * @param gameResult the result of the game
     */
    private void sendMessagesAfterWinGame(GameResult gameResult){
        Player winner = gameResult.getWinner().getPlayer();
        Player loser = gameResult.getLoser().getPlayer();
        double bet = gameResult.getRpsGame().getBet();

        PlayerUtil.sendMessagePrefixed(winner,settings.getWinMessage().replace("{BET}", String.valueOf(bet)));
        PlayerUtil.sendMessagePrefixed(loser,settings.getLoseMessage().replace("{BET}", String.valueOf(bet)));

        System.out.println("0");
        if(settings.isGlobalGameResultEnable()){
            System.out.println("1");
            if(bet >= settings.getGlobalGameResultMinBet()){
                System.out.println("2");
                List<String> message = PlaceholderService.replacePlaceholdersInGlobalResultMessage(gameResult);
                message.forEach(Bukkit::broadcastMessage);
            }
        }
    }

    /**
     * Handles a draw in a game.
     * Sends a draw message to the players and, if the settings allow, starts a new game.
     *
     * @param rpsGame the game that ended in a draw
     */
    private void doDraw(RpsGame rpsGame){
        sendDrawMessage(rpsGame);
        if(settings.isReplayOnDraw()){

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                RpsGame newRpsGame = new RpsGame(rpsGame.getInitiator().getPlayer()
                        ,rpsGame.getOpponent().getPlayer(),
                        rpsGame.getBet());
                registerGame(newRpsGame);
                startGame(newRpsGame);
            }, 20L); //ticks
        }
    }

    /**
     * Sends a draw message to the players of a game.
     *
     * @param rpsGame the game that ended in a draw
     */
    private void sendDrawMessage(RpsGame rpsGame){
        PlayerUtil.sendMessagePrefixed(rpsGame.getInitiator().getPlayer(),settings.getDrawNormalMessage());
        PlayerUtil.sendMessagePrefixed(rpsGame.getOpponent().getPlayer(), settings.getDrawNormalMessage());
    }

    /**
     * Returns the RpsPlayer associated with a Player, if one exists.
     *
     * @param player the Player to get the RpsPlayer of
     * @return an Optional containing the RpsPlayer, or an empty Optional if none exists
     */
    public Optional<RpsPlayer> getRpsPlayer(Player player){
        //Waring! Use only if this player is one or none at all
        for (RpsGame activeGame : activeGames) {
            if(PlayerUtil.compare(activeGame.getInitiator().getPlayer(),player)){
                return Optional.of(activeGame.getInitiator());
            } else if (PlayerUtil.compare(activeGame.getOpponent().getPlayer(),player)) {
                return Optional.of(activeGame.getOpponent());
            }
        }
        return Optional.empty();
    }

    /**
     * Returns all games where a player is performing.
     *
     * @param player the player to get the games of
     * @return a list of games where the player is performing
     */
    public List<RpsGame> getAllGamesWherePlayerPerform(Player player){
        List<RpsGame> rpsGames = new ArrayList<>();
        for (RpsGame game : activeGames) {
            if(PlayerUtil.compare(game.getInitiator().getPlayer(),player))
                rpsGames.add(game);
            else if (PlayerUtil.compare(game.getOpponent().getPlayer(),player))
                rpsGames.add(game);
        }
        return rpsGames;
    }

    /**
     * Reloads resources.
     * Currently, this only reloads the game GUI.
     */
    public void reloadResources(){
        gameGUI.reload();
    }

}