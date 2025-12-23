package de.lucalabs.traveljournal.network.s2c;

import de.lucalabs.traveljournal.TravelJournal;
import de.lucalabs.traveljournal.TravelJournalClient;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class S2CTakePhoto extends PacketByteBuf {

    public static final Identifier ID = TravelJournal.id("s2c_take_photo");

    public S2CTakePhoto(UUID journalId, UUID bookmarkId) {
        super(Unpooled.buffer());
        writeUuid(journalId);
        writeUuid(bookmarkId);
    }


    public static void apply(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        var journalId = buf.readUuid();
        var bookmarkId = buf.readUuid();

        client.execute(() -> TravelJournalClient.handlers.takePhotoReceived(journalId, bookmarkId));
    }
}
