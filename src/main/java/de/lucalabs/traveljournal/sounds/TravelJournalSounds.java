package de.lucalabs.traveljournal.sounds;

import de.lucalabs.traveljournal.TravelJournal;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public final class TravelJournalSounds {
    public static final Identifier JOURNAL_INTERACT_ID = Identifier.of(TravelJournal.ID, "travel_journal_interact");
    public static final SoundEvent JOURNAL_INTERACT = SoundEvent.of(JOURNAL_INTERACT_ID);

    public static final Identifier PHOTO_ID = Identifier.of(TravelJournal.ID, "travel_journal_photo");
    public static final SoundEvent PHOTO = SoundEvent.of(PHOTO_ID);

    private TravelJournalSounds() {}

    public static void initialize() {
        TravelJournal.LOGGER.info("initializing sounds");

        Registry.register(Registries.SOUND_EVENT, JOURNAL_INTERACT_ID, JOURNAL_INTERACT);
        Registry.register(Registries.SOUND_EVENT, PHOTO_ID, PHOTO);
    }
}
