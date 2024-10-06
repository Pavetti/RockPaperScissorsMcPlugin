package pl.pavetti.rockpaperscissors.game.gui.findenemygui;

import org.bukkit.Material;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.controlitem.PageItem;

public class PageBackItem extends PageItem {

    public PageBackItem() {
        super(false);
    }

    @Override
    public ItemProvider getItemProvider(PagedGui<?> gui) {
        ItemBuilder builder = new ItemBuilder(Material.RED_STAINED_GLASS_PANE);
        builder.setDisplayName("Previous page")
                .addLoreLines(gui.hasPreviousPage()
                        ? "Go to page " + gui.getCurrentPage() + "/" + gui.getPageAmount()
                        : "You can't go further back");

        return builder;
    }

}
