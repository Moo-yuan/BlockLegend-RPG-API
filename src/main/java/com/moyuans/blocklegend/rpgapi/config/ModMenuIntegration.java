package com.moyuans.blocklegend.rpgapi.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.minecraft.network.chat.Component;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> YetAnotherConfigLib.create(RPGConfig.HANDLER, (defaults, config, builder) -> {

            Option<Boolean> showChatMessages = Option.<Boolean>createBuilder()
                    .name(Component.literal("聊天栏显示更新"))
                    .description(OptionDescription.of(Component.literal("当 RPG 数据更新时，是否在聊天栏显示消息")))
                    .binding(defaults.showChatMessages, () -> config.showChatMessages, newVal -> config.showChatMessages = newVal)
                    .controller(BooleanControllerBuilder::create)
                    .build();

            Option<Boolean> debugMode = Option.<Boolean>createBuilder()
                    .name(Component.literal("调试模式"))
                    .description(OptionDescription.of(Component.literal("启用调试模式，显示 BossBar 解析日志")))
                    .binding(defaults.debugMode, () -> config.debugMode, newVal -> config.debugMode = newVal)
                    .controller(BooleanControllerBuilder::create)
                    .build();

            ConfigCategory general = ConfigCategory.createBuilder()
                    .name(Component.literal("通用设置"))
                    .option(showChatMessages)
                    .option(debugMode)
                    .build();

            return builder
                    .title(Component.literal("BlockLegend RPG API 配置"))
                    .category(general)
                    .save(RPGConfig.HANDLER::save);
        }).generateScreen(parent);
    }
}