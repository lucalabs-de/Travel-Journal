package de.lucalabs.traveljournal.network.c2s;

import de.lucalabs.traveljournal.TravelJournal;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class C2SDeleteBookmark extends PacketByteBuf {

    public static final Identifier ID = TravelJournal.id("c2s_delete_bookmark");

    public C2SDeleteBookmark(UUID journalId, UUID bookmarkId) {
        super(Unpooled.buffer());
        writeUuid(journalId);
        writeUuid(bookmarkId);
    }


    public static void apply(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        var journalId = buf.readUuid();
        var bookmarkId = buf.readUuid();
        server.execute(() -> TravelJournal.handlers.deleteBookmarkReceived(player, journalId, bookmarkId));
    }
}
