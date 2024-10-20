package dev.mariany.copperworks.block.custom.relay.bound;

import com.mojang.serialization.MapCodec;
import dev.mariany.copperworks.block.custom.relay.AbstractRelayBlock;
import dev.mariany.copperworks.block.entity.ModBlockEntities;
import dev.mariany.copperworks.block.entity.custom.relay.BoundRelayBlockEntity;
import dev.mariany.copperworks.item.custom.WrenchItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalInt;

public class BoundRelayBlock extends AbstractRelayBlock implements BindableRelay {
    public static final MapCodec<BoundRelayBlock> CODEC = BoundRelayBlock.createCodec(BoundRelayBlock::new);
    public static final IntProperty POWER = Properties.POWER;
    public static final BooleanProperty POWERED = Properties.POWERED;

    public BoundRelayBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(POWERED, Boolean.FALSE).with(POWER, 0));
    }

    public static int getRenderDistance() {
        return 256;
    }

    public static boolean isInRenderDistance(Vec3d entityPos, Vec3d viewerPos) {
        return entityPos.multiply(1.0, 0.0, 1.0).isInRange(viewerPos.multiply(1.0, 0.0, 1.0), getRenderDistance());
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
        if (!(newState.getBlock() instanceof BoundRelayBlock)) {
            if (!world.isClient) {
                world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                        RegistryEntry.of(SoundEvents.BLOCK_AMETHYST_CLUSTER_BREAK), SoundCategory.BLOCKS, 1F, 1F,
                        world.getRandom().nextLong());
            }
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        ItemStack heldItem = player.getMainHandStack();
        if (!heldItem.isEmpty() && !(heldItem.getItem() instanceof WrenchItem)) {
            return ActionResult.PASS;
        }
        if (world.getBlockEntity(pos) instanceof BoundRelayBlockEntity boundRelayBlockEntity) {
            if (world.isClient) {
                if (player.equals(MinecraftClient.getInstance().player)) {
                    BoundRelayClientData clientData = boundRelayBlockEntity.getClientData();
                    Optional<GlobalPos> optionalBoundGlobalPos = boundRelayBlockEntity.getBoundPos();
                    Optional<BoundRelayClientData> optionalBoundClientData = boundRelayBlockEntity.getBoundClientData();

                    if (optionalBoundGlobalPos.isPresent() && optionalBoundClientData.isPresent()) {
                        GlobalPos boundGlobalPos = optionalBoundGlobalPos.get();
                        BlockPos boundBlockPos = boundGlobalPos.pos();
                        BoundRelayClientData boundRelayClientData = optionalBoundClientData.get();

                        boolean visible = isInRenderDistance(boundBlockPos.toCenterPos(), player.getPos());

                        if (visible) {
                            clientData.show(BoundRelayClientData.PURPLE);
                            boundRelayClientData.show(BoundRelayClientData.PURPLE);
                        }
                    } else {
                        clientData.show(BoundRelayClientData.BLACK);
                    }
                }
            } else {
                if (boundRelayBlockEntity.getBoundPos().isPresent()) {
                    world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                            RegistryEntry.of(SoundEvents.BLOCK_AMETHYST_BLOCK_STEP), SoundCategory.BLOCKS, 0.7F, 0.65F);
                }
            }
        }
        return ActionResult.success(world.isClient);
    }

    @Override
    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.get(POWER);
    }

    public static int getBoundRedstonePower(World world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof BoundRelayBlockEntity boundRelayBlockEntity) {
            Optional<GlobalPos> optionalBoundGlobalPos = boundRelayBlockEntity.getBoundPos();
            if (optionalBoundGlobalPos.isPresent()) {
                Optional<BlockState> optionalBoundBlockState = boundRelayBlockEntity.getBoundBlockState();

                if (optionalBoundBlockState.isPresent()) {
                    BlockState boundBlockState = optionalBoundBlockState.get();

                    if (boundBlockState.get(POWERED)) {
                        return 0;
                    }

                    OptionalInt power = Arrays.stream(Direction.values()).mapToInt(directionIteration -> {
                        BlockPos iterationPos = optionalBoundGlobalPos.get().pos().offset(directionIteration);
                        if (iterationPos.equals(pos)) {
                            return 0;
                        }
                        return world.getEmittedRedstonePower(iterationPos, directionIteration);
                    }).max();

                    if (power.isPresent()) {
                        return power.getAsInt();
                    }
                }
            }
        }
        return 0;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED, POWER);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BoundRelayBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
                                                                  BlockEntityType<T> type) {
        return validateTicker(type, ModBlockEntities.BOUND_RELAY, (world1, pos, blockState, blockEntity) -> {
            if (world1 instanceof ServerWorld serverWorld) {
                blockEntity.tick(serverWorld, pos, blockState, blockEntity);
            } else {
                blockEntity.getClientData().tick();
            }
        });
    }

    @Override
    protected MapCodec<? extends AbstractRelayBlock> getCodec() {
        return CODEC;
    }
}
