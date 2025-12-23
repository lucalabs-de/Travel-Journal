package de.lucalabs.traveljournal;

import de.lucalabs.traveljournal.common.Handlers;
import de.lucalabs.traveljournal.item.JournalPageItem;
import de.lucalabs.traveljournal.item.TravelJournalItem;
import de.lucalabs.traveljournal.network.c2s.*;
import de.lucalabs.traveljournal.sounds.TravelJournalSounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TravelJournal implements ModInitializer {
    public static final String ID = "traveljournal";
    public static final Logger LOGGER = LoggerFactory.getLogger(ID);

    public static final String PHOTOS_DIR = "traveljournal_travel_journal_photos";

    public static final TravelJournalItem TRAVEL_JOURNAL_ITEM = new TravelJournalItem();
    public static final JournalPageItem JOURNAL_PAGE_ITEM = new JournalPageItem();

    public static final Handlers handlers = new Handlers();

    public static final int scaledPhotoWidth = 192;
    public static final int scaledPhotoHeight = 96;

    public static Identifier id(String path) {
        return Identifier.of(ID, path);
    }

    @Override
    public void onInitialize() {
        Registry.register(Registries.ITEM, Identifier.of(ID, "travel_journal"), TRAVEL_JOURNAL_ITEM);
        Registry.register(Registries.ITEM, Identifier.of(ID, "travel_journal_page"), JOURNAL_PAGE_ITEM);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.add(TRAVEL_JOURNAL_ITEM);
            entries.add(JOURNAL_PAGE_ITEM);
        });

        TravelJournalSounds.initialize();

        ServerPlayNetworking.registerGlobalReceiver(C2SDeleteBookmark.ID, C2SDeleteBookmark::apply);
        ServerPlayNetworking.registerGlobalReceiver(C2SDownloadPhoto.ID, C2SDownloadPhoto::apply);
        ServerPlayNetworking.registerGlobalReceiver(C2SExportMap.ID, C2SExportMap::apply);
        ServerPlayNetworking.registerGlobalReceiver(C2SExportPage.ID, C2SExportPage::apply);
        ServerPlayNetworking.registerGlobalReceiver(C2SMakeBookmark.ID, C2SMakeBookmark::apply);
        ServerPlayNetworking.registerGlobalReceiver(C2SPhoto.ID, C2SPhoto::apply);
        ServerPlayNetworking.registerGlobalReceiver(C2SUpdateBookmark.ID, C2SUpdateBookmark::apply);

        LOGGER.info("Loaded Travel Journal");
    }
}