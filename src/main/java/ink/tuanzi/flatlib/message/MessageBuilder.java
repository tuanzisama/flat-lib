package ink.tuanzi.flatlib.message;

import de.themoep.minedown.MineDown;
import de.tr7zw.nbtapi.NBTItem;
import ink.tuanzi.flatlib.bukkit.ItemStackUtil;
import ink.tuanzi.flatlib.message.vendor.InventoryViewer;
import ink.tuanzi.flatlib.text.ColorUtil;
import net.md_5.bungee.api.chat.*;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

/**
 * 消息构造器
 * 使用链式表达式，简化 MineDown 的使用成本
 * <a href="https://github.com/Phoenix616/MineDown">MineDown docs</a>
 */
@SuppressWarnings("unchecked")
public class MessageBuilder<T extends MessageBuilder<T>> {

    protected final ComponentBuilder componentBuilder = new ComponentBuilder("");
    private MessageLevel messageLevel = MessageLevel.NONE;

    public MessageBuilder() {
    }

    public MessageBuilder(BaseComponent[] baseComponents) {
        this.componentBuilder.append(baseComponents);
    }

    public MessageBuilder(MessageLevel level) {
        messageLevel = level;
    }

    /**
     * 追加文字 (Minedown)
     *
     * @param str 文字
     * @return {@code this}.
     */
    public T append(String str) {
        append(str, true, ComponentBuilder.FormatRetention.NONE);
        return (T) this;
    }

    /**
     * 追加 BaseComponents
     *
     * @param baseComponents BaseComponent
     * @return {@code this}
     */
    public T appendBaseComponents(BaseComponent[] baseComponents) {
        this.componentBuilder.append(baseComponents);
        return (T) this;
    }

    /**
     * 追加其他 MessageBuilder
     *
     * @param msgBuilder MessageBuilder 实例
     * @return {@code this}.
     */
    public T appendBuilder(T msgBuilder) {
        componentBuilder.append(msgBuilder.componentBuilder.create(), ComponentBuilder.FormatRetention.NONE);
        return (T) this;
    }

    /**
     * 追加文字 (非Minedown)
     *
     * @param str    文字
     * @param format 是否转义颜色
     * @return {@code this}.
     */
    public T appendText(String str, boolean format) {
        String content = str;
        if (format) {
            content = ColorUtil.parse(str);
        }
        append(content, false, ComponentBuilder.FormatRetention.NONE);
        return (T) this;
    }

    /**
     * 追加文字
     *
     * @param str             文字
     * @param isMineDown      是否为 Minedown 语法
     * @param formatRetention 格式保留。简而言之，是否继承上一个文字组件的样式行为。
     * @return {@code this}.
     */
    public T append(String str, boolean isMineDown, ComponentBuilder.FormatRetention formatRetention) {
        if (isMineDown) {
            componentBuilder.append(MineDown.parse(messageLevel.getChatColor() + str), formatRetention);
        } else {
            componentBuilder.append(TextComponent.fromLegacyText(str), formatRetention);
        }
        return (T) this;
    }

    /**
     * 添加一个生物实体
     *
     * @param entity 生物实体
     * @param <E>    继承自 Entity 的实体
     * @return {@code this}.
     */
    public <E extends Entity> T appendEntity(E entity) {
        if (EntityType.PLAYER.equals(entity.getType())) {
            this.appendPlayer((Player) entity);
            return (T) this;
        }

        String entityStr = "[&#FC8BAB&[{name}&#FC8BAB&]&r](show_entity={uuid}:{type} {name})"
                .replace("{name}", Objects.requireNonNullElse(entity.getCustomName(), entity.getName()))
                .replace("{uuid}", entity.getUniqueId().toString())
                .replace("{type}", entity.getType().name().toLowerCase());
        componentBuilder.append(MineDown.parse(entityStr), ComponentBuilder.FormatRetention.NONE);
        return (T) this;
    }

    /**
     * 添加一个玩家
     *
     * @param player 玩家
     * @param <P>    继承自 OfflinePlayer 的玩家
     * @return {@code this}.
     */
    public <P extends OfflinePlayer> T appendPlayer(P player) {
        String entityStr = "[&#64FFDA&[{name}&#64FFDA&]&r](show_entity={uuid}:minecraft:player {name})"
                .replace("{name}", Objects.requireNonNullElse(player.getName(), player.getName()))
                .replace("{uuid}", player.getUniqueId().toString());
        componentBuilder.append(MineDown.parse(entityStr), ComponentBuilder.FormatRetention.NONE);
        return (T) this;
    }

    /**
     * 添加可悬浮字
     *
     * @param label 显示文字
     * @param text  悬浮的文字
     * @return {@code this}.
     */
    public T appendHover(String label, String text) {
        String minedownStr = "[&r{label}](show_text={text})".replace("{label}", label).replace("{text}", text);
        append(minedownStr, true, ComponentBuilder.FormatRetention.NONE);
        return (T) this;
    }

    /**
     * 添加点击式执行指令
     *
     * @param command 指令
     * @return {@code this}.
     */
    public T appendCommand(String label, String command) {
        return appendCommand(label, command, "点击执行此指令");
    }

    /**
     * 添加点击式执行指令
     *
     * @param command   指令
     * @param hoverText 悬浮在指令上的文字
     * @return {@code this}.
     */
    public T appendCommand(String label, String command, String hoverText) {
        String cmd = command.startsWith("/") ? command : "/" + command;

        String minedownStr = "[&#FFA500&&r{label}&r](show_text={text} run_command={cmd})".replace("{label}", label).replace("{text}", hoverText).replace("{cmd}", cmd);
        append(minedownStr, true, ComponentBuilder.FormatRetention.NONE);
        return (T) this;
    }

    /**
     * 添加一个物品堆 (ItemStack)
     *
     * @param itemStack     物品堆
     * @param enabledViewer 启用库存预览
     * @return {@code this}.
     */
    public T appendItemStack(ItemStack itemStack, boolean enabledViewer) {
        String namespaceKey = itemStack.getType().toString();

//        String displayName = LocaleManager.item.getValue(namespaceKey);
        String displayName = namespaceKey;
        ItemMeta imeta = itemStack.getItemMeta();
        if (imeta != null) {
            String nameFromMeta = itemStack.getItemMeta().getDisplayName();
            if (!StringUtils.isEmpty(nameFromMeta)) {
                displayName = nameFromMeta;
            }
        }

        if (itemStack.getAmount() > 1) {
            displayName += "&7×" + itemStack.getAmount();
        }

        BaseComponent[] tooltipComp = getItemTooltipComponent(String.format("&b[%s&b]&r", displayName), itemStack, namespaceKey.toLowerCase(), enabledViewer);
        componentBuilder.append(tooltipComp);
        return (T) this;
    }

    /**
     * 添加一个物品堆 (ItemStack)
     *
     * @param itemStack 物品堆
     * @return {@code this}.
     */
    public T appendItemStack(ItemStack itemStack) {
        return appendItemStack(itemStack, false);
    }

    /**
     * 添加一个方块 (ItemStack)
     *
     * @param block 方块
     * @return {@code this}.
     */
    public T appendBlock(Block block) {
        append(String.format("[&b[%s&b]](hover=x: %s, y: %s, z: %s, world: %s)", block.getX(), block.getY(), block.getZ(), block.getWorld().getName()), true, ComponentBuilder.FormatRetention.NONE).reset();
        return (T) this;
    }

    public T appendLink(String title, String url, String description) {
        append(String.format("[&b[%s&b]](open_url=%s show_text=%s)", title, url, description), true, ComponentBuilder.FormatRetention.NONE).reset();
        return (T) this;
    }

    /**
     * 添加一个空格。主要用于间距。
     *
     * @return {@code this}.
     */
    public T space() {
        this.reset().append(" ", true, ComponentBuilder.FormatRetention.NONE);
        return (T) this;
    }

    /**
     * 添加一个回车。主要用于间距。
     *
     * @return {@code this}.
     */
    public T enter() {
        this.reset().append("\n", true, ComponentBuilder.FormatRetention.NONE);
        return (T) this;
    }

    /**
     * 添加一个样式重置字符
     *
     * @return {@code this}.
     */
    public T reset() {
        append(String.valueOf(ChatColor.RESET), true, ComponentBuilder.FormatRetention.NONE);
        return (T) this;
    }

    /**
     * 普通信息颜色
     *
     * @return {@code this}.
     */
    public T info() {
        messageLevel = MessageLevel.INFO;
        return (T) this;
    }

    /**
     * 警告信息颜色
     *
     * @return {@code this}.
     */
    public T warn() {
        messageLevel = MessageLevel.WARN;
        return (T) this;
    }

    /**
     * 警告信息颜色
     *
     * @return {@code this}.
     */
    public T success() {
        messageLevel = MessageLevel.SUCCESS;
        return (T) this;
    }

    public MessageBuilder<T> clone() {
        return new MessageBuilder<>(this.componentBuilder.create());
    }

    /**
     * 构建 消息构造器
     *
     * @return BaseComponent[]
     */
    public BaseComponent[] build() {
        return this.componentBuilder.create();
    }

    /**
     * 物品对提示框组件
     *
     * @param message       消息
     * @param itemStack     物品怼
     * @param id            物品ID
     * @param enabledViewer 启用库存预览
     */
    private static BaseComponent[] getItemTooltipComponent(String message, ItemStack itemStack, String id, boolean enabledViewer) {
        NBTItem nbti = new NBTItem(itemStack);
        String itemJson = nbti.toString();

        // Prepare a BaseComponent array with the itemJson as a text component
        BaseComponent[] hoverEventComponents = new BaseComponent[]{
                new TextComponent(itemJson) // The only element of the hover events baseComponents is the item json
        };
        // Create the hover event
//        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_ITEM, new Item(id, itemStack.getAmount(), ItemTag.ofNbt(itemJson)));
        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents);

        /* And now we create the text component (this is the actual text that the player sees)
         * and set it's hover event to the item event */
//        TextComponent component = new TextComponent(ColorUtil.parse(message));
//        component.setHoverEvent(hoverEvent);
        BaseComponent[] component = TextComponent.fromLegacyText(ColorUtil.parse(message));
        for (BaseComponent baseComponent : component) {
            baseComponent.setHoverEvent(hoverEvent);
        }


        if (enabledViewer) {
            if (ItemStackUtil.isShulkerBox(itemStack)) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta instanceof BlockStateMeta im) {
                    BlockState blockState = im.getBlockState();
                    if (blockState instanceof ShulkerBox shulker) {
                        UUID uuid = InventoryViewer.putShulkerBox(shulker, itemMeta.getDisplayName());
                        ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/invviewer " + uuid.toString());
//                        component.setClickEvent(clickEvent);
                        for (BaseComponent baseComponent : component) {
                            baseComponent.setClickEvent(clickEvent);
                        }
                    }
                }
            }
        }

        return component;
    }

    public String asString() {
        return StringUtils.join(Arrays.stream(this.componentBuilder.create()).map(com -> com.toPlainText()).toList(), "");
    }
}
