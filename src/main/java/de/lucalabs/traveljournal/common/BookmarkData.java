package de.lucalabs.traveljournal.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public record BookmarkData(UUID id, String name, RegistryKey<World> dimension, BlockPos pos, BookmarkExtraData extra) {

    public static final Codec<BookmarkData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Uuids.CODEC.fieldOf("id")
                    .forGetter(BookmarkData::id),
            Codec.STRING.fieldOf("name")
                    .forGetter(BookmarkData::name),
            RegistryKey.createCodec(RegistryKeys.WORLD).fieldOf("dimension")
                    .forGetter(BookmarkData::dimension),
            BlockPos.CODEC.fieldOf("pos")
                    .forGetter(BookmarkData::pos),
            BookmarkExtraData.CODEC.fieldOf("extra")
                    .forGetter(BookmarkData::extra)
    ).apply(instance, BookmarkData::new));

    public static final BookmarkData EMPTY = new BookmarkData(
            UUID.randomUUID(),
            "",
            World.OVERWORLD,
            BlockPos.ORIGIN,
            BookmarkExtraData.EMPTY
    );

    public static Mutable create() {
        return new Mutable(UUID.randomUUID(), EMPTY);
    }

    public static BookmarkData get(ItemStack stack) {
        var nbt = stack.getOrCreateNbt().get("bookmark_data");

        if (nbt == null) {
            return BookmarkData.EMPTY;
        }

        return BookmarkData.CODEC.decode(NbtOps.INSTANCE, nbt).result().orElseThrow().getFirst();
    }

    public static void set(ItemStack stack, BookmarkData data) {
        var bookmarkNbt = BookmarkData.CODEC.encodeStart(NbtOps.INSTANCE, data).result().orElseThrow();
        stack.getOrCreateNbt().put("bookmark_data", bookmarkNbt);
    }

    public static class Mutable {
        private final UUID id;
        private String description;
        private String name;
        private RegistryKey<World> dimension;
        private BlockPos pos;
        private String author;
        private long timestamp;
        private DyeColor color;

        public Mutable(BookmarkData data) {
            this(data.id(), data);
        }

        public Mutable(UUID id, BookmarkData data) {
            this.id = id;
            this.name = data.name();
            this.dimension = data.dimension();
            this.pos = data.pos();
            this.author = data.extra().author();
            this.description = data.extra().description();
            this.timestamp = data.extra().timestamp();
            this.color = data.extra().color();
        }

        public UUID id() {
            return this.id;
        }

        public String name() {
            return this.name;
        }

        public String description() {
            return this.description;
        }

        public BlockPos pos() {
            return this.pos;
        }

        public RegistryKey<World> dimension() {
            return this.dimension;
        }

        public DyeColor color() {
            return this.color;
        }

        public Mutable name(String name) {
            this.name = name;
            return this;
        }

        public Mutable pos(BlockPos pos) {
            this.pos = pos;
            return this;
        }

        public Mutable dimension(RegistryKey<World> dimension) {
            this.dimension = dimension;
            return this;
        }

        public Mutable author(String author) {
            this.author = author;
            return this;
        }

        public Mutable description(String description) {
            this.description = description;
            return this;
        }

        public Mutable timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Mutable color(DyeColor color) {
            this.color = color;
            return this;
        }

        public BookmarkData toImmutable() {
            var extra = new BookmarkExtraData(author, description, timestamp, color);
            return new BookmarkData(id, name, dimension, pos, extra);
        }
    }
}
