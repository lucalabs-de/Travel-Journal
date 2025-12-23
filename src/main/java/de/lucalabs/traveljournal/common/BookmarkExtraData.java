package de.lucalabs.traveljournal.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.DyeColor;


public record BookmarkExtraData(String author, String description, long timestamp, DyeColor color) {
    public static final Codec<BookmarkExtraData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("author")
                    .forGetter(BookmarkExtraData::author),
            Codec.STRING.fieldOf("description")
                    .forGetter(BookmarkExtraData::description),
            Codec.LONG.fieldOf("timestamp")
                    .forGetter(BookmarkExtraData::timestamp),
            DyeColor.CODEC.fieldOf("color")
                    .forGetter(BookmarkExtraData::color)
    ).apply(instance, BookmarkExtraData::new));

    public static final BookmarkExtraData EMPTY = new BookmarkExtraData("", "", -1, DyeColor.GRAY);
}
