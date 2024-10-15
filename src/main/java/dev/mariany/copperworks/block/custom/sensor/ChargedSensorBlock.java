package dev.mariany.copperworks.block.custom.sensor;

import com.mojang.serialization.MapCodec;
import dev.mariany.copperworks.block.ModProperties;
import dev.mariany.copperworks.block.entity.ModBlockEntities;
import dev.mariany.copperworks.block.entity.custom.SensorBlockEntity;
import dev.mariany.copperworks.util.ModConstants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallMountedBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ChargedSensorBlock extends AbstractSensorBlock {
    public static final MapCodec<ChargedSensorBlock> CODEC = ChargedSensorBlock.createCodec(ChargedSensorBlock::new);

    public static final IntProperty POWER = Properties.POWER;
    public static final IntProperty RANGE = ModProperties.RANGE;

    private static final int DEFAULT_RANGE = 5;

    public ChargedSensorBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
                                             PlayerEntity player, Hand hand, BlockHitResult hit) {
        int range = state.get(RANGE);
        if (range < ModConstants.MAX_SENSOR_RANGE && stack.getItem().equals(Items.DRAGON_BREATH)) {
            int newRange = range + 1;
            world.setBlockState(pos, state.with(RANGE, newRange), Block.NOTIFY_ALL);
            stack.decrementUnlessCreative(1, player);
            playFillSound(world, pos);
            player.sendMessage(Text.translatable("block.copperworks.sensor_charged.upgraded", newRange), true);
            return ItemActionResult.success(world.isClient);
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (player.getMainHandStack().isEmpty()) {
            player.sendMessage(Text.translatable("block.copperworks.sensor_charged.range", getRange(state)), true);
            return ActionResult.success(world.isClient);
        }
        return super.onUse(state, world, pos, player, hit);
    }

    public static int getRange(BlockState state) {
        return state.contains(RANGE) ? state.get(RANGE) : 0;
    }

    private static void playFillSound(World world, BlockPos pos) {
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.NEUTRAL,
                0.5F, 1.5F);
    }

    @Override
    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWER);
    }

    @Override
    protected int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return direction == getDirection(state) ? state.getWeakRedstonePower(world, pos, direction) : 0;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(POWER, RANGE);
    }

    @Override
    protected BlockState applyDefaultState(BlockState state) {
        return super.applyDefaultState(state).with(POWER, 0).with(RANGE, DEFAULT_RANGE);
    }

    public static Direction getDirection(BlockState state) {
        return WallMountedBlock.getDirection(state);
    }

    @Override
    protected MapCodec<? extends ChargedSensorBlock> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SensorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
                                                                  BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.SENSOR,
                (world1, pos, blockState, blockEntity) -> blockEntity.tick(world1, pos, blockState));
    }
}
