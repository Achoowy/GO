package water.of.cup.go;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import water.of.cup.boardgames.game.BoardItem;
import water.of.cup.boardgames.game.Button;
import water.of.cup.boardgames.game.Clock;
import water.of.cup.boardgames.game.Game;
import water.of.cup.boardgames.game.GameConfig;
import water.of.cup.boardgames.game.GameImage;
import water.of.cup.boardgames.game.GamePlayer;
import water.of.cup.boardgames.game.inventories.GameInventory;
import water.of.cup.boardgames.game.storage.GameStorage;

public class GoGame extends Game {
	private int boardType;
	private String[][] positions; // "WHITE" / "BLACK"
	private Button[][] positionButtons;
//	private double capturedBlack;
//	private double capturedWhite;
	private ArrayList<String> previousPositions;
	private double komi = 5.5;

	private int consecutiveSkips;

	public GoGame(int rotation) {
		super(rotation);
		boardType = 19;
		setPassButtons();
	}

	@Override
	protected void startGame() {
		this.setInGame();
		boardType = 19;
		createBoard();
//		capturedBlack = 0;
//		capturedWhite = 0;
		consecutiveSkips = 0;
		previousPositions = new ArrayList<String>();
		teamManager.setTurn("BLACK");
	}

	private void setPassButtons() {
		Button pass1 = new Button(this, "GO_PASS", new int[] { 56, 0 }, 2, "pass");
		Button pass2 = new Button(this, "GO_PASS", new int[] { 56, 121 }, 0, "pass");
		pass1.setClickable(true);
		pass2.setClickable(true);
		buttons.add(pass1);
		buttons.add(pass2);
	}

	private void createBoard() {
		gameImage.setImage("GO_BOARD_" + boardType);
		positions = new String[boardType][boardType];

		// remove old buttons
		if (positionButtons != null)
			for (Button[] line : positionButtons)
				for (Button b : line)
					buttons.remove(b);
		setPositionButtons();

	}

	private void setPositionButtons() {
		positionButtons = new Button[boardType][boardType];
		switch (boardType) {
		case 19:
			for (int x = 0; x < boardType; x++)
				for (int y = 0; y < boardType; y++) {
					positionButtons[y][x] = new Button(this, "GO_CHIP_NONE_19",
							new int[] { 6 + x * 6 + x / 3 - x / 6, 6 + y * 6 + y / 3 - y / 6 }, y, "chip");
					positionButtons[y][x].setClickable(true);
					buttons.add(positionButtons[y][x]);
				}
			break;
		}
	}

	@Override
	protected void setMapInformation(int rotation) {
		this.mapStructure = new int[][] { { 1 } };
		this.placedMapVal = 1;
	}

	@Override
	protected void setGameName() {
		this.gameName = "Go";

	}

	@Override
	protected void setBoardImage() {
		this.gameImage = new GameImage("GO_BOARD_19", 0);
	}

	@Override
	protected void clockOutOfTime() {
	}

	@Override
	protected Clock getClock() {
		return null;
	}

	@Override
	protected GameInventory getGameInventory() {
		return new GoInventory(this);
	}

	@Override
	protected GameStorage getGameStorage() {
		return new GoStorage(this);
	}

	@Override
	public ArrayList<String> getTeamNames() {
		ArrayList<String> teams = new ArrayList<String>();
		teams.add("BLACK");
		teams.add("WHITE");
		return teams;
	}

	@Override
	protected GameConfig getGameConfig() {
		return new GoConfig(this);
	}

	@Override
	public void click(Player player, double[] loc, ItemStack map) {
		GamePlayer gamePlayer = getGamePlayer(player);
		if (!teamManager.getTurnPlayer().equals(gamePlayer))
			return;

		int[] clickLoc = mapManager.getClickLocation(loc, map);
		Button b = getClickedButton(gamePlayer, clickLoc);

		if (b == null)
			return;

		if (b.getName().equals("chip")) {
			int[] position = getChipLocation(b);
			placeChip(position);
			return;
		}

		if (b.getName().equals("pass")) {
			consecutiveSkips++;
			if (consecutiveSkips >= 2) {
				this.gameOver();
				return;
			}
			teamManager.nextTurn();
			teamManager.getTurnPlayer().getPlayer().sendMessage(gamePlayer.getPlayer().getName() + " passed");
		}
	}

	private void placeChip(int[] position) {
		if (position == null)
			return;
		if (positions[position[1]][position[0]] != null)
			return;
		String turn = teamManager.getTurnTeam();
		String opponent = turn.equals("WHITE") ? "BLACK" : "WHITE";

		String[][] newPositions = Arrays.stream(positions).map(String[]::clone).toArray(String[][]::new);
		newPositions[position[1]][position[0]] = turn;

		ArrayList<int[]> capturedOpponentChips = getCapturedChips(opponent, position);
		if (capturedOpponentChips.size() > 0) {
			for (int[] pos : capturedOpponentChips)
				newPositions[pos[1]][pos[0]] = null;
		} else if (getCapturedChips(turn, position).size() > 0) {
			// unnecessary sacrifice made
			teamManager.getTurnPlayer().getPlayer().sendMessage("You can not make an unnecessary sacrifice.");
			return;
		}

		// create position string
		String positionString = "";
		for (String[] line : newPositions)
			for (String s : line)
				if (s == null)
					positionString += "n";
				else
					positionString += s;
		// check if position has already been visited
		if (previousPositions.contains(positionString)) {
			teamManager.getTurnPlayer().getPlayer().sendMessage("You can not return the board to a previous position.");
			return;
		}
		previousPositions.add(positionString);
		positions = newPositions;
		updatePositionButtons();

//		if (capturedOpponentChips.size() > 0) {
//			if (opponent.equals("WHITE")) {
//				capturedWhite += capturedOpponentChips.size();
//			} else {
//				capturedBlack += capturedOpponentChips.size();
//			}
//			for (GamePlayer gp : teamManager.getGamePlayers()) {
//				gp.getPlayer().sendMessage("White has captured " + capturedBlack + " stones.");
//				gp.getPlayer().sendMessage("Black has captured " + capturedWhite + " stones.");
//			}
//
//		}

		consecutiveSkips = 0;
		teamManager.nextTurn();
	}

	private void updatePositionButtons() {
		for (int x = 0; x < boardType; x++)
			for (int y = 0; y < boardType; y++)
				if (positions[y][x] == null)
					positionButtons[y][x].setImage("GO_CHIP_NONE_" + boardType);
				else
					positionButtons[y][x].setImage("GO_CHIP_" + positions[y][x] + "_" + boardType);
		mapManager.renderBoard();
	}

	private ArrayList<int[]> getCapturedChips(String turn, int[] position) {
		String currentTurn = teamManager.getTurnTeam();

		String[][] newPositions = Arrays.stream(positions).map(String[]::clone).toArray(String[][]::new);
		newPositions[position[1]][position[0]] = currentTurn;

		int[][] checkPositions;
		if (currentTurn.equals(turn))
			checkPositions = new int[][] { position };
		else
			checkPositions = new int[][] { { position[0] + 1, position[1] }, { position[0] - 1, position[1] },
					{ position[0], position[1] + 1 }, { position[0], position[1] - 1 } };

		ArrayList<int[]> captured = new ArrayList<int[]>();

		for (int[] startPos : checkPositions) {
			if (!positionOnBoard(startPos))
				continue;
			if (!startPos.equals(turn))
				continue;

			ArrayList<int[]> capturedQueue = new ArrayList<int[]>();
			capturedQueue.add(startPos);
			int capturedQueuePos = 0;

			queueCheck: while (capturedQueue.size() > capturedQueuePos) {
				int[] currentPos = capturedQueue.get(capturedQueuePos);
				if (arrayListContainsPosition(captured, currentPos))
					break;

				capturedQueuePos++;
				int[][] neighbors = new int[][] { { currentPos[0] + 1, currentPos[1] },
						{ currentPos[0] - 1, currentPos[1] }, { currentPos[0], currentPos[1] + 1 },
						{ currentPos[0], currentPos[1] - 1 } };
				for (int[] neighbor : neighbors) {
					if (!positionOnBoard(neighbor))
						continue;
					if (arrayListContainsPosition(capturedQueue, neighbor))
						continue;
					if (arrayListContainsPosition(captured, neighbor))
						break queueCheck;
					if (newPositions[neighbor[1]][neighbor[0]] == null) {
						capturedQueue.add(neighbor);
						break queueCheck;
					}
					if (newPositions[neighbor[1]][neighbor[0]].equals(turn))
						capturedQueue.add(neighbor);
				}
			}
			// add captured squares to captured
			if (capturedQueue.size() <= capturedQueuePos)
				captured.addAll(capturedQueue);
		}

		return captured;
	}

	private boolean arrayListContainsPosition(ArrayList<int[]> array, int[] pos) {
		for (int[] tpos : array)
			if (tpos[0] == pos[0] && tpos[1] == pos[1])
				return true;

		return false;
	}

	private boolean positionOnBoard(int[] position) {
		return !(position[0] < 0 || position[0] >= boardType || position[1] < 0 || position[1] >= boardType);
	}

	private int[] getChipLocation(Button b) {
		for (int x = 0; x < boardType; x++)
			for (int y = 0; y < boardType; y++)
				if (positionButtons[y][x] == b)
					return new int[] { x, y };

		return null;
	}

	private void gameOver() {
		// uses area scoring
		String[][] whiteConnectedTerritory = Arrays.stream(positions).map(String[]::clone).toArray(String[][]::new);
		String[][] blackConnectedTerritory = Arrays.stream(positions).map(String[]::clone).toArray(String[][]::new);
		int[][] directions = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
		boolean changeMade = true;
		while (changeMade) {
			changeMade = false;
			for (int x = 0; x < boardType; x++)
				for (int y = 0; y < boardType; y++) {
					for (int[] direction : directions) {
						int[] pos = { x + direction[0], y + direction[1] };
						if (!positionOnBoard(pos))
							continue;
						if (whiteConnectedTerritory[y][x] == null && whiteConnectedTerritory[pos[1]][pos[0]] != null
								&& whiteConnectedTerritory[pos[1]][pos[0]].equals("WHITE")) {
							changeMade = true;
							whiteConnectedTerritory[y][x] = "WHITE";
						}
						if (blackConnectedTerritory[y][x] == null && blackConnectedTerritory[pos[1]][pos[0]] != null
								&& blackConnectedTerritory[pos[1]][pos[0]].equals("BLACK")) {
							changeMade = true;
							blackConnectedTerritory[y][x] = "BLACK";
						}
					}
				}
		}
		// combine areas
		double whitePoints = 0;
		double blackPoints = 0;
		for (int x = 0; x < boardType; x++)
			for (int y = 0; y < boardType; y++)
				if (whiteConnectedTerritory[y][x] != null && whiteConnectedTerritory[y][x].equals("WHITE")
						&& blackConnectedTerritory[y][x] != null && blackConnectedTerritory[y][x].equals("BLACK"))
					positions[y][x] = null;
				else if (whiteConnectedTerritory[y][x] != null && whiteConnectedTerritory[y][x].equals("WHITE")) {
					positions[y][x] = "WHITE";
					whitePoints++;
				} else if (blackConnectedTerritory[y][x] != null && blackConnectedTerritory[y][x].equals("BLACK")) {
					positions[y][x] = "BLACK";
					blackPoints++;
				}
		whitePoints += komi;
		updatePositionButtons();
		for (GamePlayer gp : teamManager.getGamePlayers()) {
			gp.getPlayer().sendMessage("White: " + whitePoints);
			gp.getPlayer().sendMessage("Black: " + blackPoints);
		}
		this.endGame(teamManager.getGamePlayerByTeam(whitePoints > blackPoints ? "WHITE" : "BLACK"));
	}

	@Override
	protected void gamePlayerOutOfTime(GamePlayer turn) {
	}

	@Override
	public ItemStack getBoardItem() {
		return new BoardItem(gameName, new ItemStack(Material.BIRCH_TRAPDOOR, 1));
	}

}
