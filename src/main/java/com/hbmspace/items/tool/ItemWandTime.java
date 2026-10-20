package com.hbmspace.items.tool;

import net.minecraft.util.text.TextComponentTranslation;

import com.hbm.util.I18nUtil;

import com.hbmspace.util.AstronomyUtil;
import com.hbmspace.items.ItemBakedSpace;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemWandTime extends ItemBakedSpace {

    public ItemWandTime(String s) {
        super(s);
    }

    @Override
    public void addInformation(@NotNull ItemStack stack, @Nullable World worldIn, List<String> tooltip, @NotNull ITooltipFlag flagIn) {
        tooltip.add(I18nUtil.resolveKey("hbmspace.time_wand.creative"));
        tooltip.add(TextFormatting.ITALIC + I18nUtil.resolveKey("hbmspace.time_wand.quote"));
        tooltip.add(I18nUtil.resolveKey("hbmspace.time_wand.server"));
    }

    @Override
    public @NotNull EnumActionResult onItemUse(@NotNull EntityPlayer player, World worldIn, @NotNull BlockPos pos, @NotNull EnumHand hand, @NotNull EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) return EnumActionResult.SUCCESS;

        if (player.isSneaking()) {
            AstronomyUtil.TIME_MULTIPLIER /= 2;
            if (AstronomyUtil.TIME_MULTIPLIER < 1) AstronomyUtil.TIME_MULTIPLIER = 1;
        } else {
            AstronomyUtil.TIME_MULTIPLIER *= 2;
        }

        player.sendMessage(new TextComponentTranslation("hbmspace.time_wand.multiplier", AstronomyUtil.TIME_MULTIPLIER));

        return EnumActionResult.SUCCESS;
    }

}
