package pl.pavetti.rockpaperscissors.game;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import pl.pavetti.rockpaperscissors.Main;
import pl.pavetti.rockpaperscissors.config.Settings;
import pl.pavetti.rockpaperscissors.game.model.Choice;
import pl.pavetti.rockpaperscissors.game.model.RpsGame;
import pl.pavetti.rockpaperscissors.game.model.RpsPlayer;
import pl.pavetti.rockpaperscissors.util.PlayerUtil;
import pl.pavetti.rockpaperscissors.waitingroom.WaitingRoomManager;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class RpsGameManager {
    private static RpsGameManager instance;
    private final Set<RpsGame> activeGames = new HashSet<>();
    private final GameUI gameUI;
    private final Economy economy;
    private final WaitingRoomManager waitingRoomManager;
    private final Settings settings = Settings.getInstance();

    private RpsGameManager(){
        gameUI = new GameUI();
        economy = Main.getInstance().getEconomy();
        waitingRoomManager = Main.getInstance().getWaitingRoomManager();
    }
    public static RpsGameManager getInstance(){
        if(instance == null){
            instance = new RpsGameManager();
        }
        return instance;
    }

    public void registryGame(RpsGame rpsGame){
        activeGames.add(rpsGame);
    }

    public Optional<RpsPlayer> getRpsPlayer(Player player){
        for (RpsGame activeGame : activeGames) {
            if(PlayerUtil.compare(activeGame.getInitiator().getPlayer(),player)){
                return Optional.of(activeGame.getInitiator());
            } else if (PlayerUtil.compare(activeGame.getOpponent().getPlayer(),player)) {
                return Optional.of(activeGame.getOpponent());
            }
        }
        return Optional.empty();
    }

    public void startGame(RpsGame rpsGame) {
        if(activeGames.contains(rpsGame)){
            rpsGame.getOpponent().getPlayer().openInventory(gameUI.getMainInventory());
            rpsGame.getInitiator().getPlayer().openInventory(gameUI.getMainInventory());
        }
    }
    public void startTimeToEnd(RpsGame rpsGame){
        waitingRoomManager.getRpsChooseWR().addWaiter(rpsGame);
    }

    public void endGame(RpsGame rpsGame){
        if(activeGames.contains(rpsGame)){
            RpsPlayer winner = getWinner(rpsGame);
            if(winner != null){
                settleBet(winner);
            }else {
                doDraw(rpsGame);
            }
        }
        activeGames.remove(rpsGame);
        waitingRoomManager.getRpsChooseWR().removeWaiter(rpsGame);
    }

    public void endGameByTimesUp(RpsGame rpsGame){
        RpsPlayer winner;
        RpsPlayer losser;
        int bet = rpsGame.getBet();
        //founds player who didnt choose
        if(rpsGame.getInitiator().getChoice() == null){
            losser = rpsGame.getInitiator();
            winner = rpsGame.getOpponent();
        }else {
            losser = rpsGame.getOpponent();
            winner = rpsGame.getInitiator();
        }
        losser.getPlayer().closeInventory();
        settleBet(winner,losser,bet);
    }

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

    private void settleBet(RpsPlayer winner){

        RpsPlayer losser;
        RpsGame rpsGame = winner.getRpsGame();
        int bet = winner.getRpsGame().getBet();

        if(PlayerUtil.compare(rpsGame.getInitiator().getPlayer(),winner.getPlayer())){
            losser = rpsGame.getOpponent();
        }else {
            losser = rpsGame.getInitiator();
        }

        settleBet(winner,losser,bet);
    }

    private void settleBet(RpsPlayer winner, RpsPlayer loser, int bet){
        Player winnerPlayer = winner.getPlayer();
        Player losserPlayer = loser.getPlayer();

        economy.withdrawPlayer(losserPlayer,bet);
        economy.depositPlayer(winnerPlayer,bet);

        PlayerUtil.sendMessagePrefixed(winnerPlayer,settings.getWinMessage());
        PlayerUtil.sendMessagePrefixed(losserPlayer,settings.getLoseMessage());
    }

    private void doDraw(RpsGame rpsGame){
        //TODO
        PlayerUtil.sendMessagePrefixed(rpsGame.getInitiator().getPlayer(),settings.getDrawMessage());
        PlayerUtil.sendMessagePrefixed(rpsGame.getOpponent().getPlayer(), settings.getDrawMessage());
    }
}
