package net.shin1gamix.dupemachine.Utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * {@link https://www.spigotmc.org/members/2008choco.31119/}
 * 
 * Declared class as final so as to prevent it from being extended by a
 * subclass.
 */
public final class Ut {

	/*
	 * An empty, private constructor as suggested by (link above) to avoid
	 * instantiation.
	 */
	private Ut() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Checks whether a path from the file is an instance of ArrayList or not.
	 * 
	 * @param file
	 *            The file to use.
	 * @param path
	 *            The path to check.
	 * @see FileConfiguration#get(String arg0)
	 * @return boolean -> Whether the path is an array list or not
	 * @since 0.1
	 */
	public static boolean isList(final FileConfiguration file, final String path) {
		final boolean contains = file.contains(path);
		final boolean isList = file.get(path) instanceof ArrayList;
		return contains && isList;
	}

	/**
	 * Checks whether a path from the file is an instanceof ArrayList or not.
	 * 
	 * @param obj
	 *            The object to compare
	 * @return boolean -> Whether the object is an instance of arraylist or not
	 * @since 0.1
	 */
	public static boolean isList(final Object obj) {
		return obj instanceof ArrayList;

	}

	/**
	 * Broadcasts a translated message to the server.
	 *
	 * @param message
	 *            Which string to broadcast
	 * 
	 */
	public static void bcMsg(String message) {
		Bukkit.getServer().broadcastMessage(tr(message));
	}

	/**
	 * Sends a translated message to the server.
	 *
	 * @param message
	 *            Which string to broadcast
	 * 
	 */
	public static void msgConsole(String message) {
		Bukkit.getServer().broadcastMessage(tr(message));
	}

	/**
	 * Sends a translated list message to the server.
	 *
	 * @param message
	 *            Which List string to broadcast
	 * 
	 */
	public static void msgConsole(List<String> message) {
		tr(message).forEach(Bukkit.getServer().getConsoleSender()::sendMessage);
	}

	/**
	 * Sends a message to a CommandSender.
	 *
	 * @param target
	 *            Target to send the message
	 * @param message
	 *            The message to be sent.
	 * 
	 */
	public static void msg(CommandSender target, String message) {
		target.sendMessage(tr(message));
	}

	/**
	 * Sends a list message to a CommandSender.
	 *
	 * @param target
	 *            Target to send the message
	 * @param message
	 *            The list message to be sent.
	 * 
	 */
	public static void msg(CommandSender target, List<String> message) {
		tr(message).forEach(target::sendMessage);
	}

	/**
	 * Sends a message to a CommandSender with placeholders.
	 *
	 * @param target
	 *            Target to send the message
	 * @param message
	 *            The message to be sent.
	 * @param map
	 *            The map containing the placeholders
	 * 
	 */
	public static void msg(final CommandSender target, final String message, final Map<String, String> map) {
		if (map == null)
			throw new NullPointerException("The Map object provided is null.");
		msg(target, placeholderStr(message, map));
	}

	/**
	 * Sends a list message to a CommandSender with placeholders.
	 *
	 * @param target
	 *            Target to send the message
	 * @param message
	 *            The list message to be sent.
	 * @param map
	 *            The map containing the placeholders
	 * 
	 */
	public static void msg(final CommandSender target, final List<String> message, final Map<String, String> map) {
		if (map == null)
			throw new NullPointerException("The Map object provided is null.");
		msg(target, placeholderColl(message, map));
	}

	/**
	 * Returns a message translated.
	 * 
	 * @param msg
	 *            The message to be translated
	 * 
	 * @return A translated message
	 */
	public static String tr(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	/**
	 * Returns a collection to be translated using Java 8.
	 * 
	 * @param coll
	 *            The collection to be translated
	 * 
	 * @return A translated message
	 */
	public static List<String> tr(Collection<? extends String> coll) {
		return coll.stream().map(str -> tr(str)).collect(Collectors.toList());
	}

	/**
	 * Caps the first letter of a string.
	 *
	 * @param string
	 *            The string to be capped.
	 * @param reset
	 *            Boolean to if the string should be lowercase (reseted)
	 * 
	 * @return A given string with it's first letter capped.
	 * 
	 */
	public static String capFirst(String string, boolean reset) {
		if (reset)
			string = string.toLowerCase();
		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}

	/**
	 * Inserts placeholders in a string using Java 8.
	 *
	 * @param str
	 *            The string where the placeholders are going in
	 * @param map
	 *            The map with the placeholders to replace
	 * 
	 * @return The string that has been replaced with placeholders
	 * 
	 */
	public static String placeholderStr(String str, Map<String, String> map) {
		if (map == null)
			return str;
		for (Entry<String, String> entr : map.entrySet()) {
			str = str.replace(entr.getKey(), entr.getValue());
		}
		return str;
	}

	/**
	 * Inserts placeholders in a collection string
	 *
	 * @param coll
	 *            The collection for the placeholders to be inserted
	 * @param map
	 *            The map with the placeholders to replace
	 * 
	 * @return The collection that has been replaced with placeholders
	 * 
	 */
	public static List<String> placeholderColl(Collection<? extends String> coll, Map<String, String> map) {
		if (map == null)
			return new ArrayList<>();
		return coll.stream().map(x -> placeholderStr(x, map)).collect(Collectors.toList());
	}

	/**
	 * Checks if the string provided has any illegal chars.
	 *
	 * @param message
	 *            Which chars to check
	 * 
	 * @return Boolean for whether the string hasn't any invalid characters.
	 * 
	 */
	public static boolean isAllowed(final String message) {
		return message.isEmpty() || message.replace(" ", "").equalsIgnoreCase("") ? true
				: Pattern.compile("[a-zA-Z0-9]*").matcher(message).matches();
	}

	/**
	 * A random int between two values (minimum int, maximum int)
	 *
	 * @param min
	 *            The minimum amount the int can be
	 * @param max
	 *            The maxmimum amount the int can be
	 * @throws NullPointerException
	 *             If the minimum int is higher than max
	 * 
	 * @return A random number between two integers
	 * 
	 */
	public static int getRandomInt(int min, int max) {
		if (max <= min) {
			throw new NumberFormatException(
					"The minimum number can't be higher than the maximum, min:" + min + " - max:" + max);
		}
		return ThreadLocalRandom.current().nextInt(max - min) + min;
	}

}
