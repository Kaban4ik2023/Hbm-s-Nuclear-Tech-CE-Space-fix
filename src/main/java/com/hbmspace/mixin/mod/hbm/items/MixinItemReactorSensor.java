package com.hbmspace.mixin.mod.hbm.items;

import com.hbm.blocks.BlockDummyable;
import com.hbm.items.machine.ItemReactorSensor;
import com.hbmspace.blocks.machine.MachineDishControl;
import com.hbmspace.blocks.machine.MachineStardar;
import com.hbmspace.tileentity.machine.TileEntityDishControl;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemReactorSensor.class)
public class MixinItemReactorSensor {
    @Inject(method = "onItemUse", at = @At("HEAD"), cancellable = true)
    private void linkStarDar(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
                            EnumFacing facing, float hitX, float hitY, float hitZ,
                            CallbackInfoReturnable<EnumActionResult> cir) {
        Block block = world.getBlockState(pos).getBlock();
        if (!(block instanceof MachineStardar) && !(block instanceof MachineDishControl)) return;
        if (world.isRemote) {
            cir.setReturnValue(EnumActionResult.SUCCESS);
            return;
        }

        int[] core = ((BlockDummyable) block).findCore(world, pos.getX(), pos.getY(), pos.getZ());
        if (core == null) {
            cir.setReturnValue(EnumActionResult.FAIL);
            return;
        }
        ItemStack stack = player.getHeldItem(hand);
        if (block instanceof MachineStardar) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag == null) tag = new NBTTagCompound();
            tag.setInteger("x", core[0]);
            tag.setInteger("y", core[1]);
            tag.setInteger("z", core[2]);
            tag.setInteger("hbmspaceDimension", world.provider.getDimension());
            stack.setTagCompound(tag);
            player.sendMessage(new TextComponentString("StarDar position set!"));
            cir.setReturnValue(EnumActionResult.SUCCESS);
        } else {
            TileEntity tile = world.getTileEntity(new BlockPos(core[0], core[1], core[2]));
            boolean linked = tile instanceof TileEntityDishControl && ((TileEntityDishControl) tile).tryLink(stack);
            player.sendMessage(new TextComponentString(linked ? "StarDar linked!" : "Select a StarDar in this dimension first."));
            cir.setReturnValue(linked ? EnumActionResult.SUCCESS : EnumActionResult.FAIL);
        }
    }
}
