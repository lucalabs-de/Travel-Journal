package de.lucalabs.traveljournal.network.c2s;

import de.lucalabs.traveljournal.TravelJournal;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class C2SMakeBookmark extends PacketByteBuf {

    public static final Identifier ID = TravelJournal.id("c2s_make_bookmark");

    public C2SMakeBookmark(String bookmarkName) {
        super(Unpooled.buffer());
        writeString(bookmarkName);
    }

    public static void apply(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        var name = buf.readString();
        server.execute(() -> TravelJournal.handlers.makeBookmarkReceived(player, name));
    }
}
