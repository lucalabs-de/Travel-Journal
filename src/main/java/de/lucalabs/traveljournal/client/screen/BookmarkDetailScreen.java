package de.lucalabs.traveljournal.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import de.lucalabs.traveljournal.TravelJournalClient;
import de.lucalabs.traveljournal.client.Buttons;
import de.lucalabs.traveljournal.common.BookmarkData;
import de.lucalabs.traveljournal.common.CoreButtons;
import de.lucalabs.traveljournal.common.Helpers;
import de.lucalabs.traveljournal.common.Resources;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class BookmarkDetailScreen extends BaseScreen {
    private final ItemStack stack;
    private final BookmarkData.Mutable bookmark;
    private TextFieldWidget name;
    private EditBoxWidget description;
    private TexturedButtonWidget exportPageButton;
    private TexturedButtonWidget exportMapButton;

    public BookmarkDetailScreen(ItemStack stack, BookmarkData bookmark) {
        super(Text.literal(bookmark.name()));

        this.stack = stack;
        this.bookmark = new BookmarkData.Mutable(bookmark);
    }

    @Override
    protected void init() {
        super.init();

        var inputWidth = 220;
        var nameHeight = 15;
        var descriptionHeight = 46;
        var top = 110;

        name = new TextFieldWidget(textRenderer, midX - (inputWidth / 2), top, inputWidth, nameHeight, Resources.EDIT_NAME);
        name.setFocused(true);
        name.setText(bookmark.name());
        name.setChangedListener(bookmark::name);
        name.setFocusUnlocked(true);
        name.setEditableColor(-1);
        name.setUneditableColor(-1);
        name.setDrawsBackground(true);
        name.setMaxLength(32);
        name.setEditable(true);

        addDrawableChild(name);
        setFocused(name);

        description = new EditBoxWidget(textRenderer, midX - (inputWidth / 2), top + 29, inputWidth, descriptionHeight,
                Resources.EDIT_DESCRIPTION, Text.empty());

        description.setFocused(false);
        description.setText(bookmark.description());
        description.setChangeListener(bookmark::description);
        description.setMaxLength(140);

        addDrawableChild(description);

        top = 220;
        addDrawableChild(new CoreButtons.DeleteButton((int) (midX - (CoreButtons.DeleteButton.WIDTH * 1.5)) - 5, top,
                b -> deleteAndClose()));
        addDrawableChild(new CoreButtons.CancelButton(midX - (CoreButtons.CancelButton.WIDTH / 2), top,
                b -> close()));
        addDrawableChild(new CoreButtons.SaveButton(midX + (CoreButtons.SaveButton.WIDTH / 2) + 5, top,
                b -> saveAndClose()));

        exportPageButton = new Buttons.ExportPageButton(midX + 120, 30,
                b -> TravelJournalClient.handlers.exportPage(bookmark.toImmutable()));

        exportMapButton = new Buttons.ExportMapButton(midX + 120, 47,
                b -> TravelJournalClient.handlers.exportMap(bookmark.toImmutable()));

        addDrawableChild(exportPageButton);
        addDrawableChild(exportMapButton);

        exportPageButton.visible = false;
        exportMapButton.visible = false;
    }

    @Override
    public void render(DrawContext guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);

        renderPhoto(guiGraphics);
        renderDimensionAndPosition(guiGraphics);
        renderUtilityButtons();

        name.render(guiGraphics, mouseX, mouseY, delta);
        description.render(guiGraphics, mouseX, mouseY, delta);

        var textColor = 0x404040;
        guiGraphics.drawText(textRenderer, Resources.NAME_TEXT, midX - 109, 101, textColor, false);
        guiGraphics.drawText(textRenderer, Resources.DESCRIPTION, midX - 109, 129, textColor, false);
    }

    private void renderUtilityButtons() {
        var handlers = TravelJournalClient.handlers;

        var y = 13;
        var lineHeight = 17;

        if (handlers.hasPaper()) {
            y += lineHeight;
            exportPageButton.visible = true;
            exportPageButton.setY(y);
        } else {
            exportPageButton.visible = false;
        }

        if (handlers.hasMap()) {
            y += lineHeight;
            exportMapButton.visible = true;
            exportMapButton.setY(y);
        } else {
            exportMapButton.visible = false;
        }
    }

    private void renderPhoto(DrawContext guiGraphics) {
        var pose = guiGraphics.getMatrices();
        var resource = TravelJournalClient.handlers.tryLoadPhoto(bookmark.id());

        if (resource != null) {
            pose.push();
            var top = 24; // This is scaled by pose.scale()
            var left = -169; // This is scaled by pose.scale()
            pose.translate(midX - 40f, 33f, 1.0f);
            pose.scale(0.41f, 0.22f, 1.0f);
            RenderSystem.setShaderTexture(0, resource);
            guiGraphics.drawTexture(resource, left, top, 0, 0, 256, 256);
            pose.pop();
        }
    }

    private void renderDimensionAndPosition(DrawContext guiGraphics) {
        var pose = guiGraphics.getMatrices();
        var color = 0xb8907a;

        pose.push();
        var top = 30; // This is scaled by pose.scale()
        var left = 43; // This is scaled by pose.scale()
        pose.translate(midX - 25f, 20f, 1.0f);
        pose.scale(0.82f, 0.82f, 1.0f);

        var positionText = Helpers.positionAsText(bookmark.pos());
        var dimensionText = Helpers.dimensionAsText(bookmark.dimension());

        guiGraphics.drawText(textRenderer, Text.translatable(Resources.DIMENSION).formatted(Formatting.BOLD), left, top, color, false);
        guiGraphics.drawText(textRenderer, dimensionText, left, top + 12, color, false);

        guiGraphics.drawText(textRenderer, Text.translatable(Resources.POSITION).formatted(Formatting.BOLD), left, top + 30, color, false);
        guiGraphics.drawText(textRenderer, positionText, left, top + 42, color, false);
        pose.pop();
    }

    private void saveAndClose() {
        // Validation first.
        if (this.bookmark.name().isEmpty()) {
            this.bookmark.name(Resources.NEW_BOOKMARK.getString());
        }

        TravelJournalClient.handlers.updateBookmark(stack, bookmark.toImmutable());
        close();
    }

    private void deleteAndClose() {
        TravelJournalClient.handlers.deleteBookmark(stack, bookmark.toImmutable());
        close();
    }

    @Override
    public void close() {
        TravelJournalClient.handlers.openBookmarks(stack);
    }
}
