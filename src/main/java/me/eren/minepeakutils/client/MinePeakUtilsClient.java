package me.eren.minepeakutils.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class MinePeakUtilsClient implements ClientModInitializer {

    public static final String MOD_ID = "minepeakutils";
    public static final KeyBinding.Category KEYBIND_CATEGORY = KeyBinding.Category.create(Identifier.of(MOD_ID, "keys"));

    private static final Text PREFIX = Text.literal("[").setStyle(Style.EMPTY.withColor(Formatting.GRAY))
                    .append(Text.literal("MinePeakUtils").setStyle(Style.EMPTY.withBold(true).withColor(0xFF0000))
                    .append(Text.literal("] ").setStyle(Style.EMPTY.withBold(false).withColor(Formatting.GRAY))));

    private final ChatChannelListener chatChannelListener = new ChatChannelListener();

    @Override
    public void onInitializeClient() {
        chatChannelListener.load();
    }

    public static void send(String message) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null) {
            throw new IllegalStateException("player is null");
        }

        client.player.sendMessage(PREFIX.copy().append(Text.literal(message).withColor(0xFFFFFF)), false);
    }

}
