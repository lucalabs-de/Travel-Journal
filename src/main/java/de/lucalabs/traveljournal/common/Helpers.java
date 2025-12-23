package de.lucalabs.traveljournal.common;

import de.lucalabs.traveljournal.TravelJournal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public final class Helpers {
    /**
     * Fetch the most readily available travel journal held by the player.
     * The order that is checked is:
     * - mainhand
     * - offhand
     * - inventory slots starting from 0
     */
    public static ItemStack tryGetTravelJournal(PlayerEntity player) {
        var items = collectPotentialItems(player);

        for (var stack : items) {
            if (stack.isOf(TravelJournal.TRAVEL_JOURNAL_ITEM)) {
                return stack;
            }
        }

        return ItemStack.EMPTY;
    }

    /**
     * Fetch a specific travel journal from the player's inventory.
     */
    public static ItemStack tryGetTravelJournal(PlayerEntity player, UUID journalId) {
        var items = collectPotentialItems(player);

        for (var stack : items) {
            if (stack.isOf(TravelJournal.TRAVEL_JOURNAL_ITEM)) {
                if (JournalData.get(stack).id().equals(journalId)) {
                    return stack;
                }
            }
        }

        return ItemStack.EMPTY;
    }

    /**
     * Gets an ordered list of items from the player, starting with hands and then inventory.
     */
    public static List<ItemStack> collectPotentialItems(PlayerEntity player) {
        var inventory = player.getInventory();
        List<ItemStack> items = new LinkedList<>();

        for (Hand hand : Hand.values()) {
            items.add(player.getStackInHand(hand));
        }

        items.addAll(inventory.main);
        return items;
    }

    /**
     * Wrap string at a sensible line length and converts into a list of components.
     * This uses an old version of WordUtils which may be problematic?
     */
    @SuppressWarnings("deprecation")
    public static List<Text> wrap(String str) {
        var wrapped = WordUtils.wrap(str, 30);
        return Arrays.stream(wrapped.split("\n")).map(s -> (Text) Text.literal(s)).toList();
    }

    /**
     * Get formatted text component of the given blockpos.
     */
    public static Text positionAsText(BlockPos pos) {
        return Text.translatable(Resources.XYZ_KEY, pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Get formatted text component of the given dimension.
     */
    public static Text dimensionAsText(RegistryKey<World> dimension) {
        return Text.translatable(dimensionLocaleKey(dimension));
    }

    /**
     * Get a locale key for a dimension.
     */
    public static String dimensionLocaleKey(RegistryKey<World> dimension) {
        var location = dimension.getValue();
        var namespace = location.getNamespace();
        var path = location.getPath();
        return "dimension." + namespace + "." + path;
    }

    public static Text getJournalName(ItemStack stack) {
        var nbt = stack.getOrCreateNbt().getString("customName");
        if (nbt == null) {
           return Text.literal("");
        }
        return Text.Serializer.fromJson(nbt);
    }
}
