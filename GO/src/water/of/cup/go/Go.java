package water.of.cup.go;

import java.util.ArrayList;

import water.of.cup.boardgames.extension.BoardGamesConfigOption;
import water.of.cup.boardgames.extension.BoardGamesExtension;
import water.of.cup.boardgames.game.Game;

public class Go extends BoardGamesExtension {

	@Override
	public ArrayList<Class<? extends Game>> getGames() {
		ArrayList<Class<? extends Game>> games = new ArrayList<Class<? extends Game>>();
		games.add(GoGame.class);
		return games;
	}

	@Override
	public String getExtensionName() {
		// TODO Auto-generated method stub
		return "Go";
	}

	@Override
	public ArrayList<BoardGamesConfigOption> getExtensionConfig() {
		// TODO Auto-generated method stub
		return null;
	}
}
