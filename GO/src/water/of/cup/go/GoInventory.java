package water.of.cup.go;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;

import water.of.cup.boardgames.config.ConfigUtil;
import water.of.cup.boardgames.game.GamePlayer;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.inventories.GameOption;
import water.of.cup.boardgames.game.inventories.GameOptionType;

public class GoInventory extends GameInventory {

    private final GoGame game;

    public GoInventory(GoGame game) {
        super(game);
        this.game = game;
    }

    @Override
    protected ArrayList<GameOption> getOptions() {
        ArrayList<GameOption> options = new ArrayList<>();
        List<String> boardSize = Arrays.asList("7", "13", "19");
		GameOption size = new GameOption("size", Material.EXPERIENCE_BOTTLE, GameOptionType.COUNT, null, boardSize.get(1), boardSize);
		options.add(size);
        return options;
    }

    @Override
    protected int getMaxQueue() {
        return 3;
    }

    @Override
    protected int getMaxGame() {
        return 2;
    }

    @Override
    protected int getMinGame() {
        return 2;
    }

    @Override
    protected boolean hasTeamSelect() {
        return true;
    }

    @Override
    protected boolean hasGameWagers() {
        return true;
    }

    @Override
    protected boolean hasWagerScreen() {
        return true;
    }

    @Override
    protected boolean hasForfeitScreen() {
        return true;
    }

    @Override
    protected void onGameCreate(HashMap<String, Object> gameData, ArrayList<GamePlayer> players) {
        for(GamePlayer player : players) {
            player.getPlayer().sendMessage(ConfigUtil.CHAT_WELCOME_GAME.buildString(game.getAltName()));
        }

        game.startGame();
    }
}
