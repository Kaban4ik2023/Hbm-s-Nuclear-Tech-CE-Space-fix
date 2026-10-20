package com.hbmspace.mixin.mod.hbm.tileentities;

import com.hbm.tileentity.TileEntityProxyBase;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbmspace.blocks.machine.BlockTransporterRocket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TileEntityProxyCombo.class)
public abstract class MixinTileEntityProxyCombo extends TileEntityProxyBase {
    @Shadow(remap = false) boolean inventory;

    @Override
    public void onLoad() {
        super.onLoad();
        // Existing pads persisted inv=false before their ports exposed items.
        if (!inventory && getBlockType() instanceof BlockTransporterRocket) {
            inventory = true;
            markDirty();
        }
    }
}
