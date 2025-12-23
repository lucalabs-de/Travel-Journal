package de.lucalabs.traveljournal.network.c2s;

import de.lucalabs.traveljournal.TravelJournal;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class C2SPhoto extends PacketByteBuf {

    public static final Identifier ID = TravelJournal.id("c2s_photo");

    public C2SPhoto(UUID id, BufferedImage photo) {
        super(Unpooled.buffer());
        writeUuid(id);

        var stream = new ByteArrayOutputStream();
        try {
            ImageIO.write(photo, "png", stream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write image to stream: " + e.getMessage());
        }

        writeByteArray(stream.toByteArray());
    }

    public static void apply(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        UUID bookmarkId = buf.readUuid();

        BufferedImage image;
        try {
            image = ImageIO.read(new ByteArrayInputStream(buf.readByteArray()));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read image from stream: " + e.getMessage());
        }

        server.execute(() -> TravelJournal.handlers.photoReceived(player, bookmarkId, image));
    }
}
