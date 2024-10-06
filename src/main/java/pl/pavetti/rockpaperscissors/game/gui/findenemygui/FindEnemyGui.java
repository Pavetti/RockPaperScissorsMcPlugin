package pl.pavetti.rockpaperscissors.game.gui.findenemygui;

import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import pl.pavetti.rockpaperscissors.util.PlayerUtil;
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
import java.util.stream.Collectors;

public class FindEnemyGui {

    public void openGui(Player player) {
        Window.single()
                .setViewer(player)
                .setTitle(  wrapComponent( "found enemy" ) )
                .setGui(buildGui())
                .build()
                .open();
    }

    public AdventureComponentWrapper wrapComponent(String text) {
        return new AdventureComponentWrapper( MineDown.parse(text));
    }



    private Gui buildGui() {
        Item border = new SimpleItem( new ItemBuilder( Material.BLACK_STAINED_GLASS_PANE ).setDisplayName( " " ) );

        return PagedGui.items()
                .setStructure(
                        "# # # # # # # # #",
                        "# x x x x x x x #",
                        "# x x x x x x x #",
                        "# # # < # > # # #")
                .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL) // where paged items should be put
                .addIngredient('#', border)
                .addIngredient('<', new PageBackItem())
                .addIngredient('>', new ForwardPageItem())
                .setContent(getPlayersHeads())
                .build();
    }

    private List<Item> getPlayersHeads(){

        TextComponent lore = (TextComponent) Component.text("click to challenge");
        List<ComponentWrapper> loreList = new ArrayList<>();
        loreList.add(new AdventureComponentWrapper( lore ));

        List<Player> players = Bukkit.getOnlinePlayers().stream()
                .filter( player -> !PlayerUtil.isVanished( player ) )
                .filter( player -> player.hasPermission( "rps.playerslist.noinclude" ) )
                .collect( Collectors.toList() );
        List<Item> playerHeads = new ArrayList<>();
        for (Player player : players) {

            SimpleItem item = null;
            try {
                item = new SimpleItem(
                        new SkullBuilder( player.getName() )
                                .setDisplayName( player.getName() )
                                .setLore( loreList )
                );
            } catch (MojangApiUtils.MojangApiException | IOException e) {
                throw new RuntimeException( e );
            }
            playerHeads.add( item );
        }
        return playerHeads;
    }
}
