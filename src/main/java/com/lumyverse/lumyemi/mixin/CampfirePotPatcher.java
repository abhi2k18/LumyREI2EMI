package com.lumyverse.lumyemi.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;

import java.util.Map;

@Mixin(HandledScreens.class)
public class CampfirePotPatcher {

    @Shadow
    @Final
    private static Map<ScreenHandlerType<?>, HandledScreens.Provider<?, ?>> PROVIDERS;

    @Shadow
    @Final
    private static Logger LOGGER;

    @Unique
    @Nullable
    @SuppressWarnings("unchecked")
    private static <T extends ScreenHandler> HandledScreens.Provider<T, ?> getProvider(ScreenHandlerType<T> type) {
        return (HandledScreens.Provider<T, ?>) PROVIDERS.get(type);
    }

    /**
     * @author LightDream
     * @reason Fixes crash on MMO Realms by remapping the Campfire Pot menu type to a valid one.
     */
    @Overwrite
    public static <T extends ScreenHandler> void open(ScreenHandlerType<T> type, MinecraftClient client, int id, Text title) {
        ServerInfo currentServer = client.getCurrentServerEntry();
        String ip = "localhost";

        if (currentServer != null) {
            ip = currentServer.address;
        }

        if(title.getString().equals("Campfire Pot") && ip.toLowerCase().endsWith(".mmorealms.gg")) {
            //noinspection unchecked
            type = (ScreenHandlerType<T>) Registries.SCREEN_HANDLER.get(Identifier.of("minecraft", "cooking_pot"));
        }

        HandledScreens.Provider<T, ?> provider = getProvider(type);
        if (provider == null) {
            LOGGER.warn("Failed to create screen for menu type: {}", Registries.SCREEN_HANDLER.getId(type));
        } else {
            provider.open(title, type, client, id);
        }
    }
}