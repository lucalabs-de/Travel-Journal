package de.lucalabs.traveljournal.common;

import de.lucalabs.traveljournal.TravelJournal;
import de.lucalabs.traveljournal.TravelJournalClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class CoreButtons {

    public static class CloseButton extends ButtonWidget {
        public static int WIDTH = 110;
        public static int HEIGHT = 20;
        static Text TEXT = CoreResources.CLOSE;

        public CloseButton(int x, int y, PressAction PressAction) {
            super(x, y, WIDTH, HEIGHT, TEXT, PressAction, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        }
    }

    public static class BackButton extends ButtonWidget {
        public static int WIDTH = 100;
        public static int HEIGHT = 20;
        static Text TEXT = CoreResources.BACK;

        public BackButton(int x, int y, PressAction PressAction) {
            super(x, y, WIDTH, HEIGHT, TEXT, PressAction, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        }
    }

    public static class SaveButton extends ButtonWidget {
        public static int WIDTH = 100;
        public static int HEIGHT = 20;
        static Text TEXT = CoreResources.SAVE;

        public SaveButton(int x, int y, PressAction PressAction) {
            super(x, y, WIDTH, HEIGHT, TEXT, PressAction, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        }
    }

    public static class CancelButton extends ButtonWidget {
        public static int WIDTH = 100;
        public static int HEIGHT = 20;
        static Text TEXT = CoreResources.CANCEL;

        public CancelButton(int x, int y, PressAction PressAction) {
            super(x, y, WIDTH, HEIGHT, TEXT, PressAction, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        }
    }

    public static class DeleteButton extends ButtonWidget {
        public static int WIDTH = 100;
        public static int HEIGHT = 20;
        static Text TEXT = CoreResources.DELETE;

        public DeleteButton(int x, int y, PressAction PressAction) {
            super(x, y, WIDTH, HEIGHT, TEXT, PressAction, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        }
    }

    public static class EditButton extends ButtonWidget {
        public static int HEIGHT = 20;

        public EditButton(int x, int y, int width, PressAction PressAction, Text text) {
            super(x, y, width, HEIGHT, text, PressAction, ButtonWidget.DEFAULT_NARRATION_SUPPLIER);
        }
    }

    public static class NextPageButton extends TexturedButtonWidget {
        public static int WIDTH = 20;
        public static int HEIGHT = 19;
        static Identifier TEXTURE = TravelJournal.id("textures/gui/sprites/widget/core/next_page_button");
        static Text TEXT = CoreResources.NEXT_PAGE;

        public NextPageButton(int x, int y, PressAction PressAction) {
            super(x, y, WIDTH, HEIGHT, 0, 0, HEIGHT, TEXTURE, PressAction);
            setTooltip(Tooltip.of(TEXT));
        }
    }

    public static class PreviousPageButton extends TexturedButtonWidget {
        public static int WIDTH = 20;
        public static int HEIGHT = 19;
        static Identifier TEXTURE = TravelJournal.id("textures/gui/sprites/widget/core/previous_page_button");
        static Text TEXT = CoreResources.PREVIOUS_PAGE;

        public PreviousPageButton(int x, int y, PressAction PressAction) {
            super(x, y, WIDTH, HEIGHT, 0, 0, HEIGHT, TEXTURE, PressAction);
            setTooltip(Tooltip.of(TEXT));
        }
    }
}
