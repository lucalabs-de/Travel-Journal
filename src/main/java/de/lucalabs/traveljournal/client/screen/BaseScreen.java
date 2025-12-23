package de.lucalabs.traveljournal.client.screen;

import de.lucalabs.traveljournal.common.ClientHelpers;
import de.lucalabs.traveljournal.common.Resources;
import de.lucalabs.traveljournal.sounds.TravelJournalSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public abstract class BaseScreen extends Screen {
    protected int midX;
    protected int backgroundWidth;
    protected int backgroundHeight;

    public BaseScreen(Text component) {
        super(component);
    }

    @Override
    protected void init() {
        super.init();

        if (client == null) return;

        midX = width / 2;
        backgroundWidth = Resources.BACKGROUND_DIMENSIONS.getFirst();
        backgroundHeight = Resources.BACKGROUND_DIMENSIONS.getSecond();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        renderTitle(context, midX, 24);
    }

    @Override
    public void renderBackground(DrawContext context) {
        super.renderBackground(context);
        int x = (width - backgroundWidth) / 2;
        int y = 5;
        context.drawTexture(getBackgroundTexture(), x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void close() {
        var player = MinecraftClient.getInstance().player;
        if (player != null) {
            player.playSound(TravelJournalSounds.JOURNAL_INTERACT, 0.5f, 1.0f);
        }
        super.close();
    }

    protected void renderTitle(DrawContext context, int x, int y) {
        ClientHelpers.drawCenteredString(context, textRenderer, getTitle(), x, y, 0x702f20, false);
    }

    protected Identifier getBackgroundTexture() {
        return Resources.BACKGROUND;
    }

    protected MinecraftClient client() {
        return MinecraftClient.getInstance();
    }
}
