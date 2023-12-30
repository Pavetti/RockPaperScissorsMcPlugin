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

public class RpsGameManager {
    private static RpsGameManager instance;
    private final Set<RpsGame> activeGames = new HashSet<>();

    private final WaitingRoomManager waitingRoomManager;
    private final Settings settings = Settings.getInstance();
    private final GameGUI gameUI;
    private final Economy economy;

    private RpsGameManager(){
        gameUI = new GameGUI();
        economy = Main.getInstance().getEconomy();
        waitingRoomManager = Main.getInstance().getWaitingRoomManager();
    }
    public static RpsGameManager getInstance(){
        if(instance == null){
            instance = new RpsGameManager();
        }
        return instance;
    }


    public void registerGame(RpsGame rpsGame){
        activeGames.add(rpsGame);
    }

    public void deregisterGame(RpsGame rpsGame){
        activeGames.remove(rpsGame);
    }

    private void deregisterAllGamesWithPlayersOf(RpsGame rpsGame){
        RpsPlayer opponent = rpsGame.getOpponent();
        RpsPlayer initiator = rpsGame.getInitiator();

        Iterator<RpsGame> iterator = activeGames.iterator();
        while (iterator.hasNext()){
            RpsGame game = iterator.next();
            if(game != rpsGame){
                if(PlayerUtil.compare(game.getInitiator().getPlayer(), initiator.getPlayer())){
                    iterator.remove();
                } else if (PlayerUtil.compare(game.getInitiator().getPlayer(), opponent.getPlayer())) {
                    iterator.remove();
                }else if (PlayerUtil.compare(game.getOpponent().getPlayer(), initiator.getPlayer())) {
                    iterator.remove();
                }else if (PlayerUtil.compare(game.getOpponent().getPlayer(), opponent.getPlayer())) {
                    iterator.remove();
                }
            }
        }
    }

    public void startGame(RpsGame rpsGame) {
        if(activeGames.contains(rpsGame)){

            rpsGame.getOpponent().getPlayer().openInventory(gameUI.getMainInventory());
            rpsGame.getInitiator().getPlayer().openInventory(gameUI.getMainInventory());
            rpsGame.start();

            //deregister games where is opponent or initiator except this game
            deregisterAllGamesWithPlayersOf(rpsGame);
        }
    }

    public void startTimeToEnd(RpsGame rpsGame){
        waitingRoomManager.getRpsChooseWR().addWaiter(rpsGame);
        //TODO add timer
    }

    public void endGame(RpsGame rpsGame){
        if(activeGames.contains(rpsGame)){
            RpsPlayer winner = getWinner(rpsGame);
            if(winner != null){
                RpsPlayer losser = rpsGame.getOtherPlayer(winner);
                int bet = winner.getRpsGame().getBet();
                settleBet(winner,losser,bet);
            }else {
                doDraw(rpsGame);
            }
        }
        activeGames.remove(rpsGame);
        waitingRoomManager.getRpsChooseWR().removeWaiter(rpsGame);
    }

    public void endGameByPlayerLeave(RpsPlayer loser){
        RpsGame rpsGame = loser.getRpsGame();
        int bet = rpsGame.getBet();
        RpsPlayer winner = rpsGame.getOtherPlayer(loser);

        activeGames.remove(rpsGame);
        winner.getPlayer().closeInventory();
        settleBet(winner,loser,bet);
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

    private void settleBet(RpsPlayer winner, RpsPlayer loser, int bet){
        Player winnerPlayer = winner.getPlayer();
        Player losserPlayer = loser.getPlayer();

        economy.withdrawPlayer(losserPlayer,bet);
        economy.depositPlayer(winnerPlayer,bet);

        PlayerUtil.sendMessagePrefixed(winnerPlayer,settings.getWinMessage().replace("{BET}", String.valueOf(bet)));
        PlayerUtil.sendMessagePrefixed(losserPlayer,settings.getLoseMessage().replace("{BET}", String.valueOf(bet)));
    }

    private void doDraw(RpsGame rpsGame){
        PlayerUtil.sendMessagePrefixed(rpsGame.getInitiator().getPlayer(),settings.getDrawMessage());
        PlayerUtil.sendMessagePrefixed(rpsGame.getOpponent().getPlayer(), settings.getDrawMessage());
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

    public List<RpsGame> getRpsGamesWhere(Player player){
        List<RpsGame> rpsGames = new ArrayList<>();
        for (RpsGame game : activeGames) {
            if(PlayerUtil.compare(game.getInitiator().getPlayer(),player)){
                rpsGames.add(game);
            } else if (PlayerUtil.compare(game.getOpponent().getPlayer(),player)) {
                rpsGames.add(game);
            }
        }
        return rpsGames;
    }

}
