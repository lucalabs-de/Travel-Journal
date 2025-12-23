package de.lucalabs.traveljournal.item;

import de.lucalabs.traveljournal.TravelJournal;
import de.lucalabs.traveljournal.TravelJournalClient;
import de.lucalabs.traveljournal.common.BookmarkData;
import de.lucalabs.traveljournal.common.JournalData;
import de.lucalabs.traveljournal.sounds.TravelJournalSounds;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class TravelJournalItem extends Item {
    public TravelJournalItem() {
        super(new FabricItemSettings().maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        var stack = player.getStackInHand(hand);
        var journal = JournalData.get(stack);

        // Check if holding a page in another hand.
        for (var interactionHand : Hand.values()) {
            var held = player.getStackInHand(interactionHand);

            // If holding a valid page, check the journal to see if it can be added.
            if (held.isOf(TravelJournal.JOURNAL_PAGE_ITEM)) {
                var bookmark = BookmarkData.get(held);
                if (!journal.hasBookmark(bookmark.id())) {

                    // Add the page to the journal.
                    new JournalData.Mutable(journal)
                            .addBookmark(bookmark)
                            .save(stack);

                    world.playSound(null, player.getBlockPos(), TravelJournalSounds.JOURNAL_INTERACT, SoundCategory.PLAYERS, 0.7f, 1.0f);
                    held.decrement(1);
                    return new TypedActionResult<>(ActionResult.SUCCESS, stack);
                }
            }
        }

        if (world.isClient()) {
            TravelJournalClient.handlers.openBookmarks(stack);
        }

        world.playSound(null, player.getBlockPos(), TravelJournalSounds.JOURNAL_INTERACT, SoundCategory.PLAYERS, 0.7f, 1.0f);
        return new TypedActionResult<>(ActionResult.SUCCESS, stack);
    }
}
