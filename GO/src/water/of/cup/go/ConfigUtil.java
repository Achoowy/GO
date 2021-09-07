package water.of.cup.go;

import org.bukkit.ChatColor;

import water.of.cup.boardgames.BoardGames;
import water.of.cup.boardgames.config.ConfigInterface;

public enum ConfigUtil implements ConfigInterface {

	// CHAT

	// POKER
	CHAT_GO_SACRIFICE("settings.messages.chat.go.sacrifice", "You can not make an unnecessary sacrifice."),
	CHAT_GO_PREVIOUSPOSITION("settings.messages.chat.go.previousposition", "You can not return the board to a previous position."),
    CHAT_GO_PLAYERPASSED("settings.messages.chat.go.playerpassed", "%player% passed."),
    CHAT_GO_WHITESCORE("settings.messages.chat.go.whitescore", "White: %num%"),
    CHAT_GO_BLACKSCORE("settings.messages.chat.go.blackscore", "Black: %num%");

	private final String path;
	private final String defaultValue;
	private static final BoardGames instance = BoardGames.getInstance();

	ConfigUtil(String path, String defaultValue) {
		this.path = path;
		this.defaultValue = defaultValue;
	}

	@Override
	public String toString() {
		String configString = instance.getConfig().getString(this.path);

		if (configString == null)
			return "";

		return ChatColor.translateAlternateColorCodes('&', configString);
	}

	public String toRawString() {
		return ChatColor.stripColor(this.toString());
	}

	@Override
	public String getPath() {
		return this.path;
	}

	@Override
	public String getDefaultValue() {
		return this.defaultValue;
	}

	public String buildString(String replaceWith) {
		String formatted = this.toString();

		formatted = formatted.replace("%player%", replaceWith).replace("%game%", replaceWith).replace("%num%",
				replaceWith);
		return formatted;
	}

	public String buildString(String player, String game, Number num) {
		String formatted = this.toString();

		formatted = formatted.replace("%player%", player).replace("%game%", game).replace("%num%", num + "");
		return formatted;
	}

	public String buildString(String replaceWith, int num) {
		String formatted = this.toString();

		formatted = formatted.replace("%player%", replaceWith).replace("%game%", replaceWith).replace("%num%",
				num + "");

		return formatted;
	}

	public String buildString(String replaceWith, int num, int num2) {
		String formatted = this.toString();

		formatted = formatted.replace("%player%", replaceWith).replace("%game%", replaceWith).replace("%num%", num + "")
				.replace("%num2%", num2 + "");

		return formatted;
	}

	public String buildString(Number num, Number num2) {
		String formatted = this.toString();

		formatted = formatted.replace("%num%", num + "").replace("%num2%", num2 + "");

		return formatted;
	}
}