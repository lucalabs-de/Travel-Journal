package de.lucalabs.traveljournal.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Uuids;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public record JournalData(UUID id, List<BookmarkData> bookmarks) {
    public static final Codec<JournalData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Uuids.CODEC.fieldOf("id")
                    .forGetter(JournalData::id),
            BookmarkData.CODEC.listOf().fieldOf("bookmarks")
                    .forGetter(JournalData::bookmarks)
    ).apply(instance, JournalData::new));

    public static final JournalData EMPTY = new JournalData(UUID.randomUUID(), List.of());

    public static Mutable create() {
        return new Mutable(UUID.randomUUID(), EMPTY);
    }

    public static JournalData get(ItemStack stack) {
        var nbt = stack.getOrCreateNbt().get("journal_data");

        if (nbt == null) {
            return JournalData.EMPTY;
        }

        return JournalData.CODEC.decode(NbtOps.INSTANCE, nbt).result().orElseThrow().getFirst();
    }

    public static JournalData set(ItemStack stack, Mutable mutable) {
        var immutable = mutable.toImmutable();
        var journalNbt = JournalData.CODEC.encodeStart(NbtOps.INSTANCE, immutable).result().orElseThrow();

        stack.getOrCreateNbt().put("journal_data", journalNbt);

        return immutable;
    }

    public Optional<BookmarkData> getBookmark(UUID bookmarkId) {
        return bookmarks.stream().filter(b -> b.id().equals(bookmarkId)).findFirst();
    }

    public boolean hasBookmark(UUID bookmarkId) {
        return getBookmark(bookmarkId).isPresent();
    }

    @SuppressWarnings("UnusedReturnValue")
    public static class Mutable {
        private final UUID id;
        private final List<BookmarkData> bookmarks;

        public Mutable(JournalData data) {
            this(data.id(), data);
        }

        public Mutable(UUID id, JournalData data) {
            this.id = id;
            this.bookmarks = new ArrayList<>(data.bookmarks());
        }

        public Optional<BookmarkData> getBookmark(UUID bookmarkId) {
            return bookmarks.stream().filter(b -> b.id().equals(bookmarkId)).findFirst();
        }

        public Mutable addBookmark(BookmarkData bookmark) {
            bookmarks.add(bookmark);
            return this;
        }

        public Mutable updateBookmark(BookmarkData bookmark) {
            var existing = getBookmark(bookmark.id()).orElseThrow();
            bookmarks.set(bookmarks.indexOf(existing), bookmark);
            return this;
        }

        public Mutable deleteBookmark(UUID bookmarkId) {
            var existing = getBookmark(bookmarkId).orElseThrow();
            bookmarks.remove(existing);
            return this;
        }

        public JournalData save(ItemStack stack) {
            return JournalData.set(stack, this);
        }

        public JournalData toImmutable() {
            return new JournalData(id, bookmarks);
        }
    }
}
