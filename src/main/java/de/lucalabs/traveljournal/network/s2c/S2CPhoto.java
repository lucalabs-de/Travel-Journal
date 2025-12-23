package de.lucalabs.traveljournal.network.s2c;

import de.lucalabs.traveljournal.TravelJournal;
import de.lucalabs.traveljournal.TravelJournalClient;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class S2CPhoto extends PacketByteBuf {

    public static final Identifier ID = TravelJournal.id("s2c_photo");

    public S2CPhoto(UUID bookmarkId, BufferedImage photo) {
        super(Unpooled.buffer());
        writeUuid(bookmarkId);

        var stream = new ByteArrayOutputStream();
        try {
            ImageIO.write(photo, "png", stream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write image to stream: " + e.getMessage());
        }

        writeByteArray(stream.toByteArray());
    }

    public static void apply(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        var bookmarkId = buf.readUuid();
        var bytes = buf.readByteArray();

        BufferedImage image;
        try {
            image = ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read image from stream: " + e.getMessage());
        }

        client.execute(() -> TravelJournalClient.handlers.photoReceived(bookmarkId, image));
    }
}
