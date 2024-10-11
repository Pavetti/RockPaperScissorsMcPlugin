package pl.pavetti.rockpaperscissors.game.gui.findenemygui;

import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import pl.pavetti.rockpaperscissors.config.file.GuiConfig;
import pl.pavetti.rockpaperscissors.config.model.GuiItemModel;
import pl.pavetti.rockpaperscissors.util.PlayerUtil;
import pl.pavetti.rockpaperscissors.util.TextUtil;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.builder.SkullBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.util.MojangApiUtils;
import xyz.xenondevs.invui.window.Window;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FindEnemyGui {

    private final GuiConfig guiConfig;

    public FindEnemyGui(GuiConfig guiConfig) {
        this.guiConfig = guiConfig;
    }

    public void openGui(Player player) {
        Window.single()
                .setViewer(player)
                .setTitle(  TextUtil.wrapTextToXenoComponent( guiConfig.getFindEnemyGuiModel().title() ) )
                .setGui(buildGui())
                .build()
                .open();
    }

    private Gui buildGui() {
        Map<String, GuiItemModel> items = guiConfig.getFindEnemyGuiModel().items();
        Item border = new SimpleItem(
                new ItemBuilder(
                        items.get( "fillItem" ).material() ).setDisplayName( " " ) );

        return PagedGui.items()
                .setStructure(guiConfig.getFindEnemyGuiModel().structure().toArray(new String[0]))
                .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL) // where paged items should be put
                .addIngredient('#', border)
                .addIngredient('<', new PageBackItem(guiConfig))
                .addIngredient('>', new PageForwardItem(guiConfig))
                .setContent(getPlayersHeads(items))
                .build();
    }


    private List<Item> getPlayersHeads(Map<String, GuiItemModel> items){

        List<Player> players = Bukkit.getOnlinePlayers().stream()
                .filter( player -> !PlayerUtil.isVanished( player ) )
                .filter( player -> player.hasPermission( "rps.playerslist.noinclude" ) )
                .collect( Collectors.toList() );
        List<Item> playerHeads = new ArrayList<>();
        for (Player player : players) {

            try {
                SimpleItem item = new SimpleItem(
                        new SkullBuilder( player.getName() )
                                .setDisplayName( TextUtil.wrapTextToXenoComponent( items.get("playerHead").name()
                                        .replace( "{PLAYER}",player.getName() ))
                                )
                                .setLore( TextUtil.wrapTextListToXenoComponentList(
                                        items.get("playerHead").lore() )
                                )
                );
                playerHeads.add( item );
            } catch (MojangApiUtils.MojangApiException | IOException e) {
                throw new RuntimeException( e );
            }
        }
        return playerHeads;
    }
}
