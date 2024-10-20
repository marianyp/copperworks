package dev.mariany.copperworks.item.custom;

import dev.mariany.copperworks.block.ModProperties;
import dev.mariany.copperworks.block.custom.ComparatorMirrorBlock;
import dev.mariany.copperworks.block.custom.MufflerBlock;
import dev.mariany.copperworks.block.entity.custom.EnhancedSculkSensorBlockEntity;
import dev.mariany.copperworks.item.component.CopperworksComponents;
import dev.mariany.copperworks.sound.ModSoundEvents;
import dev.mariany.copperworks.util.ModConstants;
import dev.mariany.copperworks.util.ModUtils;
import net.minecraft.block.*;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class WrenchItem extends Item {
    public WrenchItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isOf(Items.COPPER_INGOT);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        List<Integer> frequencyWhitelist = stack.get(CopperworksComponents.COPIED_FREQUENCY_WHITELIST);
        Integer range = stack.get(CopperworksComponents.COPIED_RANGE);
        if (frequencyWhitelist != null) {
            Text formattedFrequencies = frequencyWhitelist.isEmpty() ? Text.translatable(
                    "item.copperworks.wrench.copied_frequencies.none.tooltip") : Text.of(
                    frequencyWhitelist.stream().map(String::valueOf).collect(Collectors.joining(", ")));
            tooltip.add(Text.translatable("item.copperworks.wrench.copied_frequencies.tooltip", formattedFrequencies)
                    .withColor(Colors.GRAY));
        }

        if (range != null) {
            tooltip.add(
                    Text.translatable("item.copperworks.wrench.copied_range.tooltip", range).withColor(Colors.GRAY));
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();
        ItemStack itemStack = context.getStack();
        BlockPos blockPos = context.getBlockPos();
        Direction side = context.getSide();
        BlockState blockState = world.getBlockState(blockPos);

        if (wrench(world, player, itemStack, blockState, blockPos, side)) {
            damage(itemStack, player, hand);
            world.playSoundFromEntity(null, player, ModSoundEvents.WRENCH, SoundCategory.NEUTRAL, 0.24F,
                    MathHelper.nextBetween(world.random, 0.8F, 1F));
            return ActionResult.success(world.isClient);
        }

        return super.useOnBlock(context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (itemStack.contains(CopperworksComponents.COPIED_FREQUENCY_WHITELIST) && user.isSneaking()) {
            itemStack.remove(CopperworksComponents.COPIED_FREQUENCY_WHITELIST);
            itemStack.remove(CopperworksComponents.COPIED_RANGE);
            user.sendMessage(Text.translatable("item.copperworks.wrench.cleared_settings.tooltip"), true);
            if (world.isClient) {
                user.swingHand(hand);
            }
        }
        return super.use(world, user, hand);
    }

    private boolean wrench(World world, @Nullable PlayerEntity player, ItemStack itemStack, BlockState blockState,
                           BlockPos blockPos, Direction side) {
        Block block = blockState.getBlock();

        if (block instanceof ComparatorMirrorBlock) {
            boolean locked = blockState.get(Properties.LOCKED);
            world.setBlockState(blockPos, blockState.with(Properties.LOCKED, !locked));
            world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, blockPos);
            return true;
        }

        if (block instanceof MufflerBlock) {
            boolean increment = player != null && player.isSneaking();
            int incrementAmount = increment ? 1 : -1;
            int currentMufflerRange = blockState.get(ModProperties.MUFFLER_RANGE);
            int newMufflerRange = ModUtils.wrapIncrement(currentMufflerRange, 1, ModConstants.MAX_MUFFLER_RANGE,
                    incrementAmount);
            world.setBlockState(blockPos, blockState.with(ModProperties.MUFFLER_RANGE, newMufflerRange));
            world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, blockPos);
            if (player != null) {
                player.sendMessage(Text.translatable("item.copperworks.wrench.muffler_range_changed", newMufflerRange),
                        true);
            }
            return true;
        }

        if (world.getBlockEntity(blockPos) instanceof EnhancedSculkSensorBlockEntity enhancedSculkSensorBlockEntity) {
            if (player != null) {
                if (player.isSneaking() && itemStack.contains(CopperworksComponents.COPIED_FREQUENCY_WHITELIST)) {
                    List<Integer> frequencyWhitelist = itemStack.getOrDefault(
                            CopperworksComponents.COPIED_FREQUENCY_WHITELIST, List.of());
                    Integer range = itemStack.get(CopperworksComponents.COPIED_RANGE);
                    enhancedSculkSensorBlockEntity.setFrequencyWhitelist(frequencyWhitelist);
                    if (range != null) {
                        enhancedSculkSensorBlockEntity.setRange(range);
                    }
                    player.sendMessage(Text.translatable("item.copperworks.wrench.applied_settings"), true);
                } else {
                    List<Integer> frequencyWhitelist = enhancedSculkSensorBlockEntity.getFrequencyWhitelist().stream()
                            .toList();
                    itemStack.set(CopperworksComponents.COPIED_FREQUENCY_WHITELIST, frequencyWhitelist);
                    itemStack.set(CopperworksComponents.COPIED_RANGE, enhancedSculkSensorBlockEntity.getRange());
                    player.sendMessage(Text.translatable("item.copperworks.wrench.copied_settings"), true);
                }
            }
            return true;
        }

        boolean counterClockWise = player != null && player.isSneaking();
        Optional<BlockState> optionalRotatedBlockState = getRotatedBlockState(side, blockState, !counterClockWise);

        if (optionalRotatedBlockState.isPresent()) {
            BlockState rotatedBlockState = optionalRotatedBlockState.get();
            world.setBlockState(blockPos, rotatedBlockState);
            world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, blockPos);
            return true;
        }

        return false;
    }

    private void damage(ItemStack itemStack, @Nullable PlayerEntity player, Hand hand) {
        if (player != null) {
            EquipmentSlot slot = hand.equals(Hand.MAIN_HAND) ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            itemStack.damage(1, player, slot);
        }
    }

    // Credit: https://github.com/MehVahdJukaar/Supplementaries/blob/master/common/src/main/java/net/mehvahdjukaar/supplementaries/common/utils/BlockUtil.java#L71
    private Optional<BlockState> getRotatedBlockState(Direction direction, BlockState state, boolean clockwise) {
        BlockRotation rotation = clockwise ? BlockRotation.CLOCKWISE_90 : BlockRotation.COUNTERCLOCKWISE_90;
        Block block = state.getBlock();

        // Horizontal Facing Blocks
        if (direction.getAxis() == Direction.Axis.Y) {
            BlockState rotated = state.rotate(rotation);
            if (rotated == state) {
                rotated = rotateVertical(state, rotated, rotation);
            }
            if (rotated != state) {
                return Optional.of(rotated);
            }
        } else if (state.contains(Properties.BLOCK_FACE) && state.contains(Properties.HORIZONTAL_FACING)) {
            Optional<BlockState> optionalRotated = getRotatedHorizontalFaceBlock(state, direction, clockwise);
            if (optionalRotated.isPresent()) {
                return optionalRotated;
            }
        }

        // 6 Direction / Directional Blocks
        if (state.contains(Properties.FACING)) {
            return getRotatedDirectionalBlock(state, direction, clockwise);
        }

        // Axis Blocks
        if (state.contains(Properties.AXIS)) {
            Direction.Axis targetAxis = state.get(Properties.AXIS);
            Direction.Axis myAxis = direction.getAxis();
            if (myAxis == Direction.Axis.X) {
                return Optional.of(state.with(Properties.AXIS,
                        targetAxis == Direction.Axis.Y ? Direction.Axis.Z : Direction.Axis.Y));
            } else if (myAxis == Direction.Axis.Z) {
                return Optional.of(state.with(Properties.AXIS,
                        targetAxis == Direction.Axis.Y ? Direction.Axis.X : Direction.Axis.Y));
            }
        }

        // Stairs
        if (block instanceof StairsBlock) {
            return getRotatedStairs(state, direction, clockwise);
        }

        // Slabs
        if (state.contains(SlabBlock.TYPE)) {
            SlabType type = state.get(SlabBlock.TYPE);
            if (type == SlabType.DOUBLE) {
                return Optional.empty();
            }
            return Optional.of(state.with(SlabBlock.TYPE, type == SlabType.BOTTOM ? SlabType.TOP : SlabType.BOTTOM));
        }

        // Trapdoors
        if (state.contains(TrapdoorBlock.HALF)) {
            return Optional.of(state.cycle(TrapdoorBlock.HALF));
        }

        return Optional.empty();
    }

    private static BlockState rotateVertical(BlockState state, BlockState rotated, BlockRotation rotation) {
        if (state.contains(Properties.FACING)) {
            rotated = state.with(Properties.FACING, rotation.rotate(state.get(Properties.FACING)));
        } else if (state.contains(Properties.HORIZONTAL_FACING)) {
            rotated = state.with(Properties.HORIZONTAL_FACING,
                    rotation.rotate(state.get(Properties.HORIZONTAL_FACING)));
        } else if (state.contains(PillarBlock.AXIS)) {
            rotated = PillarBlock.changeRotation(state, rotation);
        } else if (state.contains(Properties.HORIZONTAL_AXIS)) {
            rotated = state.cycle(Properties.HORIZONTAL_AXIS);
        }
        return rotated;
    }

    private Optional<BlockState> getRotatedHorizontalFaceBlock(BlockState original, Direction axis, boolean clockwise) {
        Direction facingDirection = original.get(Properties.HORIZONTAL_FACING);

        BlockFace face = original.get(Properties.BLOCK_FACE);

        return Optional.of(switch (face) {
            case FLOOR -> original.with(Properties.BLOCK_FACE, BlockFace.WALL).with(Properties.HORIZONTAL_FACING,
                    clockwise ? axis.rotateCounterclockwise(axis.getAxis()) : axis.rotateClockwise(axis.getAxis()));
            case CEILING -> original.with(Properties.BLOCK_FACE, BlockFace.WALL).with(Properties.HORIZONTAL_FACING,
                    !clockwise ? axis.rotateCounterclockwise(axis.getAxis()) : axis.rotateClockwise(axis.getAxis()));
            case WALL -> {
                clockwise = clockwise ^ (axis.getDirection() != Direction.AxisDirection.POSITIVE);
                yield original.with(Properties.BLOCK_FACE,
                        (facingDirection.getDirection() == Direction.AxisDirection.POSITIVE) ^ clockwise ? BlockFace.CEILING : BlockFace.FLOOR);
            }
        });
    }

    private Optional<BlockState> getRotatedDirectionalBlock(BlockState state, Direction axis, boolean clockwise) {
        Vec3d normal = Vec3d.of(axis.getVector());
        Vec3d targetNormal = Vec3d.of(state.get(Properties.FACING).getVector());

        if (clockwise) {
            targetNormal = targetNormal.multiply(-1);
        }

        Vec3d rotated = normal.crossProduct(targetNormal);

        if (!rotated.equals(Vec3d.ZERO)) {
            Direction newDirection = Direction.getFacing(rotated.x, rotated.y, rotated.z);
            return Optional.of(state.with(Properties.FACING, newDirection));
        }

        return Optional.empty();
    }

    public static Optional<BlockState> getRotatedStairs(BlockState state, Direction axis, boolean clockwise) {
        Direction facing = state.get(StairsBlock.FACING);

        if (facing.getAxis() == axis.getAxis()) {
            return Optional.empty();
        }

        boolean flipped = axis.getDirection() == Direction.AxisDirection.POSITIVE ^ !clockwise;
        BlockHalf half = state.get(StairsBlock.HALF);
        boolean top = half == BlockHalf.TOP;
        boolean positive = facing.getDirection() == Direction.AxisDirection.POSITIVE;

        if ((top ^ positive) ^ flipped) {
            half = top ? BlockHalf.BOTTOM : BlockHalf.TOP;
        } else {
            facing = facing.getOpposite();
        }

        return Optional.of(state.with(StairsBlock.HALF, half).with(StairsBlock.FACING, facing));
    }
}
