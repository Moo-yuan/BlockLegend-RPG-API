package com.moyuans.blocklegend.rpgapi;

public class BossBarParser {

    public static RPGStats parse(String text) {
        RPGStats stats = new RPGStats();
        stats.setRawText(text);

        if (text == null || text.isEmpty()) {
            stats.setValid(false);
            return stats;
        }

        try {
            String cleanText = text.replaceAll("§[0-9a-fk-or]", "");
            String[] parts = cleanText.split("\s+");

            int index = 0;
            for (String part : parts) {
                if (part.isEmpty()) continue;

                if (containsIcon(part)) {
                    String valueStr = extractNumber(part);

                    switch (index) {
                        case 0: stats.setAttackDamage(parseFloat(valueStr)); break;
                        case 1: stats.setCritChance(parseFloat(valueStr)); break;
                        case 2: stats.setCritDamage(parseFloat(valueStr)); break;
                        case 3: stats.setBaseDefense(parseFloat(valueStr)); break;
                        case 4: parseHealth(valueStr, stats); break;
                        case 5: stats.setSkillDamage(parseFloat(valueStr)); break;
                        case 6: stats.setSkillCritChance(parseFloat(valueStr)); break;
                        case 7: stats.setSkillCritDamage(parseFloat(valueStr)); break;
                        case 8: stats.setParryChance(parseFloat(valueStr)); break;
                        case 9: stats.setBlockChance(parseFloat(valueStr)); break;
                    }
                    index++;
                } else if (isNumber(part) && index > 0) {
                    stats.setDodgeChance(parseFloat(part));
                }
            }

            stats.setValid(stats.getMaxHealth() > 0);

        } catch (Exception e) {
            stats.setValid(false);
        }

        return stats;
    }

    private static boolean containsIcon(String text) {
        return text.contains("鄿") || text.contains("竴") || text.contains("鳵") ||
                text.contains("鐖") || text.contains("馸") || text.contains("灋") ||
                text.contains("嬫") || text.contains("戞") || text.contains("斝") ||
                text.contains("盪") || text.contains("閄") || text.contains("❤");
    }

    private static String extractNumber(String text) {
        StringBuilder result = new StringBuilder();
        boolean foundDigit = false;

        for (char c : text.toCharArray()) {
            if (c > 127) continue;
            if (Character.isDigit(c) || c == '.' || c == '/') {
                result.append(c);
                foundDigit = true;
            }
        }

        return foundDigit ? result.toString() : "";
    }

    private static void parseHealth(String text, RPGStats stats) {
        if (text.contains("/")) {
            String[] parts = text.split("/");
            if (parts.length == 2) {
                stats.setCurrentHealth(parseFloat(parts[0]));
                stats.setMaxHealth(parseFloat(parts[1]));
            }
        } else {
            float value = parseFloat(text);
            stats.setCurrentHealth(value);
            stats.setMaxHealth(value);
        }
    }

    private static float parseFloat(String text) {
        try {
            return Float.parseFloat(text);
        } catch (NumberFormatException e) {
            return 0f;
        }
    }

    private static boolean isNumber(String text) {
        try {
            Float.parseFloat(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
