package net.shin1gamix.dupemachinex.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

/**
 * {@link https://www.spigotmc.org/members/2008choco.31119/}
 * 
 * Declared class as final so as to prevent it from being extended by a
 * subclass.
 */
public final class Utils {

	/*
	 * An empty, private constructor as suggested by (link above) to avoid
	 * instantiation.
	 */
	private Utils() {
		throw new UnsupportedOperationException();
	}

	public static String calculateTime(final int secondsx) {
		int days = (int) TimeUnit.SECONDS.toDays(secondsx);
		int hours = (int) (TimeUnit.SECONDS.toHours(secondsx) - TimeUnit.DAYS.toHours(days));
		int minutes = (int) (TimeUnit.SECONDS.toMinutes(secondsx) - TimeUnit.HOURS.toMinutes(hours)
				- TimeUnit.DAYS.toMinutes(days));
		int seconds = (int) (TimeUnit.SECONDS.toSeconds(secondsx) - TimeUnit.MINUTES.toSeconds(minutes)
				- TimeUnit.HOURS.toSeconds(hours) - TimeUnit.DAYS.toSeconds(days));

		String timeString = days > 0 ? pluralize("^# day^s", days) : "";
		timeString += hours > 0 ? timeString.equals("") ? "" : ", " + pluralize("^# hour^s", hours) : "";
		timeString += minutes > 0 ? timeString.equals("") ? "" : ", " + pluralize("^# minute^s", minutes) : "";
		timeString += seconds > 0 ? timeString.equals("") ? "" : ", " + pluralize("^# second^s", seconds) : "";
		return timeString.equals("") ? "0 second(s)" : timeString;

	}

	private static String pluralize(String text, int value) {
		Map<String, String> map = Maps.newHashMap();
		map.put("^s", value > 1 ? "s" : "");
		map.put("^#", String.valueOf(value));
		return placeHolder(text, map, false);
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
	public static String getMachineIP() {
		try {
			return new BufferedReader(new InputStreamReader(new URL("http://checkip.amazonaws.com").openStream()))
					.readLine();
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean isDouble(final String input) {
		return Doubles.tryParse(StringUtils.deleteWhitespace(input)) != null;
	}

	public static boolean isInt(final String input) {
		return Ints.tryParse(StringUtils.deleteWhitespace(input)) != null;
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
		return isList(file.get(path));
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

	// bcMsg
	// -----------------------------------------------------------------------
	/**
	 * Self explanatory.
	 * 
	 * Usage:
	 * 
	 * Utils.bcMsg("Test");
	 */
	public static void bcMsg(final String input) {
		Bukkit.broadcastMessage(colorize(input));
	}

	public static void bcMsg(final List<String> input) {
		input.forEach(Utils::bcMsg);
	}

	@Deprecated
	public static void bcMsg(final Object input) {
		bcMsg(String.valueOf(input));
	}

	/**
	 * msgConsole Methods with colored messages.
	 */

	public static void msgConsole(final String... messages) {
		Stream.of(messages).forEach(msg -> Bukkit.getConsoleSender().sendMessage(colorize(msg)));
	}

	public static void debug(final String... messages) {
		msgConsole("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
		msgConsole(" ");
		Stream.of(messages).forEach(Utils::msgConsole);
		msgConsole(" ");
		msgConsole("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
	}

	// msg
	// -----------------------------------------------------------------------
	/**
	 * Usage
	 * 
	 * final CommandSender target = ...;
	 * 
	 * Utils.msg(target, "yourMessage");
	 */

	public static void msg(final CommandSender target, final String message) {
		Validate.notNull(target, "The commandsender can't be null");
		target.sendMessage(colorize(message));
	}

	public static void msg(final CommandSender target, final List<String> message) {
		Validate.notNull(target, "The commandsender can't be null");
		colorize(message).forEach(target::sendMessage);
	}

	public static void msg(final CommandSender target, final String[] message) {
		Validate.notNull(target, "The commandsender can't be null");
		target.sendMessage(colorize(message));

	}

	public static void msg(final CommandSender target, final String message, final Map<String, String> map,
			final boolean ignoreCase) {
		msg(target, placeHolder(message, map, ignoreCase));
	}

	public static void msg(final CommandSender target, final List<String> message, final Map<String, String> map,
			final boolean ignoreCase) {
		msg(target, placeHolder(message, map, ignoreCase));
	}

	public static void msg(final CommandSender target, final String[] message, final Map<String, String> map,
			final boolean ignoreCase) {
		msg(target, placeHolder(message, map, ignoreCase));
	}

	// placeHolder
	// -----------------------------------------------------------------------
	/**
	 * Usage
	 * 
	 * final Map<String, String> map = Utils.newMap();
	 * 
	 * map.put("%code%", "Coding");
	 * 
	 * Utils.placeHolder("%code% is fun", map, false) -> "Coding is fun"
	 */

	public static String placeHolder(String str, final Map<String, String> map, final boolean ignoreCase) {
		Validate.notNull(str, "The string can't be null!");

		if (map == null) {
			return str;
		}

		for (final Entry<String, String> entr : map.entrySet()) {
			str = ignoreCase ? replaceIgnoreCase(str, entr.getKey(), entr.getValue())
					: str.replace(entr.getKey(), entr.getValue());
		}

		return str;
	}

	public static String[] placeHolder(final String[] array, final Map<String, String> map, final boolean ignoreCase) {
		Validate.notNull(array, "The string array can't be null!");
		final String[] newarr = Arrays.copyOf(array, array.length);
		if (map == null) {
			return newarr;
		}
		for (int i = 0; i < newarr.length; i++) {
			newarr[i] = placeHolder(newarr[i], map, ignoreCase);
		}
		return newarr;
	}

	public static List<String> placeHolder(final List<String> coll, final Map<String, String> map,
			final boolean ignoreCase) {
		Validate.notNull(coll, "The string collection can't be null!");
		if (map == null) {
			return coll;
		}
		final List<String> toCopy = new ArrayList<>(coll.size());
		Collections.copy(toCopy, coll);
		return toCopy.stream().map(x -> placeHolder(x, map, ignoreCase)).collect(Collectors.toList());
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
	@Nullable
	public static boolean isStringLegal(final String message, final boolean ignoreSpace) {
		return ignoreSpace ? StringUtils.isAlphanumericSpace(message) : StringUtils.isAlphanumeric(message);
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
	public static String capFirst(final String string, final boolean reset) {
		Validate.notNull(string, "Null string object can't be capitalized!");
		return StringUtils.capitalize(reset ? string.toLowerCase() : string);
	}

	/**
	 * A random int between two values (minimum int, maximum int)
	 *
	 * @param lowerBound
	 *            The minimum amount the int can be
	 * @param upperBound
	 *            The maxmimum amount the int can be
	 * @throws NullPointerException
	 *             If the minimum int is higher than max
	 *
	 * @return A random number between two integers INCLUSIVE of UpperBound
	 *
	 */
	public static int getRandomInt(final int lowerBound, final int upperBound) {
		Validate.isTrue(upperBound > lowerBound,
				"uppderBound should be > lowerBound, min: " + lowerBound + ", max: " + upperBound);
		return ThreadLocalRandom.current().nextInt(lowerBound, upperBound + 1);
	}

	/**
	 * Returns a translated string.
	 * 
	 * @param msg
	 *            The message to be translated
	 * 
	 * @return A translated message
	 */
	public static String colorize(final String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	/**
	 * Returns a translated string array.
	 * 
	 * @param msg
	 *            The messages to be translated
	 * 
	 * @return A translated message array
	 */
	public static String[] colorize(@Nullable final String[] msg) {
		for (int i = 0; i < msg.length; i++) {
			msg[i] = colorize(msg[i]);
		}
		return msg;
	}

	/**
	 * Returns a translated string collection.
	 * 
	 * @param coll
	 *            The collection to be translated
	 * 
	 * @return A translated message
	 */
	public static List<String> colorize(final List<String> coll) {
		coll.replaceAll(Utils::colorize);
		return coll;
	}

	public static String filterSpecialChars(final String str) {
		return str.replaceAll("[^a-zA-Z0-9 ]", "");
	}

	/**
	 * Gets a players ping with reflection.
	 *
	 * @param player
	 *            the player to get the ping from.
	 * @return the player's ping.
	 */
	public static int getPlayerPing(final Player player) {
		try {
			/* Let's retrieve the server's version. */
			final Pattern brand = Pattern.compile("(v|)[0-9][_.][0-9][_.][R0-9]*");
			final String pkg = Bukkit.getServer().getClass().getPackage().getName();
			final String version = pkg.substring(pkg.lastIndexOf('.') + 1);
			if (!brand.matcher(version).matches()) {
				/* The version didn't match our pattern ¯\_(ツ)_/¯ */
				return -1;
			}
			final Object cast = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer").cast(player);
			final Object customP = cast.getClass().getMethod("getHandle", new Class[0]).invoke(cast, new Object[0]);
			return customP.getClass().getField("ping").getInt(customP);
		} catch (Exception e) {
			/* Something went wrong ¯\_(ツ)_/¯ */
			e.printStackTrace();
			return -1;
		}

	}

	/**
	 * <p>
	 * Case insensitively replaces all occurrences of a String within another
	 * String.
	 * </p>
	 *
	 * <p>
	 * A {@code null} reference passed to this method is a no-op.
	 * </p>
	 *
	 * <pre>
	 * StringUtils.replaceIgnoreCase(null, *, *)        = null
	 * StringUtils.replaceIgnoreCase("", *, *)          = ""
	 * StringUtils.replaceIgnoreCase("any", null, *)    = "any"
	 * StringUtils.replaceIgnoreCase("any", *, null)    = "any"
	 * StringUtils.replaceIgnoreCase("any", "", *)      = "any"
	 * StringUtils.replaceIgnoreCase("aba", "a", null)  = "aba"
	 * StringUtils.replaceIgnoreCase("abA", "A", "")    = "b"
	 * StringUtils.replaceIgnoreCase("aba", "A", "z")   = "zbz"
	 * </pre>
	 *
	 * @param text
	 *            text to search and replace in, may be null
	 * @param searchString
	 *            the String to search for (case insensitive), may be null
	 * @param replacement
	 *            the String to replace it with, may be null
	 * @return a map containing non-null items, the map can be empty.
	 */
	public static Map<Integer, ItemStack> addItems(final Inventory inv, final ItemStack... items) {
		/* Filtering the items so as to prevent any NPE. */
		final ItemStack[] filteredItems = Stream.of(items).filter(Objects::nonNull)
				.filter(item -> item.getType() != Material.AIR).toArray(ItemStack[]::new);

		
		/*
		 * Attempt to add the items. Returns all items that couldn't fit the inventory.
		 * The map should be empty if all items were added successfuly.
		 */
		return inv.addItem(filteredItems);
	}

	/**
	 * <p>
	 * Case insensitively replaces all occurrences of a String within another
	 * String.
	 * </p>
	 *
	 * <p>
	 * A {@code null} reference passed to this method is a no-op.
	 * </p>
	 *
	 * <pre>
	 * StringUtils.replaceIgnoreCase(null, *, *)        = null
	 * StringUtils.replaceIgnoreCase("", *, *)          = ""
	 * StringUtils.replaceIgnoreCase("any", null, *)    = "any"
	 * StringUtils.replaceIgnoreCase("any", *, null)    = "any"
	 * StringUtils.replaceIgnoreCase("any", "", *)      = "any"
	 * StringUtils.replaceIgnoreCase("aba", "a", null)  = "aba"
	 * StringUtils.replaceIgnoreCase("abA", "A", "")    = "b"
	 * StringUtils.replaceIgnoreCase("aba", "A", "z")   = "zbz"
	 * </pre>
	 *
	 * @param text
	 *            text to search and replace in, may be null
	 * @param searchString
	 *            the String to search for (case insensitive), may be null
	 * @param replacement
	 *            the String to replace it with, may be null
	 * @return the text with any replacements processed, {@code null} if null String
	 *         input
	 */
	public static String replaceIgnoreCase(final String text, String searchString, final String replacement) {
		int max = -1;
		if (StringUtils.isEmpty(text) || StringUtils.isEmpty(searchString) || replacement == null || max == 0) {
			return text;
		}
		String searchText = text;
		searchText = text.toLowerCase();
		searchString = searchString.toLowerCase();
		int start = 0;
		int end = searchText.indexOf(searchString, start);
		if (end == -1) {
			return text;
		}
		final int replLength = searchString.length();
		int increase = replacement.length() - replLength;
		increase = increase < 0 ? 0 : increase;

		increase *= max < 0 ? 16 : max > 64 ? 64 : max;
		final StringBuilder buf = new StringBuilder(text.length() + increase);
		while (end != -1) {
			buf.append(text, start, end).append(replacement);
			start = end + replLength;
			if (--max == 0) {
				break;
			}
			end = searchText.indexOf(searchString, start);
		}
		buf.append(text, start, text.length());
		return buf.toString();
	}

	/**
	 * Attemps to get the item from the main hand of the player regardless of
	 * version. This is so as to avoid errors.
	 *
	 * @param player
	 *            the player to retrieve the item from.
	 * @return the item that the player is currently holding in their main hand. May
	 *         be null.
	 */
	public static ItemStack getMainItem(final Player player) {
		final String version = Bukkit.getVersion();

		@SuppressWarnings("deprecation")
		final ItemStack item = version.contains("1.8") || version.contains("1.7") ? player.getItemInHand()
				: player.getInventory().getItemInMainHand();
		return item == null || item.getType() == Material.AIR ? null : item;
	}

	/**
	 * Compares two ItemStacks and allows to either ignore or not durability
	 * comparison.
	 *
	 * @param stack
	 *            the ItemStack to compare.
	 * @param stack2
	 *            the second ItemStack to compare.
	 * @param durability
	 *            if durability should also be taken into consideration.
	 * @return whether or not the two itemstacks are similar.
	 */
	@SuppressWarnings("deprecation")
	public static boolean isSimilar(final ItemStack stack, final ItemStack stack2, final boolean durability) {
		final boolean a = stack2.getType() == stack.getType() && stack2.hasItemMeta() == stack.hasItemMeta()
				&& (stack2.hasItemMeta() ? Bukkit.getItemFactory().equals(stack2.getItemMeta(), stack.getItemMeta())
						: true);
		return durability ? a : a && stack2.getDurability() == stack.getDurability();
	}

	/**
	 * Geats all nearby blocks that are not AIR in a radius around the given
	 * location.
	 *
	 * @param location
	 *            the Location to make the scan.
	 * @param radius
	 *            the radius that this scan should reach to.
	 * @return all blocks retrieved from the scan.
	 */
	public static Set<Block> getNearbyBlocks(final Location location, final int radius) {
		final Set<Block> blocks = Sets.newHashSet();
		for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
			for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
				for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
					final Block br = location.getWorld().getBlockAt(x, y, z);
					if (br.getType() != Material.AIR) {
						blocks.add(br);
					}
				}
			}
		}
		return blocks;
	}

	public static String arrayToUpperCase(final String name, final String splitter) {
		if (!name.contains("_")) {
			return name;
		}
		final String[] split = StringUtils.splitByWholeSeparator(name, splitter);
		final StringBuilder buff = new StringBuilder();
		for (final String str : split) {
			buff.append(StringUtils.capitalize(str.toLowerCase()) + " ");
		}
		return buff.toString().substring(0, buff.toString().length() - 1);
	}

}
