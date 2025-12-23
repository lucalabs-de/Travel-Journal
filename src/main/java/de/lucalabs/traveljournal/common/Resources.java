package de.lucalabs.traveljournal.common;

import com.mojang.datafixers.util.Pair;
import de.lucalabs.traveljournal.TravelJournal;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class Resources {
    public static final Pair<Integer, Integer> BACKGROUND_DIMENSIONS = Pair.of(256, 208);
    public static final Identifier BACKGROUND = Identifier.of(TravelJournal.ID, "textures/gui/travel_journal.png");
    public static final Identifier PHOTO_BACKGROUND = Identifier.of(TravelJournal.ID, "textures/gui/photo_background.png");
    public static final Text DESCRIPTION = Text.translatable("gui.traveljournal.travel_journals.description");
    public static final Text DETAILS = Text.translatable("gui.traveljournal.travel_journals.details");
    public static final Text EDIT_DESCRIPTION = Text.translatable("gui.traveljournal.travel_journals.edit_description");
    public static final Text EDIT_NAME = Text.translatable("gui.traveljournal.travel_journals.edit_name");
    public static final Text EXPORT_PAGE = Text.translatable("gui.traveljournal.travel_journals.save_to_page");
    public static final Text EXPORT_MAP = Text.translatable("gui.traveljournal.travel_journals.save_to_map");
    public static final Text NAME_TEXT = Text.translatable("gui.traveljournal.travel_journals.name");
    public static final Text NEW_BOOKMARK = Text.translatable("gui.traveljournal.travel_journals.new_bookmark");
    public static final Text TRAVEL_JOURNAL = Text.translatable("gui.traveljournal.travel_journals.travel_journal");
    public static final String DIMENSION = "gui.traveljournal.travel_journals.dimension";
    public static final String POSITION = "gui.traveljournal.travel_journals.position";
    public static final String XYZ_KEY = "gui.traveljournal.travel_journals.xyz";
    public static final String CREATED_BY_KEY = "gui.traveljournal.travel_journals.created_by";
}

