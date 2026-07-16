package com.moyuans.blocklegend.rpgapi;

import com.moyuans.blocklegend.rpgapi.config.RPGConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class RPGDataManager {

    private static final long DATA_TIMEOUT_MS = 3000;
    private static RPGDataManager INSTANCE;

    private RPGStats currentStats = new RPGStats();
    private Consumer<RPGStats> listener;
    private Field eventsField;
    private String lastBossBarText = "";
    private String lastRawText = "";
    private int parseSuccessCount = 0;
    private int parseFailCount = 0;
    private long lastUpdateTime = 0;
    private boolean wasValid = false;

    private RPGDataManager() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            if (eventsField == null) {
                initReflection();
            }

            if (eventsField != null) {
                checkBossBarUpdates();
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            clearData();
        });
    }

    public static RPGDataManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RPGDataManager();
        }
        return INSTANCE;
    }

    private void initReflection() {
        try {
            eventsField = net.minecraft.client.gui.components.BossHealthOverlay.class.getDeclaredField("events");
            eventsField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            Field[] fields = net.minecraft.client.gui.components.BossHealthOverlay.class.getDeclaredFields();
            for (Field field : fields) {
                if (field.getType() == Map.class) {
                    eventsField = field;
                    eventsField.setAccessible(true);
                    break;
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void checkBossBarUpdates() {
        try {
            Minecraft mc = Minecraft.getInstance();
            Object overlay = mc.gui.getBossOverlay();

            Map<UUID, LerpingBossEvent> events = (Map<UUID, LerpingBossEvent>) eventsField.get(overlay);

            if (events == null || events.isEmpty()) {
                return;
            }

            boolean foundRPGBar = false;
            for (Map.Entry<UUID, LerpingBossEvent> entry : events.entrySet()) {
                LerpingBossEvent event = entry.getValue();
                String text = event.getName().getString();
                lastRawText = text;

                if (text.contains("馸") || text.contains("❤")) {
                    foundRPGBar = true;
                    lastUpdateTime = System.currentTimeMillis();
                    wasValid = true;

                    if (!text.equals(lastBossBarText)) {
                        lastBossBarText = text;
                        RPGStats stats = BossBarParser.parse(text);

                        if (stats.isValid()) {
                            currentStats = stats;
                            parseSuccessCount++;

                            if (RPGConfig.HANDLER.instance().showChatMessages) {
                                sendChatMessage("§a[RPG API] 血量更新: " + stats.getCurrentHealth() + "/" + stats.getMaxHealth());
                            }

                            if (listener != null) {
                                listener.accept(stats);
                            }
                        } else {
                            parseFailCount++;
                        }
                    }
                    break;
                }
            }

            if (!foundRPGBar) {
                if (wasValid && lastUpdateTime > 0) {
                    long elapsed = System.currentTimeMillis() - lastUpdateTime;
                    if (elapsed > DATA_TIMEOUT_MS) {
                        clearData();
                        if (RPGConfig.HANDLER.instance().debugMode) {
                            sendChatMessage("§e[RPG API] 已离开 RPG 服务器，数据清除");
                        }
                    }
                }
            }

        } catch (Exception e) {
            // 忽略错误
        }
    }

    public void clearData() {
        currentStats = new RPGStats();
        lastBossBarText = "";
        lastUpdateTime = 0;
        wasValid = false;
    }

    private void sendChatMessage(String message) {
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                mc.player.sendSystemMessage(Component.literal(message));
            }
        } catch (Exception e) {
            // 忽略
        }
    }

    public void addListener(Consumer<RPGStats> listener) {
        this.listener = listener;
    }

    public RPGStats getCurrentStats() {
        return currentStats;
    }

    public float getRealHealth() {
        return currentStats.isValid() ? currentStats.getCurrentHealth() : 0;
    }

    public float getRealMaxHealth() {
        return currentStats.isValid() ? currentStats.getMaxHealth() : 0;
    }

    public boolean hasValidData() {
        if (!currentStats.isValid() || !wasValid) return false;
        if (lastUpdateTime == 0) return false;
        long elapsed = System.currentTimeMillis() - lastUpdateTime;
        return elapsed <= DATA_TIMEOUT_MS;
    }

    public String getLastRawText() {
        return lastRawText;
    }

    public String getLastBossBarText() {
        return lastBossBarText;
    }

    public int getParseSuccessCount() {
        return parseSuccessCount;
    }

    public int getParseFailCount() {
        return parseFailCount;
    }

    public void resetStats() {
        parseSuccessCount = 0;
        parseFailCount = 0;
    }
}