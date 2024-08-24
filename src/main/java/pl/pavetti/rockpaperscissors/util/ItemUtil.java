package pl.pavetti.rockpaperscissors.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import pl.pavetti.rockpaperscissors.Main;

@UtilityClass
public class ItemUtil {

    public static Material getMaterialOf(String string){
        Material material;
        try{
            material = Material.valueOf(string);
        }catch (IllegalArgumentException e){
            Main.getInstance().getLogger().severe("\nOne of the material given in config.yml does not exist." +
                    " Pleas check config file. Full list of allowed materials you can find here " +
                    "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html\\n");
            material = Material.BARRIER;
        }
        return material;
    }
}
