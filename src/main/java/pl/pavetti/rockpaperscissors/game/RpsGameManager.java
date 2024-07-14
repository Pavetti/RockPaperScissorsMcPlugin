package pl.pavetti.rockpaperscissors.game;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.pavetti.rockpaperscissors.Main;
import pl.pavetti.rockpaperscissors.config.Settings;
import pl.pavetti.rockpaperscissors.game.model.Choice;
import pl.pavetti.rockpaperscissors.game.model.RpsGame;
import pl.pavetti.rockpaperscissors.game.model.RpsPlayer;
import pl.pavetti.rockpaperscissors.util.PlayerUtil;
import pl.pavetti.rockpaperscissors.waitingroom.WaitingRoomManager;

import java.util.*;

/**
 * Singleton class that manages Rock-Paper-Scissors games.
 */
public class RpsGameManager {
    private static RpsGameManager instance;
    private final Set<RpsGame> activeGames = new HashSet<>();

    private final WaitingRoomManager waitingRoomManager;
    private final Settings settings = Settings.getInstance();
    private final GameGUI gameGUI;
    private final Economy economy;

    /**
     * Private constructor for the singleton class.
     */
    private RpsGameManager(){
        gameGUI = new GameGUI();
        economy = Main.getInstance().getEconomy();
        waitingRoomManager = Main.getInstance().getWaitingRoomManager();
    }

    /**
     * Returns the singleton instance of the RpsGameManager.
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
     *
     * @param rpsGame the game to register
     */
    public void registerGame(RpsGame rpsGame){
        activeGames.add(rpsGame);
    }

    /**
     * Deregisters a game.
     *
     * @param rpsGame the game to deregister
     */
    public void deregisterGame(RpsGame rpsGame){
        activeGames.remove(rpsGame);
    }

    /**
     * Deregisters all other games with the players of the given game.
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
     *
     * @param rpsGame the game to end
     */
    public void startTimeToEnd(RpsGame rpsGame){
        waitingRoomManager.getRpsChooseWR().addWaiter(rpsGame);
        //TODO add timer
    }

    /**
     * Ends a game.
     *
     * @param rpsGame the game to end
     */
    public void endGame(RpsGame rpsGame){
        if(activeGames.contains(rpsGame)){
            RpsPlayer winner = getWinner(rpsGame);
            if(winner != null){
                RpsPlayer losser = rpsGame.getOtherPlayer(winner);
                settleBet(winner,losser,rpsGame.getBet());
            }else {
                doDraw(rpsGame);
            }
        }
        activeGames.remove(rpsGame);
        waitingRoomManager.getRpsChooseWR().removeWaiter(rpsGame);
    }

    /**
     * Ends a game due to a player leaving.
     *
     * @param loser the player who left the game
     */
    public void endGameByPlayerLeave(RpsPlayer loser){
        RpsGame rpsGame = loser.getRpsGame();
        RpsPlayer winner = rpsGame.getOtherPlayer(loser);

        activeGames.remove(rpsGame);
        winner.getPlayer().closeInventory();
        settleBet(winner,loser,rpsGame.getBet());
    }

    /**
     * Determines the winner of a game.
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
     * Settles the bet between the winner and loser of a game.
     *
     * @param winner the winning player
     * @param loser the losing player
     * @param bet the amount of the bet
     */
    private void settleBet(RpsPlayer winner, RpsPlayer loser, double bet){
        Player winnerPlayer = winner.getPlayer();
        Player losserPlayer = loser.getPlayer();

        economy.withdrawPlayer(losserPlayer,bet);
        economy.depositPlayer(winnerPlayer,bet);

        PlayerUtil.sendMessagePrefixed(winnerPlayer,settings.getWinMessage().replace("{BET}", String.valueOf(bet)));
        PlayerUtil.sendMessagePrefixed(losserPlayer,settings.getLoseMessage().replace("{BET}", String.valueOf(bet)));
    }

    /**
     * Handles a draw in a game.
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
     */
    public void reloadResources(){
        gameGUI.reload();
    }

}
