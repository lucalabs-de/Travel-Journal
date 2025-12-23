package de.lucalabs.traveljournal;

import de.lucalabs.traveljournal.client.Handlers;
import de.lucalabs.traveljournal.network.s2c.S2CPhoto;
import de.lucalabs.traveljournal.network.s2c.S2CTakePhoto;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class TravelJournalClient implements ClientModInitializer {
	public static final Handlers handlers = new Handlers();

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(handlers::clientTick);
		HudRenderCallback.EVENT.register(handlers::hudRender);

		ClientPlayNetworking.registerGlobalReceiver(S2CPhoto.ID, S2CPhoto::apply);
		ClientPlayNetworking.registerGlobalReceiver(S2CTakePhoto.ID, S2CTakePhoto::apply);
	}
}