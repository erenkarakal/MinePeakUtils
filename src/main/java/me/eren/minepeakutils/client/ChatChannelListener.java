package me.eren.minepeakutils.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents.AllowChat;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents.ModifyChat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import org.lwjgl.glfw.GLFW;

import static me.eren.minepeakutils.client.MinePeakUtilsClient.KEYBIND_CATEGORY;
import static me.eren.minepeakutils.client.MinePeakUtilsClient.MOD_ID;

public class ChatChannelListener implements AllowChat, ModifyChat, EndTick {

    private ChatChannel currentChannel = ChatChannel.ALL;
    private KeyBinding changeChatChannelBinding;

    public void load() {
        ClientSendMessageEvents.ALLOW_CHAT.register(this);
        ClientSendMessageEvents.MODIFY_CHAT.register(this);
        ClientTickEvents.END_CLIENT_TICK.register(this);

        changeChatChannelBinding = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key." + MOD_ID + ".change_chat_channel",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_PAGE_DOWN,
                        KEYBIND_CATEGORY
                ));
    }

    @Override
    public boolean allowSendChatMessage(String message) {
        if (currentChannel == ChatChannel.CLAN && !message.startsWith("/")) {
            String command = currentChannel.prefix + message;
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            assert player != null;
            player.networkHandler.sendChatCommand(command);
            return false;
        }
        return true;
    }

    @Override
    public String modifySendChatMessage(String message) {
        if (currentChannel != ChatChannel.CLAN) {
            return currentChannel.prefix + message;
        }
        return message;
    }

    @Override
    public void onEndTick(MinecraftClient minecraftClient) {
        if (changeChatChannelBinding.wasPressed()) {
            ChatChannel[] channels = ChatChannel.values();
            int index = 0;
            for (; index < channels.length; index++) {
                if (channels[index] == currentChannel) {
                    break;
                }
            }
            int newIndex = (index + 1) % channels.length;
            currentChannel = channels[newIndex];
            MinePeakUtilsClient.send("Changed chat channel to " + currentChannel);
        }
    }

    public enum ChatChannel {
        ALL(""),
        CLAN("clanchat "),
        LOCAL("!");

        private final String prefix;

        ChatChannel(String prefix) {
            this.prefix = prefix;
        }
    }

}
