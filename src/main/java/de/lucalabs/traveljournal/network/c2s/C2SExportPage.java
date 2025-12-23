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

public class C2SExportPage extends PacketByteBuf {
    public static final Identifier ID = TravelJournal.id("c2s_export_page");

    public C2SExportPage(BookmarkData bookmark) {
        super(Unpooled.buffer());

        var bookmarkNbt = BookmarkData.CODEC.encodeStart(NbtOps.INSTANCE, bookmark).result().orElseThrow();
        var wireNbt = new NbtCompound();

        wireNbt.put("bookmark", bookmarkNbt);

        writeNbt(wireNbt);
    }

    public static void apply(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        var wireNbt = buf.readNbt();

        if (wireNbt != null && wireNbt.contains("bookmark")) {
            var bookmarkNbt = wireNbt.get("bookmark");
            var bookmark = BookmarkData.CODEC.decode(NbtOps.INSTANCE, bookmarkNbt).result().orElseThrow().getFirst();

            server.execute(() -> TravelJournal.handlers.exportPageReceived(player, bookmark));
        }
    }
}
