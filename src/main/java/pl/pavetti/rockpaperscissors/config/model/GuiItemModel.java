package pl.pavetti.rockpaperscissors.config.model;

import org.bukkit.Material;

import java.util.List;

public record GuiItemModel(
        String name,
        List<String> lore,
        List<String> hasNextPageLore,
        List<String> hasNotNextPageLore,
        Material material,
        int modelId
) {
}
