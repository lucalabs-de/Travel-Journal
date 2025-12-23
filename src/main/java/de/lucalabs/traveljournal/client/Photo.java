package de.lucalabs.traveljournal.client;

import de.lucalabs.traveljournal.TravelJournal;
import de.lucalabs.traveljournal.TravelJournalClient;
import de.lucalabs.traveljournal.sounds.TravelJournalSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.ScreenshotRecorder;

import java.util.UUID;

/**
 * Handles the countdown while taking a photo and the screenshot function when countdown is done.
 */
public class Photo {
    private final UUID journalId; // The journal that this photo belongs to
    private final UUID photoId; // The bookmark that this photo belongs to
    private int ticks;
    private boolean valid;
    private boolean isTakingPhoto;

    public Photo(UUID journalId, UUID photoId) {
        this.journalId = journalId;
        this.photoId = photoId;
        this.valid = true;
        this.isTakingPhoto = false;
    }

    public UUID journalId() {
        return journalId;
    }

    public UUID photoId() {
        return photoId;
    }

    public void tick() {
        ticks++;

        if (ticks < 60) {
            return;
        }

        if (ticks < 62) {
            hideGui();
            return;
        }

        if (ticks > 70) {
            // Escape if something is wrong.
            valid = false;
            return;
        }

        takePhoto();
    }

    public void finish() {
        showGui();

        var minecraft = MinecraftClient.getInstance();
        if (minecraft.player != null) {
            minecraft.player.playSound(TravelJournalSounds.PHOTO, 1.0F, 1.0F);
        }

        // Move the screenshot into the custom photos folder.
        TravelJournalClient.handlers.moveScreenshotIntoPhotosDir(photoId);

        valid = false;
    }

    public boolean isValid() {
        return valid;
    }

    private void takePhoto() {
        var minecraft = MinecraftClient.getInstance();
        if (minecraft.player == null) {
            return;
        }

        // Give up if there's a screen in the way. We don't want a photo of that!
        if (minecraft.currentScreen != null) {
            showGui();
            return;
        }

        if (isTakingPhoto) {
            return;
        }

        isTakingPhoto = true;

        ScreenshotRecorder.saveScreenshot(
                minecraft.runDirectory,
                photoId + ".png",
                minecraft.getFramebuffer(),
                component -> {
                    TravelJournal.LOGGER.info("Photo taken for bookmarkId: {}", photoId);
                    finish();
                }
        );
    }

    public void renderCountdown(DrawContext context) {
        var minecraft = MinecraftClient.getInstance();
        int x = (context.getScaledWindowWidth() / 8) + 1;
        int y = 20;
        String str = "";

        if (ticks <= 20) {
            str = "3";
        } else if (ticks <= 40) {
            str = "2";
        } else if (ticks <= 60) {
            str = "1";
        }

        if (!str.isEmpty()) {
            var pose = context.getMatrices();
            pose.push();
            pose.scale(4.0f, 4.0f, 1.0f);
            context.drawCenteredTextWithShadow(minecraft.textRenderer, str, x, y, 0xffffff);
            pose.pop();
        }
    }

    private void hideGui() {
        MinecraftClient.getInstance().options.hudHidden = true;
    }

    private void showGui() {
        MinecraftClient.getInstance().options.hudHidden = false;
    }
}
