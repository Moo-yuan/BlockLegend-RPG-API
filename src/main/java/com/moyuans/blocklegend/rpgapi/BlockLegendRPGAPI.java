package com.moyuans.blocklegend.rpgapi;

import com.moyuans.blocklegend.rpgapi.config.RPGConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import com.mojang.brigadier.context.CommandContext;

public class BlockLegendRPGAPI implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // 加载配置
        RPGConfig.HANDLER.load();

        // 初始化数据管理器
        RPGDataManager manager = RPGDataManager.getInstance();

        // 注册指令
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {

            // /debugHP - 显示客户端血量
            dispatcher.register(
                    com.mojang.brigadier.builder.LiteralArgumentBuilder.<FabricClientCommandSource>literal("debugHP")
                            .executes(context -> {
                                showClientHP(context);
                                return 1;
                            })
            );

            // /debugRPG - 显示 RPG 数据
            dispatcher.register(
                    com.mojang.brigadier.builder.LiteralArgumentBuilder.<FabricClientCommandSource>literal("debugRPG")
                            .executes(context -> {
                                showRPGData(context);
                                return 1;
                            })
                            // /debugRPG raw - 显示原始文本
                            .then(com.mojang.brigadier.builder.LiteralArgumentBuilder.<FabricClientCommandSource>literal("raw")
                                    .executes(context -> {
                                        showRawText(context);
                                        return 1;
                                    })
                            )
                            // /debugRPG reset - 重置统计
                            .then(com.mojang.brigadier.builder.LiteralArgumentBuilder.<FabricClientCommandSource>literal("reset")
                                    .executes(context -> {
                                        resetStats(context);
                                        return 1;
                                    })
                            )
            );

            // /debugRPGUI - 显示带特殊字符的原始格式
            dispatcher.register(
                    com.mojang.brigadier.builder.LiteralArgumentBuilder.<FabricClientCommandSource>literal("debugRPGUI")
                            .executes(context -> {
                                showRPGUI(context);
                                return 1;
                            })
            );
        });

        System.out.println("[BlockLegend RPG API] 已初始化");
    }

    /**
     * /debugHP - 显示客户端血量
     */
    private void showClientHP(CommandContext<FabricClientCommandSource> context) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            context.getSource().sendFeedback(Component.literal("§f§c玩家未加载"));
            return;
        }

        float health = mc.player.getHealth();
        float maxHealth = mc.player.getMaxHealth();

        context.getSource().sendFeedback(Component.literal("§f§b=== 客户端血量 ==="));
        context.getSource().sendFeedback(Component.literal("§f血量: §e" + health + " §7/ §e" + maxHealth));
        context.getSource().sendFeedback(Component.literal("§f百分比: §e" + String.format("%.1f", (health / maxHealth) * 100) + "%"));

        // 如果有 RPG 数据，也显示
        RPGDataManager manager = RPGDataManager.getInstance();
        if (manager.hasValidData()) {
            context.getSource().sendFeedback(Component.literal("§f§b=== 服务器真实血量 ==="));
            context.getSource().sendFeedback(Component.literal("§f血量: §e" + manager.getRealHealth() + " §7/ §e" + manager.getRealMaxHealth()));
        }

        context.getSource().sendFeedback(Component.literal("§f§b==================="));
    }

    /**
     * /debugRPG - 显示解析后的 RPG 数据
     * 图标对应表（按 BossBar 原始顺序）：
     * 鄿(U+913F)=攻击伤害 竴(U+7AF4)=暴击几率 鳵(U+9CF5)=暴击伤害
     * 鐖(U+9416)=基础防御 馸(U+99B8)=血量 灋(U+704B)=技能伤害
     * 嬫(U+5B2B)=技能暴击率 戞(U+621E)=技能暴击伤害 斝(U+659D)=招架几率
     * 盪(U+76EA)=格挡几率 閄(U+9584)=闪避几率
     */
    private void showRPGData(CommandContext<FabricClientCommandSource> context) {
        RPGDataManager manager = RPGDataManager.getInstance();
        RPGStats stats = manager.getCurrentStats();

        context.getSource().sendFeedback(Component.literal("§f§b=== RPG 属性数据 ==="));

        if (!manager.hasValidData()) {
            context.getSource().sendFeedback(Component.literal("§f§c无有效数据"));
            context.getSource().sendFeedback(Component.literal("§f§7提示: 进入服务器，等待屏幕上方显示 RPG 属性"));
            context.getSource().sendFeedback(Component.literal("§f§b==================="));
            return;
        }

        // 鄿 = 攻击伤害 (U+913F)
        context.getSource().sendFeedback(Component.literal("§f鄿 攻击伤害: §e" + stats.getAttackDamage()));
        // 竴 = 暴击几率 (U+7AF4)
        context.getSource().sendFeedback(Component.literal("§f竴 暴击几率: §e" + stats.getCritChance()));
        // 鳵 = 暴击伤害 (U+9CF5)
        context.getSource().sendFeedback(Component.literal("§f鳵 暴击伤害: §e" + stats.getCritDamage()));
        // 鐖 = 基础防御 (U+9416)
        context.getSource().sendFeedback(Component.literal("§f鐖 基础防御: §e" + stats.getBaseDefense()));
        // 馸 = 血量 (U+99B8)
        context.getSource().sendFeedback(Component.literal("§f馸 血量: §e" + stats.getCurrentHealth() + "§7/§e" + stats.getMaxHealth()));
        // 灋 = 技能伤害 (U+704B)
        context.getSource().sendFeedback(Component.literal("§f灋 技能伤害: §e" + stats.getSkillDamage()));
        // 嬫 = 技能暴击率 (U+5B2B)
        context.getSource().sendFeedback(Component.literal("§f嬫 技能暴击率: §e" + stats.getSkillCritChance()));
        // 戞 = 技能暴击伤害 (U+621E)
        context.getSource().sendFeedback(Component.literal("§f戞 技能暴击伤害: §e" + stats.getSkillCritDamage()));
        // 斝 = 招架几率 (U+659D)
        context.getSource().sendFeedback(Component.literal("§f斝 招架几率: §e" + stats.getParryChance()));
        // 盪 = 格挡几率 (U+76EA)
        context.getSource().sendFeedback(Component.literal("§f盪 格挡几率: §e" + stats.getBlockChance()));
        // 閄 = 闪避几率 (U+9584)
        context.getSource().sendFeedback(Component.literal("§f閄 闪避几率: §e" + stats.getDodgeChance()));

        context.getSource().sendFeedback(Component.literal("§f§b==================="));
    }

    /**
     * /debugRPG raw - 显示原始 Boss Bar 文本（带颜色代码）
     */
    private void showRawText(CommandContext<FabricClientCommandSource> context) {
        RPGDataManager manager = RPGDataManager.getInstance();
        String rawText = manager.getLastRawText();

        context.getSource().sendFeedback(Component.literal("§f§b=== 原始 Boss Bar 文本 ==="));

        if (rawText == null || rawText.isEmpty()) {
            context.getSource().sendFeedback(Component.literal("§f§c尚未接收到 Boss Bar 数据"));
            context.getSource().sendFeedback(Component.literal("§f§7提示: 进入服务器后，等待屏幕上方显示 RPG 属性"));
        } else {
            // 直接显示原始文本（包含颜色代码和特殊字符）
            context.getSource().sendFeedback(Component.literal("§f" + rawText));
        }

        context.getSource().sendFeedback(Component.literal("§f§b==================="));
    }

    /**
     * /debugRPG reset - 重置统计计数器
     */
    private void resetStats(CommandContext<FabricClientCommandSource> context) {
        RPGDataManager.getInstance().resetStats();
        context.getSource().sendFeedback(Component.literal("§f§a统计计数器已重置"));
    }

    /**
     * /debugRPGUI - 显示带特殊字符的原始格式
     */
    private void showRPGUI(CommandContext<FabricClientCommandSource> context) {
        RPGDataManager manager = RPGDataManager.getInstance();
        String rawText = manager.getLastRawText();

        context.getSource().sendFeedback(Component.literal("§f§b=== RPG 原始格式 ==="));

        if (rawText == null || rawText.isEmpty()) {
            context.getSource().sendFeedback(Component.literal("§f§c尚未接收到 Boss Bar 数据"));
        } else {
            // 显示带特殊字符的格式（如 鄿7.9 竴0 鳵200...）
            // 移除颜色代码但保留特殊字符
            String uiText = rawText.replaceAll("§[0-9a-fk-or]", "");
            context.getSource().sendFeedback(Component.literal("§f" + uiText));
        }

        context.getSource().sendFeedback(Component.literal("§f§b==================="));
    }
}