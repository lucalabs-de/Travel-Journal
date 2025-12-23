package de.lucalabs.traveljournal.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import de.lucalabs.traveljournal.TravelJournalClient;
import de.lucalabs.traveljournal.client.Buttons;
import de.lucalabs.traveljournal.common.*;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class BookmarksScreen extends BaseScreen {
    private static final String DATE_FORMAT = "dd-MMM-yy";

    private final int columns;
    private final ItemStack stack;
    private int page;
    private boolean renderedButtons = false;

    public BookmarksScreen(ItemStack stack, int page) {
        super(Objects.requireNonNullElse(Helpers.getJournalName(stack), Resources.TRAVEL_JOURNAL));
        this.stack = stack;
        this.page = page;
        this.columns = 2;
    }

    @Override
    protected void init() {
        super.init();

        // Add footer buttons
        addDrawableChild(new CoreButtons.CloseButton(midX + 5, 220, b -> close()));
        addDrawableChild(new Buttons.NewBookmarkButton(midX - (Buttons.NewBookmarkButton.WIDTH + 5), 220,
                b -> TravelJournalClient.handlers.makeNewBookmark()));

        renderedButtons = false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        var bookmarks = bookmarks();
        var index = (page - 1) * columns;

        while (page > 1 && index >= bookmarks.size()) {
            --page;
            index = (page - 1) * columns;
        }

        for (var x = 0; x < columns; x++) {
            if (index >= bookmarks.size()) continue;

            var bookmark = bookmarks.get(index);

            renderTitleDetails(context, bookmark, x);
            renderPhoto(context, bookmark, x);
            renderPhotoDescriptionHover(context, bookmark, mouseX, mouseY, x);
            renderPosition(context, bookmark, x);
            renderDetailsButton(bookmark, x);

            index++;
        }

        if (bookmarks.isEmpty()) {
            renderWhenNoBookmarks();
        } else {
            renderPaginationButtons(index);
        }

        renderedButtons = true;
    }

    private void renderTitleDetails(DrawContext context, BookmarkData bookmark, int x) {
        var pose = context.getMatrices();
        var name = bookmark.name();
        var extra = bookmark.extra();
        var author = extra.author();
        var timestamp = extra.timestamp();
        var date = new Date(timestamp * 1000L);
        var format = new SimpleDateFormat(DATE_FORMAT);
        var titleColor = 0x444444;
        var extraColor = 0xa7a7a7;
        var maxTitleLength = 25;
        var left = -100 + (x * 169);
        var top = 33;

        // Render top text.
        pose.push();
        pose.translate(midX - 40f, 20f, 1.0f);
        pose.scale(0.7f, 0.7f, 1.0f);

        if (name.length() > maxTitleLength) {
            name = name.substring(0, maxTitleLength - 1);
        }

        context.drawText(textRenderer, Text.literal(name).formatted(Formatting.BOLD), left, top, titleColor, false);

        if (!author.isEmpty()) {
            top += 12;
            context.drawText(textRenderer, Text.translatable(Resources.CREATED_BY_KEY, author), left, top, extraColor, false);
        }

        if (timestamp >= 0) {
            top += 12;
            context.drawText(textRenderer, Text.literal(format.format(date)), left, top, extraColor, false);
        }
        pose.pop();
    }

    private void renderPhoto(DrawContext context, BookmarkData bookmark, int x) {
        var pose = context.getMatrices();

        var resource = TravelJournalClient.handlers.tryLoadPhoto(bookmark.id());
        if (resource != null) {
            var top = 127;
            var left = -168 + (x * 272);
            pose.push();
            pose.translate(midX - 40f, 40f, 1.0f);
            pose.scale(0.42f, 0.24f, 1.0f);
            RenderSystem.setShaderTexture(0, resource);
            context.drawTexture(resource, left, top, 0, 0, 256, 256);
            pose.pop();
        }
    }

    private void renderPhotoDescriptionHover(DrawContext context, BookmarkData bookmark, int mouseX, int mouseY, int x) {
        var description = bookmark.extra().description();
        if (description.isEmpty()) return;

        var x1 = midX - 110 + (x * 114);
        var y1 = 71;
        var x2 = midX - 110 + (x * 114) + 106;
        var y2 = 131;

        if (mouseX >= x1 && mouseX <= x2
                && mouseY >= y1 && mouseY <= y2) {
            context.drawTooltip(textRenderer, Helpers.wrap(description), Optional.empty(), mouseX, mouseY);
        }
    }

    private void renderPosition(DrawContext context, BookmarkData bookmark, int x) {
        var pose = context.getMatrices();
        var top = 202;
        var left = -95 + (x * 163);
        var color = 0xb8907a;

        pose.push();
        pose.translate(midX - 25f, 20f, 1.0f);
        pose.scale(0.7f, 0.7f, 1.0f);

        var positionText = Helpers.positionAsText(bookmark.pos());
        var dimensionText = Helpers.dimensionAsText(bookmark.dimension());

        ClientHelpers.drawCenteredString(context, textRenderer, dimensionText, left + 50, top, color, false);
        ClientHelpers.drawCenteredString(context, textRenderer, positionText, left + 50, top + 13, color, false);
        pose.pop();
    }

    private void renderDetailsButton(BookmarkData bookmark, int x) {
        var left = midX - 110 + (x * 114);
        var top = 135;

        if (!renderedButtons) {
            addDrawableChild(new CoreButtons.EditButton(left, top, 107,
                    b -> TravelJournalClient.handlers.openBookmark(stack, bookmark), Resources.DETAILS));
        }
    }

    private void renderWhenNoBookmarks() {
        if (!renderedButtons) {
            addDrawableChild(new Buttons.NewWhenEmptyButton(midX - (Buttons.NewWhenEmptyButton.WIDTH / 2), 45,
                    b -> TravelJournalClient.handlers.makeNewBookmark()));
        }
    }

    private void renderPaginationButtons(int index) {
        var size = bookmarks().size();
        var pages = size / columns;

        if (!renderedButtons) {
            if (page > 1) {
                addDrawableChild(new CoreButtons.PreviousPageButton(midX - 110, 180,
                        b -> TravelJournalClient.handlers.openBookmarks(stack, page - 1)));
            }
            if (page < pages || index < size) {
                addDrawableChild(new CoreButtons.NextPageButton(midX + 80, 180,
                        b -> TravelJournalClient.handlers.openBookmarks(stack, page + 1)));
            }
        }
    }

    private JournalData journal() {
        return JournalData.get(stack);
    }

    private List<BookmarkData> bookmarks() {
        return journal().bookmarks();
    }
}
