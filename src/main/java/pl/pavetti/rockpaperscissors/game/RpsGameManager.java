package pl.pavetti.rockpaperscissors.game;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.pavetti.rockpaperscissors.Main;
import pl.pavetti.rockpaperscissors.config.Settings;
import pl.pavetti.rockpaperscissors.datatransporter.GameResult;
import pl.pavetti.rockpaperscissors.game.model.Choice;
import pl.pavetti.rockpaperscissors.game.model.RpsPlayer;
import pl.pavetti.rockpaperscissors.util.ChatUtil;
import pl.pavetti.rockpaperscissors.util.PlayerUtil;
import pl.pavetti.rockpaperscissors.util.ServerUtil;
import pl.pavetti.rockpaperscissors.waitingroom.WaitingRoomManager;

import java.util.*;

/**
 * This class manages the Rock Paper Scissors game.
 * It handles game registration, invitation blocking, game start, game end, and game replay.
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
     * Private constructor for singleton pattern.
     */
    private RpsGameManager(){
        gameGUI = new GameGUI();
        economy = Main.getInstance().getEconomy();
        waitingRoomManager = Main.getInstance().getWaitingRoomManager();
    }

    /**
     * Singleton pattern getter.
     * @return the single instance of RpsGameManager
     */
    public static RpsGameManager getInstance(){
        if(instance == null){
            instance = new RpsGameManager();
        }
        return instance;
    }

    /**
     * Registers a game to the active games set.
     * @param rpsGame the game to be registered
     */
    public void registerGame(RpsGame rpsGame){
        activeGames.add(rpsGame);
    }

    /**
     * Adds a player to the set of players with blocked invitations.
     * @param uuid the UUID of the player
     */
    private void addBlockingInvitationToPlayer(String uuid){
        playersWithBlockedInvitaionsSet.add(uuid);
    }

    /**
     * Removes a player from the set of players with blocked invitations.
     * @param uuid the UUID of the player
     */
    private void removeBlockingInvitationToPlayer(String uuid){
        playersWithBlockedInvitaionsSet.remove(uuid);
    }

    /**
     * Toggles the blocking of invitations for a player.
     * @param uuid the UUID of the player
     * @return true if the player is now blocking invitations, false otherwise
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
     * Checks if a player is blocking invitations.
     * @param uuid the UUID of the player
     * @return true if the player is blocking invitations, false otherwise
     */
    public boolean isPlayerBlockingInvitation(String uuid){
        return playersWithBlockedInvitaionsSet.contains(uuid);
    }

    /**
     * Starts a game.
     * @param rpsGame the game to be started
     * @param isReplay true if the game is a replay, false otherwise
     */
    public void startGame(RpsGame rpsGame, boolean isReplay){
        if(activeGames.contains(rpsGame)){
            // Deposit is made only on first game start (not on replay)
            if(!isReplay) makeGameDeposit(rpsGame);
            rpsGame.getOpponent().getPlayer().openInventory(gameGUI.getGameInventory());
            rpsGame.getInitiator().getPlayer().openInventory(gameGUI.getGameInventory());
        }
    }

    /**
     * Makes a deposit for a game.
     * @param rpsGame the game for which the deposit is made
     */
    private void makeGameDeposit(RpsGame rpsGame){
        double bet = rpsGame.getBet();
        Player initiator = rpsGame.getInitiator().getPlayer();
        Player opponent = rpsGame.getOpponent().getPlayer();
        economy.withdrawPlayer(initiator,bet);
        economy.withdrawPlayer(opponent,bet);

        PlayerUtil.sendPrefixedMessage(initiator,settings.getCollectedGameDeposit(),"{BET}", String.valueOf(bet));
        PlayerUtil.sendPrefixedMessage(opponent,settings.getCollectedGameDeposit(),"{BET}", String.valueOf(bet));
    }

    /**
     * Makes a deposit for a draw game.
     * @param rpsGame the game for which the deposit is made
     */
    private void makeDrawDepositReceive(RpsGame rpsGame){
        double bet = rpsGame.getBet();
        Player initiator = rpsGame.getInitiator().getPlayer();
        Player opponent = rpsGame.getOpponent().getPlayer();
        economy.depositPlayer(initiator,bet);
        economy.depositPlayer(opponent,bet);

        PlayerUtil.sendPrefixedMessage(initiator,settings.getDrawNormalMessage(),"{BET}", String.valueOf(bet));
        PlayerUtil.sendPrefixedMessage(opponent,settings.getDrawNormalMessage(),"{BET}", String.valueOf(bet));
    }

    /**
     * Displays time for second choice player.
     * @param rpsGame the game for which the time is displayed
     */
    public void displayTimeForSecondChoicePlayer(RpsGame rpsGame){
        waitingRoomManager.getRpsChooseWR().addWaiter(rpsGame);
        //TODO add timer
    }

    /**
     * Ends a game.
     * @param rpsGame the game to be ended
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
     * Ends a game by player leave.
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
     * Gets the winner of a game.
     * @param rpsGame the game for which the winner is determined
     * @return the winner of the game
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
     * Settles the bet for a game.
     * @param gameResult the result of the game for which the bet is settled
     */
    private void settleBet(GameResult gameResult){
        double bet = gameResult.getRpsGame().getBet();
        economy.depositPlayer(gameResult.getWinner().getPlayer(),2*bet);

        sendMessagesAfterWinGame(gameResult);
    }

    /**
     * Sends messages after a game win.
     * @param gameResult the result of the game for which the messages are sent
     */
    private void sendMessagesAfterWinGame(GameResult gameResult){
        Player winner = gameResult.getWinner().getPlayer();
        Player loser = gameResult.getLoser().getPlayer();
        double bet = gameResult.getRpsGame().getBet();

        PlayerUtil.sendPrefixedMessage(winner,settings.getWinMessage(),"{AMOUNT}", String.valueOf(2*bet));
        PlayerUtil.sendPrefixedMessage(loser,settings.getLoseMessage(),"{BET}", String.valueOf(bet));

        if(settings.isGlobalGameResultEnable()){
            if(bet >= settings.getGlobalGameResultMinBet()){
                List<String> lines = new ArrayList<>();
                for (String line : Settings.getInstance().getGlobalGameResultMessage()) {
                   lines.add(ChatUtil.replacePlaceholders(line,
                            "{WINNER}",winner.getName(),
                            "{LOSER}",loser.getName(),
                            "{BET}", String.valueOf(bet)));
                }
                ServerUtil.broadcastMessageList(lines);
            }
        }
    }

    /**
     * Handles a draw game.
     * @param rpsGame the game that ended in a draw
     */
    private void doDraw(RpsGame rpsGame){
        if(settings.isReplayOnDraw())
            doGameReplay(rpsGame);
        else
            makeDrawDepositReceive(rpsGame);
    }

    /**
     * Handles a game replay.
     * @param rpsGame the game to be replayed
     */
    private void doGameReplay(RpsGame rpsGame){
        PlayerUtil.sendPrefixedMessage(rpsGame.getInitiator().getPlayer(),settings.getDrawReplayMessage());
        PlayerUtil.sendPrefixedMessage(rpsGame.getOpponent().getPlayer(), settings.getDrawReplayMessage());

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            RpsGame newRpsGame = new RpsGame(rpsGame.getInitiator().getPlayer()
                    ,rpsGame.getOpponent().getPlayer(),
                    rpsGame.getBet());
            registerGame(newRpsGame);
            startGame(newRpsGame,true);
        }, 20L); //ticks
    }

    /**
     * Gets the RpsPlayer for a player.
     * @param player the player for which the RpsPlayer is determined
     * @return the RpsPlayer for the player
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
     * Checks if a player is in a game.
     * @param player the player to be checked
     * @return true if the player is in a game, false otherwise
     */
    public boolean isPlayerInGame(Player player){
        return getRpsPlayer(player).isPresent();
    }

    /**
     * Reloads resources.
     */
    public void reloadResources(){
        gameGUI.initialize();
    }

}