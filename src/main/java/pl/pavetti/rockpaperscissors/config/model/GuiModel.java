package pl.pavetti.rockpaperscissors.config.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public record GuiModel(
        List<String> structure,
        String title,
        Map<String,GuiItemModel> items
) {
}
