package dev.mariany.copperworks.block.custom.stasis;

import com.mojang.serialization.MapCodec;
import dev.mariany.copperworks.block.ModBlocks;
import dev.mariany.copperworks.block.ModProperties;
import dev.mariany.copperworks.block.entity.custom.StasisChamberBlockEntity;
import dev.mariany.copperworks.world.chunk.ChunkLoaderBlock;
import dev.mariany.copperworks.util.ModConstants;
import dev.mariany.copperworks.util.ModUtils;
import dev.mariany.copperworks.world.chunk.ChunkLoadingManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlockStateComponent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StasisChamberCharged extends AbstractStasisChamber implements ChunkLoaderBlock {
    public static final MapCodec<StasisChamberCharged> CODEC = StasisChamber.createCodec(StasisChamberCharged::new);
    public static final IntProperty CHARGE = ModProperties.CHARGE;
    public static final BooleanProperty POWERED = Properties.POWERED;

    public StasisChamberCharged(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(CHARGE, ModConstants.MAX_STASIS_CHAMBER_CHARGE)
                .with(POWERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(POWERED, CHARGE);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
                         ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        if (world.getBlockEntity(pos) instanceof StasisChamberBlockEntity stasisChamberBlockEntity) {
            if (placer != null) {
                stasisChamberBlockEntity.setOwner(placer);
                if (world instanceof ServerWorld serverWorld) {
                    ChunkLoadingManager.startLoading(serverWorld, pos);
                }
            }
        }
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient && player.isCreative() && world.getGameRules()
                .getBoolean(GameRules.DO_TILE_DROPS) && world.getBlockEntity(
                pos) instanceof StasisChamberBlockEntity stasisChamberBlockEntity) {
            int charge = state.get(CHARGE);
            if (charge != ModConstants.MAX_STASIS_CHAMBER_CHARGE) {
                ItemStack itemStack = new ItemStack(this);
                itemStack.applyComponentsFrom(stasisChamberBlockEntity.createComponentMap());
                itemStack.set(DataComponentTypes.BLOCK_STATE, BlockStateComponent.DEFAULT.with(CHARGE, charge));
                ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
            }

            ChunkLoadingManager.stopLoading((ServerWorld) world, pos);
        }

        return super.onBreak(world, pos, state, player);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos,
                                  boolean notify) {
        boolean powered = ModUtils.isPowered(world, pos);
        if (powered != state.get(POWERED)) {
            world.setBlockState(pos, state.with(POWERED, powered), Block.NOTIFY_LISTENERS);

            if (powered && world.getBlockEntity(pos) instanceof StasisChamberBlockEntity stasisChamberBlockEntity) {
                if (world instanceof ServerWorld serverWorld) {
                    LivingEntity owner = stasisChamberBlockEntity.getOwner();
                    if (owner != null) {
                        owner.teleportTo(new TeleportTarget(serverWorld, pos.up().toBottomCenterPos(), Vec3d.ZERO,
                                owner.getYaw(), owner.getPitch(), TeleportTarget.NO_OP));
                        owner.onLanding();

                        if (owner instanceof ServerPlayerEntity serverPlayer) {
                            serverPlayer.clearCurrentExplosion();
                        }

                        tryDecrementingCharge(world, pos, state);

                        displayParticles(world, pos);
                        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ENTITY_PLAYER_TELEPORT,
                                SoundCategory.PLAYERS);
                    }
                }
            }
        }
    }

    private void tryDecrementingCharge(World world, BlockPos pos, BlockState state) {
        if (!world.isClient) {
            if (world.random.nextBoolean()) {
                int newCharge = MathHelper.clamp(state.get(CHARGE) - 1, 0, ModConstants.MAX_STASIS_CHAMBER_CHARGE);
                BlockState newState = newCharge > 0 ? state.with(CHARGE,
                        newCharge) : ModBlocks.COPPER_STASIS_CHAMBER.getDefaultState();
                world.setBlockState(pos, newState, Block.NOTIFY_ALL);
            }
        }
    }

    private void displayParticles(World world, BlockPos pos) {
        for (int i = 0; i < 32; i++) {
            world.addParticle(ParticleTypes.PORTAL, pos.getX(), pos.getY() + world.random.nextDouble() * 2.0,
                    pos.getZ(), world.random.nextGaussian(), 0.0, world.random.nextGaussian());
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        super.appendTooltip(stack, context, tooltip, options);
        ModUtils.appendBlockStateChargeTooltip(stack, tooltip, ModConstants.MAX_STASIS_CHAMBER_CHARGE);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new StasisChamberBlockEntity(pos, state);
    }
}
