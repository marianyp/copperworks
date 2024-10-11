package dev.mariany.copperworks.item.custom;

import dev.mariany.copperworks.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Oxidizable;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Optional;

public class PatinaItem extends BlockItem {
    public PatinaItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemStack itemStack = context.getStack();
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        Direction side = context.getSide();

        if (block instanceof Oxidizable) {
            Optional<Block> optionalIncreasedOxidationBlock = Oxidizable.getIncreasedOxidationBlock(block);

            if (optionalIncreasedOxidationBlock.isPresent()) {
                Block increasedOxidationBlock = optionalIncreasedOxidationBlock.get();
                world.setBlockState(blockPos, increasedOxidationBlock.getDefaultState());

                itemStack.decrement(1);

                playSound(world, blockPos);

                return ActionResult.success(world.isClient);
            }
        } else if (!side.equals(Direction.DOWN)) {
            BlockPos belowPos = blockPos.down();
            BlockState belowState = world.getBlockState(belowPos);
            if (belowState.isReplaceable() && belowState.canPlaceAt(world, belowPos)) {
                world.setBlockState(belowPos, ModBlocks.PATINA.getDefaultState());
                playSound(world, belowPos);
                return ActionResult.success(world.isClient);
            }
        }

        return super.useOnBlock(context);
    }

    private void playSound(World world, BlockPos blockPos) {
        world.playSound(null, blockPos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS);
    }
}
