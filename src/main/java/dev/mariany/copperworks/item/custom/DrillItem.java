package dev.mariany.copperworks.item.custom;

import dev.mariany.copperworks.item.component.ModComponents;
import dev.mariany.copperworks.sound.ModSoundEvents;
import dev.mariany.copperworks.tag.ModTags;
import dev.mariany.copperworks.util.ModUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class DrillItem extends MiningToolItem {
    public DrillItem(ToolMaterial material, Item.Settings settings, int maxCharge, int chargeRate) {
        super(material, ModTags.Blocks.DRILLABLE,
                settings.component(ModComponents.CHARGE, 0).component(ModComponents.MAX_CHARGE, maxCharge)
                        .component(ModComponents.CHARGE_RATE, chargeRate));
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (super.postMine(stack, world, state, pos, miner)) {
            if (miner.getRandom().nextBoolean()) {
                Integer charge = stack.get(ModComponents.CHARGE);

                if (charge != null) {
                    stack.set(ModComponents.CHARGE, charge - MathHelper.nextInt(miner.getRandom(), 0, 2));
                }
            }

            world.playSoundFromEntity(null, miner, ModSoundEvents.DRILL, SoundCategory.NEUTRAL, 1F,
                    (float) MathHelper.nextDouble(miner.getRandom(), 0.7, 1.3));
            return true;
        }

        return false;
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        if (!ModUtils.itemHasSomeCharge(miner.getMainHandStack())) {
            return false;
        }
        return super.canMine(state, world, pos, miner);
    }

    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        if (!ModUtils.itemHasSomeCharge(stack)) {
            return 0;
        }

        return super.getMiningSpeed(stack, state);
    }
}