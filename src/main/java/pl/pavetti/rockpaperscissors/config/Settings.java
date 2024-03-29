package pl.pavetti.rockpaperscissors.config;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import pl.pavetti.rockpaperscissors.Main;
import pl.pavetti.rockpaperscissors.util.ChatUtil;
@Getter
public class  Settings {

    private static Settings instance;

    //basic
    private String prefix;
    //gameConfig
    private boolean replayOnDraw;
    private double maxBet;
    private double minBet;
    private int acceptTime;
    private int chooseTime;
    //gui
    private String guiMainTitle;
    private String guiMainFillItemName;
    private Material guiMainFillItem;
    private String guiRockName;
    private Material guiRockItem;
    private String guiPaperName;
    private Material guiPaperItem;
    private String guiScissorsName;
    private Material guiScissorsItem;
    //messages
    private String noPermission;
    private String badUseRpsGameCmd;
    private String badUseRpsAcceptCmd;
    private String myselfInvite;
    private String notEnoughMoney;
    private String betOutOfRangeMax;
    private String betOutOfRangeMin;
    private String playerNotExist;
    private String rpsInvite;
    private String noInvitation;
    private String alreadyPlay;
    private String alreadyInvite;
    private String winMessage;
    private String loseMessage;
    private String drawNormalMessage;
    private String drawReplayMessage;
    private String successfullyChoice;
    private String successfullyInvite;
    private String successfullyPluginReload;
    //buttons
    private String rpsInviteAcceptButton;
    private String rpsInviteDenyButton;

    private Settings() {
        load();
    }

    public static Settings getInstance(){
        if(instance == null){
            instance = new Settings();
        }
        return instance;
    }

    public void load(){
        FileConfiguration configuration = Main.getInstance().getConfig();


        //basic
        prefix = ChatUtil.chatColor(configuration.getString("settings.prefix"));
        //gameConfig
        replayOnDraw = configuration.getBoolean("settings.gameConfig.replayOnDraw");
        maxBet = configuration.getDouble("settings.gameConfig.maxBet");
        minBet = configuration.getDouble("settings.gameConfig.minBet");
        acceptTime = configuration.getInt("settings.gameConfig.acceptTime");
        chooseTime = configuration.getInt("settings.gameConfig.chooseTime");
        //gui
        guiMainTitle = ChatUtil.chatColor(configuration.getString("settings.gui.main.title"));
        guiMainFillItem = getMaterialOf(configuration.getString("settings.gui.main.fillItem.item"));
        guiMainFillItemName = ChatUtil.chatColor(configuration.getString("settings.gui.main.fillItem.name"));
        guiRockItem = getMaterialOf(configuration.getString("settings.gui.main.rock.item"));
        guiRockName = ChatUtil.chatColor(configuration.getString("settings.gui.main.rock.name"));
        guiPaperItem = getMaterialOf(configuration.getString("settings.gui.main.paper.item"));
        guiPaperName = ChatUtil.chatColor(configuration.getString("settings.gui.main.paper.name"));
        guiScissorsItem = getMaterialOf(configuration.getString("settings.gui.main.scissors.item"));
        guiScissorsName = ChatUtil.chatColor(configuration.getString("settings.gui.main.scissors.name"));

        //messages
        noPermission = ChatUtil.chatColor(configuration.getString("settings.messages.noPermission"));
        badUseRpsGameCmd = ChatUtil.chatColor(configuration.getString("settings.messages.badUseRpsGameCmd"));
        badUseRpsAcceptCmd = ChatUtil.chatColor(configuration.getString("settings.messages.badUseRpsAcceptCmd"));
        myselfInvite = ChatUtil.chatColor(configuration.getString("settings.messages.myselfInvite"));
        notEnoughMoney = ChatUtil.chatColor(configuration.getString("settings.messages.notEnoughMoney"));
        betOutOfRangeMax = ChatUtil.chatColor(configuration.getString("settings.messages.betOutOfRangeMax"));
        betOutOfRangeMin = ChatUtil.chatColor(configuration.getString("settings.messages.betOutOfRangeMin"));
        playerNotExist = ChatUtil.chatColor(configuration.getString("settings.messages.playerNotExist"));
        rpsInvite = ChatUtil.chatColor(configuration.getString("settings.messages.rpsInvite"));
        noInvitation = ChatUtil.chatColor(configuration.getString("settings.messages.noInvitation"));
        alreadyPlay = ChatUtil.chatColor(configuration.getString("settings.messages.alreadyPlay"));
        alreadyInvite = ChatUtil.chatColor(configuration.getString("settings.messages.alreadyInvite"));
        winMessage = ChatUtil.chatColor(configuration.getString("settings.messages.winMessage"));
        loseMessage = ChatUtil.chatColor(configuration.getString("settings.messages.loseMessage"));
        drawNormalMessage = ChatUtil.chatColor(configuration.getString("settings.messages.drawNormalMessage"));
        drawReplayMessage = ChatUtil.chatColor(configuration.getString("settings.messages.drawReplayMessage"));
        successfullyChoice = ChatUtil.chatColor(configuration.getString("settings.messages.successfullyChoice"));
        successfullyInvite = ChatUtil.chatColor(configuration.getString("settings.messages.successfullyInvite"));
        successfullyPluginReload = ChatUtil.chatColor(configuration.getString("settings.messages.successfullyPluginReload"));

        //buttons
        rpsInviteAcceptButton = ChatUtil.chatColor(configuration.getString("settings.chatButtons.rpsInviteAcceptButton"));
        rpsInviteDenyButton = ChatUtil.chatColor(configuration.getString("settings.chatButtons.rpsInviteDenyButton"));
    }

    // for safely converting string to material
    private Material getMaterialOf(String string){
        Material material;
        try{
            material = Material.valueOf(string);
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException("\nOne of the material given in config.yml does not exist." +
                    " Pleas check config file. Full list of allowed materials you can find here " +
                    "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html\\n");
        }
        return material;
    }
}
