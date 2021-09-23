package water.of.cup.go;

import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.storage.BoardGamesStorageType;
import water.of.cup.boardgames.game.storage.GameStorage;
import water.of.cup.boardgames.game.storage.StorageType;

public class GoStorage extends GameStorage {

	public GoStorage(Game game) {
		super(game);
	}

	@Override
	protected String getTableName() {
		return "go";
	}

	@Override
	protected StorageType[] getGameStores() {
		return new StorageType[] { 
				BoardGamesStorageType.WINS, 
				BoardGamesStorageType.LOSSES 
				};
	}
}
