package pl.pavetti.rockpaperscissors.config;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import pl.pavetti.rockpaperscissors.Main;
import pl.pavetti.rockpaperscissors.exception.BadMaterialConvertException;
import pl.pavetti.rockpaperscissors.util.ChatUtil;
@Getter
public class  Settings {

    private static Settings instance;

    //basic
    private String prefix;

    //gameConfig
    private boolean active;
    private boolean replayOnDraw;
    private boolean loseBetOnLeave;
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
    private String playerNotExist;
    private String rpsInvite;
    private String noInvitation;
    private String winMessage;
    private String loseMessage;
    private String drawMessage;

    private Settings() {
        load();
    }

    public static Settings getInstance(){
        if(instance == null){
            instance = new Settings();
        }
        return instance;
    }

    private void load(){
        FileConfiguration configuration = Main.getInstance().getConfig();

        //basic
        prefix = ChatUtil.chatColor(configuration.getString("settings.prefix"));
        //gameConfig
        active = configuration.getBoolean("settings.gameConfig.active");
        replayOnDraw = configuration.getBoolean("settings.gameConfig.replayOnDraw");
        loseBetOnLeave = configuration.getBoolean("settings.gameConfig.loseBetOnLeave");
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
        playerNotExist = ChatUtil.chatColor(configuration.getString("settings.messages.playerNotExist"));
        rpsInvite = ChatUtil.chatColor(configuration.getString("settings.messages.rpsInvite"));
        noInvitation = ChatUtil.chatColor(configuration.getString("settings.messages.noInvitation"));
        winMessage = ChatUtil.chatColor(configuration.getString("settings.messages.winMessage"));
        loseMessage = ChatUtil.chatColor(configuration.getString("settings.messages.loseMessage"));
        drawMessage = ChatUtil.chatColor(configuration.getString("settings.messages.drawMessage"));
    }

    private Material getMaterialOf(String string){
        Material material = Material.valueOf(string);
        if (material == null) throw new BadMaterialConvertException(
                "One of the items given in config.yml does not exist. Pleas check config file.");
        return material;
    }
}
