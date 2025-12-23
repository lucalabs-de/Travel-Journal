package de.lucalabs.traveljournal.network.c2s;

import de.lucalabs.traveljournal.TravelJournal;
import de.lucalabs.traveljournal.common.BookmarkData;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class C2SUpdateBookmark extends PacketByteBuf {

    public static final Identifier ID = TravelJournal.id("c2s_update_bookmark");

    public C2SUpdateBookmark(UUID id, BookmarkData bookmark) {
        super(Unpooled.buffer());
        writeUuid(id);

        var bookmarkNbt = BookmarkData.CODEC.encodeStart(NbtOps.INSTANCE, bookmark).result().orElseThrow();
        var wireNbt = new NbtCompound();

        wireNbt.put("bookmark", bookmarkNbt);

        writeNbt(wireNbt);
    }

    public static void apply(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        var id = buf.readUuid();
        var wireNbt = buf.readNbt();

        if (wireNbt != null && wireNbt.contains("bookmark")) {
            var bookmarkNbt = wireNbt.get("bookmark");
            var bookmark = BookmarkData.CODEC.decode(NbtOps.INSTANCE, bookmarkNbt).result().orElseThrow().getFirst();

            server.execute(() -> TravelJournal.handlers.updateBookmarkReceived(player, id, bookmark));
        }
    }
}
