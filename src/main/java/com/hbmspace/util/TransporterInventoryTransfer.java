package com.hbmspace.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public final class TransporterInventoryTransfer {
    private TransporterInventoryTransfer() {}

    public static boolean pullOne(IItemHandler source, IItemHandler destination, int inputSlots) {
        for (int i = 0; i < source.getSlots(); i++) {
            ItemStack stack = source.extractItem(i, 1, true);
            if (stack.isEmpty()) continue;
            for (int j = 0; j < inputSlots; j++) {
                if (destination.insertItem(j, stack, true).isEmpty()) {
                    ItemStack extracted = source.extractItem(i, 1, false);
                    if (extracted.isEmpty()) return false;
                    destination.insertItem(j, extracted, false);
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean pushOne(IItemHandler source, int outputStart, int outputEnd, IItemHandler destination) {
        for (int i = outputStart; i < outputEnd; i++) {
            ItemStack out = source.getStackInSlot(i);
            if (out.isEmpty()) continue;
            for (int j = 0; j < destination.getSlots(); j++) {
                ItemStack copy = out.copy();
                copy.setCount(1);
                if (destination.insertItem(j, copy, true).isEmpty()
                        && destination.insertItem(j, copy, false).isEmpty()) {
                    source.extractItem(i, 1, false);
                    return true;
                }
            }
        }
        return false;
    }
}
