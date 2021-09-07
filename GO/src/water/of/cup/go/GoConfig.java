package water.of.cup.go;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;

import water.of.cup.boardgames.config.GameRecipe;
import water.of.cup.boardgames.config.GameSound;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameConfig;

public class GoConfig extends GameConfig {
    public GoConfig(Game game) {
        super(game);
    }

    @Override
    protected GameRecipe getGameRecipe() {
        HashMap<String, String> recipe = new HashMap<>();
        recipe.put("L", Material.LEATHER.toString());
        recipe.put("B", Material.WHITE_DYE.toString());
        recipe.put("W", Material.BLACK_DYE.toString());
        recipe.put("C", Material.CLAY.toString());

        ArrayList<String> shape = new ArrayList<String>() {
            {
                add("LWL");
                add("LCL");
                add("LBL");
            }
        };

        return new GameRecipe(game.getName(), recipe, shape);
    }

    @Override
    protected ArrayList<GameSound> getGameSounds() {
        ArrayList<GameSound> gameSounds = new ArrayList<>();
        //gameSounds.add(new GameSound("click", Sound.BLOCK_WOOD_PLACE));
        return gameSounds;
    }

    @Override
    protected HashMap<String, Object> getCustomValues() {
        return null;
    }

    @Override
    protected int getWinAmount() {
        return 0;
    }
}