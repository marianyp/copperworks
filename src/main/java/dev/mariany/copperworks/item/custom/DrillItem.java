package dev.mariany.copperworks.item.custom;

import dev.mariany.copperworks.sound.ModSoundEvents;
import dev.mariany.copperworks.tag.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class DrillItem extends MiningToolItem {
    public DrillItem(ToolMaterial material, Item.Settings settings) {
        super(material, ModTags.Blocks.DRILLABLE, settings);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (super.postMine(stack, world, state, pos, miner)) {
            world.playSoundFromEntity(null, miner, ModSoundEvents.DRILL, SoundCategory.NEUTRAL, 1F,
                    (float) MathHelper.nextDouble(miner.getRandom(), 0.7, 1.3));
            return true;
        }

        return false;
    }
}