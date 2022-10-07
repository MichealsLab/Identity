package it.myke.identity.utils;


import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class FormatUtils {

    private static final Set<Character> colorChars = "4c6e2ab319d5f780rlonmk".chars().mapToObj(i -> (char) i).collect(Collectors.toSet());

    /**
     * Shorthand for {@link ChatColor#translateAlternateColorCodes(char, String)} with the option to format hex color codes
     * @param input The input string
     * @return The colored string, replacing color codes using ampersands with proper codes
     */
    public static String color(String input) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (i + 1 >= input.length()) {
                builder.append(c);
                continue;
            }
            char n = input.charAt(i + 1);
            if (c == '\\' && (n == '&' || n == '\\')) {
                i++;
                builder.append(n);
                continue;
            }
            if (c != '&') {
                builder.append(c);
                continue;
            }
            if (colorChars.contains(n)) {
                builder.append(ChatColor.COLOR_CHAR);
                continue;
            }
            if (n == '#' && i + 7 <= input.length()) {
                String hexCode = input.substring(i + 2, i + 8).toUpperCase(Locale.ROOT);
                if (hexCode.chars().allMatch(ch -> (ch <= '9' && ch >= '0') || (ch <= 'F' && ch >= 'A'))) {
                    hexCode = Arrays.stream(hexCode.split("")).map(s -> ChatColor.COLOR_CHAR + s).collect(Collectors.joining());
                    builder.append(ChatColor.COLOR_CHAR).append("x").append(hexCode);
                    i += 7;
                    continue;
                }
            }
            builder.append(c);
        }
        return builder.toString();
    }


    public static List<String> color(List<String> input) {
        return input.stream().map(FormatUtils::color).collect(Collectors.toList());
    }


    public static void sendActionbar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(FormatUtils.color(message)));
    }


    public static List<String> replaceList(List<String> list, String key, String value) {
        return list.stream().map(s -> s.replace(key, value)).collect(Collectors.toList());
    }


    public static String loreListToSingleString(List<String> lore) {
        String loreFinal = "";
        for(String loreLine : lore) {
            loreFinal = loreLine + "\n";
        }
        return loreFinal;
    }

    public static String firstUppercase(String s) {
        char first = Character.toUpperCase(s.charAt(0));
        return first + s.substring(1);
    }

}