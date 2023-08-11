package ink.tuanzi.flatlib.factory;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.nbt.*;
import de.tr7zw.nbtapi.utils.MinecraftVersion;
import ink.tuanzi.flatlib.text.ColorUtil;
import lombok.val;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 打开虚拟告示牌工厂类
 * <a href="https://github.com/dev-launchers/minecraft__devbeans/blob/main/src/main/java/com/skullzbones/devbeans/Tools/SignInput.java">Source from github repo</a>
 */
public class SignInputFactory {
    private static final int SIGN_LINES = 4;
    private static final String NBT_FORMAT = "{\"text\":\"%s\"}";
    private final Plugin plugin;
    private final Map<Player, Menu> inputReceivers = new HashMap<>();

    public SignInputFactory(Plugin plugin) {
        this.plugin = plugin;
        this.listen();
    }

    public Menu createFactory(List<String> text) {
        Objects.requireNonNull(text, "text");
        return new Menu(text);
    }

    public Menu createFactory() {
        return new Menu(List.of("", "", "", ""));
    }

    private void listen() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.plugin, PacketType.Play.Client.UPDATE_SIGN) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();

                Menu menu = inputReceivers.remove(player);

                if (menu == null) {
                    return;
                }
                event.setCancelled(true);

                List<String> stringList = List.of(event.getPacket().getStringArrays().read(0));
                boolean success = menu.responseHandler.test(stringList);

                if (!success && menu.opensOnFail()) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> menu.open(player), 2L);
                }
                Location location = menu.position.toLocation(player.getWorld());
                player.sendBlockChange(location, location.getBlock().getBlockData());
            }
        });
    }

    public class Menu {

        private final List<String> text;
        private Predicate<List<String>> responseHandler;
        private boolean reopenIfFail;
        private BlockPosition position;
        private Material signMaterial = Material.BIRCH_SIGN;

        private Menu(List<String> text) {
            this.text = text;
        }

        public boolean opensOnFail() {
            return this.reopenIfFail;
        }

        protected BlockPosition getPosition() {
            return this.position;
        }

        public Menu setReopenIfFail(boolean flag) {
            this.reopenIfFail = flag;
            return this;
        }

        public Menu setMaterial(Material material) {
            this.signMaterial = material;
            return this;
        }

        public Menu handler(Predicate<List<String>> responseHandler) {
            this.responseHandler = responseHandler;
            return this;
        }

        public void open(Player player) {
            Objects.requireNonNull(player, "player");
            Location location = player.getLocation();
            this.position = new BlockPosition(location.getBlockX(), location.getBlockY() - 5, location.getBlockZ());

            player.sendBlockChange(this.position.toLocation(location.getWorld()), this.signMaterial.createBlockData());

            PacketContainer openSign = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.OPEN_SIGN_EDITOR);
            PacketContainer signData = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.TILE_ENTITY_DATA);

            openSign.getBooleans().writeSafely(0, true); // 1.20 front
            openSign.getBlockPositionModifier().write(0, this.position);

            NbtCompound signNBT = (NbtCompound) signData.getNbtModifier().read(0);

            IntStream.range(0, SIGN_LINES).forEach(line -> signNBT.put("Text" + (line + 1), text.size() > line ? String.format(NBT_FORMAT, ColorUtil.parse(text.get(line))) : " "));

            signNBT.put("x", this.position.getX());
            signNBT.put("y", this.position.getY());
            signNBT.put("z", this.position.getZ());
            signNBT.put("id", "minecraft:sign");

            signData.getBlockPositionModifier().write(0, this.position);
            signData.getNbtModifier().write(0, signNBT);

            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, signData);
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, openSign);
            } catch (InvocationTargetException exception) {
                exception.printStackTrace();
            }
            inputReceivers.put(player, this);
        }
    }
}
