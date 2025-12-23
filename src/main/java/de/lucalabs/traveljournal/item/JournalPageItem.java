package de.lucalabs.traveljournal.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;

public class JournalPageItem extends Item {
    public JournalPageItem() {
        super(new FabricItemSettings().maxCount(1));
    }
}
