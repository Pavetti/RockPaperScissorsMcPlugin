package pl.pavetti.rockpaperscissors.game.gui.findenemygui;

import pl.pavetti.rockpaperscissors.config.file.GuiConfig;
import pl.pavetti.rockpaperscissors.config.model.GuiItemModel;
import pl.pavetti.rockpaperscissors.util.TextUtil;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;

import java.util.List;

public class PageForwardItem extends PageItem {

    private final GuiConfig guiConfig;

    public PageForwardItem(GuiConfig guiConfig) {
        super(true);
        this.guiConfig = guiConfig;
    }

    @Override
    public ItemProvider getItemProvider(PagedGui<?> gui) {
        GuiItemModel pageForwardItemModel = guiConfig.getFindEnemyGuiModel().items().get( "pageForward" );
        ItemBuilder builder = new ItemBuilder(pageForwardItemModel.material());
        builder.setDisplayName(TextUtil.wrapTextToXenoComponent( pageForwardItemModel.name() ));
        if( gui.hasNextPage() ){
            builder.setLore( getPageItemLoreFrom( pageForwardItemModel.hasNextPageLore(), gui ) );
        } else {
            builder.setLore( getPageItemLoreFrom( pageForwardItemModel.hasNotNextPageLore(), gui ) );
        }


        return builder;
    }

    private List<ComponentWrapper> getPageItemLoreFrom(List<String> lore, PagedGui<?> gui){
        lore.forEach( line -> line
                .replace( "{CURRENT}", String.valueOf( gui.getCurrentPage() ) )
                .replace( "{MAX}", String.valueOf( gui.getPageAmount() ) )
        );
        return lore.stream().map( TextUtil::wrapTextToXenoComponent ).toList();
    }

}
