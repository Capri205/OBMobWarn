package net.obmc.OBMobWarn;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class MessagingUtils {

	static Logger log = Logger.getLogger("Minecraft");

	public static void sendWarning(String mobName, Component directionIndicator, Player player) {

		Component warnMessage = buildWarning(mobName, directionIndicator);

		if (OBMobWarn.getInstance().getConfgManager().getDoActionBarWarning()) {
			sendActionBarWarning(player, warnMessage);
		}
		if (OBMobWarn.getInstance().getConfgManager().getDoChatWarning()) {
			sendChatWarning(player, warnMessage);
		}
	}

	public static void sendActionBarWarning(Player player, Component message) {
		player.sendActionBar(message);
	}
	public static void sendChatWarning(Player player, Component message) {
		player.sendMessage(message);
	}

	private static Component buildWarning(String mobName, Component directionIndicator) {
		return Component.text(mobName)
			.color(NamedTextColor.RED)
			.decorate(TextDecoration.BOLD)
			.append(Component.text(" "))
			.append(directionIndicator);
	}
}
