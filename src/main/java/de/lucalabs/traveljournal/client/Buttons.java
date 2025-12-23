package de.lucalabs.traveljournal.client;

import de.lucalabs.traveljournal.TravelJournal;
import de.lucalabs.traveljournal.common.Resources;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class Buttons {

    public static class NewWhenEmptyButton extends ButtonWidget {
        public static int WIDTH = 100;
        public static int HEIGHT = 20;
        static Text TEXT = Resources.NEW_BOOKMARK;

        public NewWhenEmptyButton(int x, int y, PressAction onPress) {
            super(x, y, WIDTH, HEIGHT, TEXT, onPress, DEFAULT_NARRATION_SUPPLIER);
        }
    }

    public static class NewBookmarkButton extends ButtonWidget {
        public static int WIDTH = 110;
        public static int HEIGHT = 20;
        static Text TEXT = Resources.NEW_BOOKMARK;

        public NewBookmarkButton(int x, int y, PressAction onPress) {
            super(x, y, WIDTH, HEIGHT, TEXT, onPress, DEFAULT_NARRATION_SUPPLIER);
        }
    }

    public static class ExportMapButton extends TexturedButtonWidget {
        public static int WIDTH = 20;
        public static int HEIGHT = 18;
        static Identifier TEXTURE = TravelJournal.id("textures/gui/sprites/widget/travel_journals/map_button");
        static Text TEXT = Resources.EXPORT_MAP;

        public ExportMapButton(int x, int y, PressAction onPress) {
            super(x, y, WIDTH, HEIGHT, 0, 0, HEIGHT, TEXTURE, onPress);
            setTooltip(Tooltip.of(TEXT));
        }
    }

    public static class ExportPageButton extends TexturedButtonWidget {
        public static int WIDTH = 20;
        public static int HEIGHT = 18;
        static Identifier TEXTURE = TravelJournal.id("textures/gui/sprites/widget/travel_journals/page_button");
        static Text TEXT = Resources.EXPORT_PAGE;

        public ExportPageButton(int x, int y, PressAction onPress) {
            super(x, y, WIDTH, HEIGHT, 0, 0, HEIGHT, TEXTURE, onPress);
            setTooltip(Tooltip.of(TEXT));
        }
    }
}
