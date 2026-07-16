# BlockLegend RPG API - 调用文档

## 模组信息

| 项目 | 内容 |
|------|------|
| **模组 ID** | `blocklegend-rpg-api` |
| **版本** | 1.0.0 |
| **Minecraft 版本** | 1.20.4 |
| **加载器** | Fabric |
| **作者** | Moyuans |

---

## 前置依赖

在你的 `build.gradle` 中添加：

```gradle
repositories {
    // 如果有自定义 maven 仓库，添加在这里
}

dependencies {
    modImplementation "com.moyuans.blocklegend:blocklegend-rpg-api:1.0.0"
}
```

---

## 核心类

### 1. RPGDataManager（数据管理器）

**单例模式**，负责监听和提供 RPG 数据。

```java
import com.moyuans.blocklegend.rpgapi.RPGDataManager;
import com.moyuans.blocklegend.rpgapi.RPGStats;

// 获取实例
RPGDataManager manager = RPGDataManager.getInstance();
```

#### 方法列表

| 方法 | 返回值 | 说明 |
|------|--------|------|
| `getInstance()` | `RPGDataManager` | 获取单例实例 |
| `getCurrentStats()` | `RPGStats` | 获取当前的 RPG 数据 |
| `getRealHealth()` | `float` | 获取真实当前血量（服务器数据） |
| `getRealMaxHealth()` | `float` | 获取真实最大血量（服务器数据） |
| `hasValidData()` | `boolean` | 是否有有效的 RPG 数据 |
| `getLastRawText()` | `String` | 获取最后接收到的原始 Boss Bar 文本 |
| `getLastBossBarText()` | `String` | 获取最后解析的 Boss Bar 文本 |
| `getParseSuccessCount()` | `int` | 获取解析成功次数 |
| `getParseFailCount()` | `int` | 获取解析失败次数 |
| `resetStats()` | `void` | 重置统计计数器 |
| `addListener(Consumer<RPGStats>)` | `void` | 添加数据更新监听器 |

---

### 2. RPGStats（数据类）

存储解析后的 RPG 属性数据。

```java
import com.moyuans.blocklegend.rpgapi.RPGStats;

RPGStats stats = RPGDataManager.getInstance().getCurrentStats();
```

#### 属性列表

| 方法 | 返回值 | 说明 | 图标 |
|------|--------|------|------|
| `getAttackDamage()` | `float` | 攻击伤害 | 鄿 |
| `getCritChance()` | `float` | 暴击几率 | 竴 |
| `getCritDamage()` | `float` | 暴击伤害 | 鳵 |
| `getBaseDefense()` | `float` | 基础防御 | 鐖 |
| `getCurrentHealth()` | `float` | 当前血量 | 馸/❤ |
| `getMaxHealth()` | `float` | 最大血量 | 馸/❤ |
| `getSkillDamage()` | `float` | 技能伤害 | 灋 |
| `getSkillCritChance()` | `float` | 技能暴击率 | 嬫 |
| `getSkillCritDamage()` | `float` | 技能暴击伤害 | 戞 |
| `getParryChance()` | `float` | 招架几率 | 斝 |
| `getBlockChance()` | `float` | 格挡几率 | 盪 |
| `getDodgeChance()` | `float` | 闪避几率 | (无图标) |
| `getRawText()` | `String` | 原始 Boss Bar 文本 | - |
| `isValid()` | `boolean` | 数据是否有效 | - |

---

## 使用示例

### 示例 1：获取真实血量（ClassicBar 等血条模组）

```java
import com.moyuans.blocklegend.rpgapi.RPGDataManager;

public class MyHealthBarMod {

    public void renderHealthBar(Player player) {
        float health;
        float maxHealth;

        // 检查是否有 RPG 数据
        if (RPGDataManager.getInstance().hasValidData()) {
            // 使用服务器真实血量
            health = RPGDataManager.getInstance().getRealHealth();
            maxHealth = RPGDataManager.getInstance().getRealMaxHealth();
        } else {
            // 使用客户端默认血量
            health = player.getHealth();
            maxHealth = player.getMaxHealth();
        }

        // 渲染血条...
        renderBar(health, maxHealth);
    }
}
```

### 示例 2：监听数据更新

```java
import com.moyuans.blocklegend.rpgapi.RPGDataManager;
import com.moyuans.blocklegend.rpgapi.RPGStats;

public class MyMod {

    public void onInitializeClient() {
        // 注册监听器，当 RPG 数据更新时自动调用
        RPGDataManager.getInstance().addListener(stats -> {
            System.out.println("血量更新: " + stats.getCurrentHealth() + "/" + stats.getMaxHealth());
            System.out.println("攻击伤害: " + stats.getAttackDamage());
            // ... 其他属性
        });
    }
}
```

### 示例 3：获取所有属性

```java
import com.moyuans.blocklegend.rpgapi.RPGDataManager;
import com.moyuans.blocklegend.rpgapi.RPGStats;

public class MyHUDMod {

    public void renderHUD() {
        RPGStats stats = RPGDataManager.getInstance().getCurrentStats();

        if (!stats.isValid()) {
            return; // 没有有效数据
        }

        // 显示所有属性
        drawText("攻击: " + stats.getAttackDamage());
        drawText("防御: " + stats.getBaseDefense());
        drawText("血量: " + stats.getCurrentHealth() + "/" + stats.getMaxHealth());
        drawText("技能伤害: " + stats.getSkillDamage());
        // ... 其他属性
    }
}
```

---

## 数据格式说明

### Boss Bar 原始文本格式

```
§e鄿7.9 竴0 鳵200 鐖115.3 馸95/95 灋154.2 嬫48.1 戞102.8 斝0 盪0.6 0
```

### 解析后数据

| 属性 | 值 | 图标 |
|------|-----|------|
| 攻击伤害 | 7.9 | 鄿 |
| 暴击几率 | 0 | 竴 |
| 暴击伤害 | 200 | 鳵 |
| 基础防御 | 115.3 | 鐖 |
| 当前血量 | 95 | 馸/❤ |
| 最大血量 | 95 | 馸/❤ |
| 技能伤害 | 154.2 | 灋 |
| 技能暴击率 | 48.1 | 嬫 |
| 技能暴击伤害 | 102.8 | 戞 |
| 招架几率 | 0 | 斝 |
| 格挡几率 | 0.6 | 盪 |
| 闪避几率 | 0 | (无图标) |

---

## 图标对照表

所有图标都是**材质包自定义的 Unicode 字符**（`§e` 黄色）。

| 图标 | Unicode | 属性 | 说明 |
|------|---------|------|------|
| 鄿 | U+90BF | 攻击伤害 | 物理攻击基础伤害 |
| 竴 | U+7AF4 | 暴击几率 | 物理暴击概率 |
| 鳵 | U+9CF5 | 暴击伤害 | 物理暴击伤害倍率 |
| 鐖 | U+9416 | 基础防御 | 物理防御值 |
| 馸 | U+99B8 | 血量 | 当前血量/最大血量 |
| ❤ | U+2764 | 血量 | 备用血量图标 |
| 灋 | U+704B | 技能伤害 | 技能基础伤害 |
| 嬫 | U+5B2B | 技能暴击率 | 技能暴击概率 |
| 戞 | U+621E | 技能暴击伤害 | 技能暴击伤害倍率 |
| 斝 | U+659D | 招架几率 | 招架攻击概率 |
| 盪 | U+76EA | 格挡几率 | 格挡攻击概率 |
| (无) | - | 闪避几率 | 闪避攻击概率（最后一个数字） |

---

## 指令列表（调试用途）

| 指令 | 功能 |
|------|------|
| `/debugHP` | 显示客户端血量和服务器真实血量 |
| `/debugRPG` | 显示解析后的 RPG 属性数据 |
| `/debugRPG raw` | 显示原始 Boss Bar 文本（带颜色代码） |
| `/debugRPG reset` | 重置解析统计计数器 |
| `/debugRPGUI` | 显示带特殊字符的原始格式 |

---

## 配置选项（ModMenu）

按 `Esc` → `模组` → `BlockLegend RPG API` → `配置`

| 选项 | 默认值 | 说明 |
|------|--------|------|
| 聊天栏显示更新 | `false` | 当 RPG 数据更新时，是否在聊天栏显示消息 |
| 调试模式 | `false` | 启用调试模式，显示更多日志信息 |

---

## 常见问题

### Q: 为什么获取不到数据？

**A:** 检查以下几点：
1. 是否在服务器中（单人世界无效）
2. 屏幕上方是否显示 RPG 属性（如 ❤ 95/95）
3. 模组是否正确加载（按 F3 查看右上角）

### Q: 数据为什么不更新？

**A:** 数据每 tick（20次/秒）检查一次，只有 Boss Bar 文本变化时才会更新。如果血量变化但 Boss Bar 文本没变，数据不会更新。

### Q: UUID 变化会影响功能吗？

**A:** 不会。本 API 不依赖 UUID，只通过文本内容识别（包含 ❤ 或 馸 图标）。

### Q: 如何在自己的模组中使用？

**A:** 
1. 在 `build.gradle` 中添加依赖
2. 使用 `RPGDataManager.getInstance()` 获取数据
3. 参考上面的使用示例

---

## 技术实现

### 数据来源
- 服务器通过 `ClientboundBossEventPacket` 发送 Boss Bar 数据
- 客户端存储在 `BossHealthOverlay.events` 字段（`Map<UUID, LerpingBossEvent>`）
- 本 API 通过 Java 反射直接读取该内部数据结构

### 监听机制
- 使用 `ClientTickEvents.END_CLIENT_TICK` 每 tick 检查
- 遍历 `BossHealthOverlay.events` 中的所有 Boss Bar
- 查找包含血量图标（❤ 或 馸）的 Boss Bar
- 文本变化时触发解析和回调

### 解析逻辑
1. 移除颜色代码（`§e`, `§f` 等）
2. 按空格分割文本
3. 识别 Unicode 图标字符（ASCII 范围外）
4. 提取图标后的数字
5. 特殊处理血量（格式：`95/95`）
6. 最后一个纯数字 = 闪避几率

---

## 版本历史

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| 1.0.0 | 2026-06-27 | 初始版本，支持基础 RPG 属性读取 |

---

## 作者信息

- **作者**: Moyuans
- **项目**: BlockLegend RPG API
- **用途**: 为 Minecraft Fabric 1.20.4 提供服务器 RPG 属性读取 API

---

*本文档由 AI 生成，如有问题请联系作者。*
