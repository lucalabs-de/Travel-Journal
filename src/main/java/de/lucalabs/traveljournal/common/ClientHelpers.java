package de.lucalabs.traveljournal.common;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;

public final class ClientHelpers {
    /**
     * Draw a centered string with optional dropshadow.
     */
    public static void drawCenteredString(DrawContext context, TextRenderer font, Text text, int x, int y, int color, boolean dropShadow) {
        var formattedCharSequence = text.asOrderedText();
        context.drawText(font, formattedCharSequence, x - font.getWidth(formattedCharSequence) / 2, y, color, dropShadow);
    }

    /**
     * Gets the nice name of the biome that the player is in.
     */
    public static String biomeName(PlayerEntity player) {
        return Text.translatableWithFallback(biomeLocaleKey(player), "").getString();
    }

    /**
     * Get a locale key for the biome at the player's current position.
     */
    public static String biomeLocaleKey(PlayerEntity player) {
        var registry = player.getWorld().getRegistryManager();
        var biome = player.getWorld().getBiome(player.getBlockPos());
        var key = registry.get(RegistryKeys.BIOME).getKey(biome.value());

        return key.map(k -> {
            var namespace = k.getValue().getNamespace();
            var path = k.getValue().getPath();
            return "biome." + namespace + "." + path;
        }).orElse("");
    }
}
