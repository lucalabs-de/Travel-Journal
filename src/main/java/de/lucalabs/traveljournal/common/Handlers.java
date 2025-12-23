package de.lucalabs.traveljournal.common;

import de.lucalabs.traveljournal.TravelJournal;
import de.lucalabs.traveljournal.network.s2c.S2CPhoto;
import de.lucalabs.traveljournal.network.s2c.S2CTakePhoto;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Handlers {

    public static final boolean DO_DELETE_ON_SERVER = false;

    /**
     * Client wants to make a new bookmark.
     */
    public void makeBookmarkReceived(PlayerEntity player, String bookmarkName) {
        var dimension = player.getWorld().getRegistryKey();
        var pos = player.getBlockPos();

        var stack = Helpers.tryGetTravelJournal(player);
        if (stack.isEmpty()) return;

        var journal = JournalData.get(stack);
//        if (journal.isFull()) {
//            return; // TODO: message back to client.
//        }

        // Create a new bookmark and attach it to the journal.
        var bookmark = BookmarkData.create()
                .name(bookmarkName)
                .dimension(dimension)
                .pos(pos)
                .timestamp(System.currentTimeMillis() / 1000L)
                .author(player.getEntityName())
                .toImmutable();

        TravelJournal.LOGGER.info("Created a new bookmark entry with UUID {}", bookmark.id());

        new JournalData.Mutable(journal)
                .addBookmark(bookmark)
                .save(stack);

        // Instruct the client to take a photo.
        ServerPlayNetworking.send((ServerPlayerEntity) player, S2CTakePhoto.ID, new S2CTakePhoto(journal.id(), bookmark.id()));
    }

    /**
     * Client wants to update the given bookmark.
     * Check that the given journal is present in the player's inventory.
     */
    public void updateBookmarkReceived(PlayerEntity player, UUID journalId, BookmarkData bookmark) {
        var stack = Helpers.tryGetTravelJournal(player, journalId);
        if (stack.isEmpty()) {
            TravelJournal.LOGGER.error("No such journal?");
            return;
        }

        new JournalData.Mutable(JournalData.get(stack))
                .updateBookmark(bookmark)
                .save(stack);
    }

    /**
     * Client wants to delete the given bookmark.
     * Check that the given journal is present in the player's inventory.
     */
    public void deleteBookmarkReceived(PlayerEntity player, UUID journalId, UUID bookmarkId) {
        var stack = Helpers.tryGetTravelJournal(player, journalId);
        if (stack.isEmpty()) {
            TravelJournal.LOGGER.error("No such journal?");
            return;
        }

        new JournalData.Mutable(JournalData.get(stack))
                .deleteBookmark(bookmarkId)
                .save(stack);

        // Clean up photo on server.
        if (DO_DELETE_ON_SERVER) {
            tryDeletePhoto((ServerWorld) player.getWorld(), bookmarkId);
        }
    }

    /**
     * Client has sent a new photo.
     */
    public void photoReceived(PlayerEntity player, UUID bookmarkId, BufferedImage photo) {
        trySavePhoto((ServerWorld) player.getWorld(), bookmarkId, photo);
    }

    /**
     * Client wants to download a photo.
     */
    public void downloadPhotoReceived(PlayerEntity player, UUID uuid) {
        var image = tryLoadPhoto((ServerWorld) player.getWorld(), uuid);
        if (image == null) {
            return;
        }

        // Send the photo to the client.
        ServerPlayNetworking.send((ServerPlayerEntity) player, S2CPhoto.ID, new S2CPhoto(uuid, image));
    }

    /**
     * Clients wants to export a map using the given bookmark.
     */
    public void exportMapReceived(PlayerEntity player, BookmarkData bookmark) {
        var level = (ServerWorld) player.getWorld();
        var dimension = bookmark.dimension();
        var pos = bookmark.pos();

        if (level.getRegistryKey() != dimension) {
            return;
        }

        for (var stack : Helpers.collectPotentialItems(player)) {
            if (stack.isOf(Items.MAP)) {
                var map = FilledMapItem.createMap(level, pos.getX(), pos.getZ(), (byte) 2, true, true);
                FilledMapItem.fillExplorationMap(level, map);
                MapState.addDecorationsNbt(map, pos, "+", MapIcon.Type.TARGET_X);
                setCommonDataAndGiveToPlayer(player, map, bookmark);
                stack.decrement(1);
                return;
            }
        }
    }

    /**
     * Clients wants to export a page using the given bookmark.
     */
    public void exportPageReceived(PlayerEntity player, BookmarkData bookmark) {
        for (var stack : Helpers.collectPotentialItems(player)) {
            if (stack.isOf(Items.PAPER)) {
                var page = new ItemStack(TravelJournal.JOURNAL_PAGE_ITEM);
                BookmarkData.set(page, bookmark);
                setCommonDataAndGiveToPlayer(player, page, bookmark);
                stack.decrement(1);
                return;
            }
        }
    }

    /**
     * Shared method to add the bookmark name as the item name and extra details as the item lore.
     */
    private void setCommonDataAndGiveToPlayer(PlayerEntity player, ItemStack stack, BookmarkData bookmark) {
        var name = bookmark.name();
        var description = bookmark.extra().description();

//        stack.set(DataTexts.ITEM_NAME, Text.literal(name));
        stack.setCustomName(Text.literal(name));
        List<Text> lore = new ArrayList<>();

        // Add dimension and position to tooltip.
        lore.add(Helpers.dimensionAsText(bookmark.dimension()).copy()
                .fillStyle(Style.EMPTY.withColor(Formatting.AQUA).withItalic(false)));
        lore.add(Helpers.positionAsText(bookmark.pos()).copy()
                .fillStyle(Style.EMPTY.withColor(Formatting.AQUA).withItalic(false)));

        var loreList = new NbtList();

        // Add the full description to tooltip, if present.
        if (!description.isEmpty()) {
            lore.addAll(Helpers.wrap(description));
            for (var line : lore) {
                loreList.add(NbtString.of(Text.Serializer.toJson(line)));
            }
        }

//        stack.set(DataTexts.LORE, new ItemLore(lore));
        stack.getOrCreateNbt().getCompound("display").put("Lore", loreList);
        player.giveItemStack(stack);
        player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.6f, 0.95f);
    }

    /**
     * Gets or returns the custom photos directory.
     * Create a subdirectory within the world folder to store all our custom photos in.
     */
    public File getOrCreatePhotosDir(ServerWorld level) {
        var server = level.getServer();
        var photosDir = server.getFile(TravelJournal.PHOTOS_DIR);

        if (!photosDir.exists() && !photosDir.mkdir()) {
            throw new RuntimeException("Could not create photos directory in the world folder, giving up");
        }
        return photosDir;
    }

    /**
     * Try and save a given image buffer to a file within the custom photos directory.
     */
    public void trySavePhoto(ServerWorld level, UUID uuid, BufferedImage image) {
        var dir = getOrCreatePhotosDir(level);
        var path = new File(dir, uuid + ".png");
        boolean success;

        try {
            success = ImageIO.write(image, "png", path);
        } catch (IOException e) {
            TravelJournal.LOGGER.error("Could not save photo for bookmarkId: {}: {}", uuid, e.getMessage());
            return;
        }

        if (success) {
            TravelJournal.LOGGER.info("Saved image to photos for bookmarkId: {}", uuid);
        } else {
            TravelJournal.LOGGER.error("ImageIO.write did not save the image successfully for bookmarkId: {}", uuid);
        }
    }

    /**
     * Try and load an image from the custom photos directory for a given photo UUID.
     */
    @Nullable
    public BufferedImage tryLoadPhoto(ServerWorld level, UUID uuid) {
        var dir = getOrCreatePhotosDir(level);
        var path = new File(dir, uuid + ".png");
        BufferedImage image;

        try {
            image = ImageIO.read(path);
        } catch (IOException e) {
            TravelJournal.LOGGER.warn("Could not load photo for bookmarkId: {}: {}", uuid, e.getMessage());
            return null;
        }

        return image;
    }

    /**
     * Try and delete an image from the custom photos directory for given photo UUID.
     */
    public void tryDeletePhoto(ServerWorld level, UUID uuid) {
        var dir = getOrCreatePhotosDir(level);
        var path = new File(dir, uuid + ".png");
        if (path.exists()) {
            var result = path.delete();
            if (result) {
                TravelJournal.LOGGER.info("Deleted photo with bookmarkId: {}", uuid);
            } else {
                TravelJournal.LOGGER.error("Error trying to delete photo with bookmarkId: {}", uuid);
            }
        }
    }
}
