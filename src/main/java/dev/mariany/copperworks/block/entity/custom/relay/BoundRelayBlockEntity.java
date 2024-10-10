package dev.mariany.copperworks.block.entity.custom.relay;

import dev.mariany.copperworks.Copperworks;
import dev.mariany.copperworks.block.ModBlocks;
import dev.mariany.copperworks.block.custom.relay.BoundRelayBlock;
import dev.mariany.copperworks.block.entity.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BoundRelayBlockEntity extends BlockEntity {
    private static final String BOUND_NBT = "Bound";
    private Optional<GlobalPos> bound = Optional.empty();

    public BoundRelayBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BOUND_RELAY, pos, state);
    }

    public void tick(ServerWorld world, BlockPos pos, BlockState blockState,
                     BoundRelayBlockEntity boundRelayBlockEntity) {
        if (boundRelayBlockEntity.getBoundPos().isEmpty()) {
            return;
        }

        if (boundRelayBlockEntity.getBoundBlockState().isEmpty()) {
            world.setBlockState(pos, ModBlocks.COPPER_RELAY_CHARGED.getDefaultState());
        } else {
            int power = BoundRelayBlock.getBoundRedstonePower(world, pos);
            boolean powered = power > 0;

            boolean changed = powered != blockState.get(Properties.POWERED) || power != blockState.get(
                    Properties.POWER);

            if (changed) {
                world.setBlockState(pos, blockState.with(Properties.POWERED, powered).with(Properties.POWER, power),
                        Block.NOTIFY_LISTENERS);
                world.updateNeighborsAlways(pos, blockState.getBlock());
            }
        }
    }

    public Optional<BlockState> getBoundBlockState() {
        if (this.getBoundPos().isPresent()) {
            GlobalPos boundGlobalPos = this.getBoundPos().get();

            if (this.getWorld() instanceof ServerWorld serverWorld) {
                MinecraftServer server = serverWorld.getServer();
                ServerWorld boundWorld = server.getWorld(boundGlobalPos.dimension());

                if (boundWorld != null) {
                    BlockState thisBlockState = serverWorld.getBlockState(this.getPos());
                    BlockState boundBlockState = boundWorld.getBlockState(boundGlobalPos.pos());
                    if (boundBlockState.getBlock().equals(thisBlockState.getBlock())) {
                        return Optional.of(boundBlockState);
                    }
                }
            }
        }

        return Optional.empty();
    }

    public void bind(@NotNull GlobalPos pos) {
        this.bound = Optional.of(pos);
    }

    public Optional<GlobalPos> getBoundPos() {
        return this.bound;
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains(BOUND_NBT)) {
            this.bound = GlobalPos.CODEC.parse(NbtOps.INSTANCE, nbt.get(BOUND_NBT))
                    .resultOrPartial(Copperworks.LOGGER::error);
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        getBoundPos().flatMap(
                        pos -> GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, pos).resultOrPartial(Copperworks.LOGGER::error))
                .ifPresent(pos -> nbt.put(BOUND_NBT, pos));
    }

    @Override
    public void removeFromCopiedStackNbt(NbtCompound nbt) {
        super.removeFromCopiedStackNbt(nbt);
        nbt.remove(BOUND_NBT);
    }
}
