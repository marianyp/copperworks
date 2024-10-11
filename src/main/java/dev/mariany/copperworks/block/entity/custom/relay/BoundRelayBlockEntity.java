package dev.mariany.copperworks.block.entity.custom.relay;

import dev.mariany.copperworks.Copperworks;
import dev.mariany.copperworks.block.ModBlocks;
import dev.mariany.copperworks.block.custom.relay.bound.BoundRelayBlock;
import dev.mariany.copperworks.block.custom.relay.bound.BoundRelayClientData;
import dev.mariany.copperworks.block.entity.ModBlockEntities;
import dev.mariany.copperworks.util.ModUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BoundRelayBlockEntity extends AbstractBoundRelayBlockEntity {
    private static final String BOUND_NBT = "Bound";
    private Optional<GlobalPos> bound = Optional.empty();

    @Environment(EnvType.CLIENT)
    private final BoundRelayClientData clientData = new BoundRelayClientData();

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

        this.markDirty();
        if (this.world != null) {
            this.world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, this.pos);
        }
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
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createComponentlessNbt(registryLookup);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Environment(EnvType.CLIENT)
    public BoundRelayClientData getClientData() {
        return clientData;
    }

    @Environment(EnvType.CLIENT)
    public Optional<BoundRelayClientData> getBoundClientData() {
        World world = this.getWorld();

        if (world != null) {
            if (this.getBoundPos().isPresent()) {
                GlobalPos boundGlobalPos = this.getBoundPos().get();

                if (ModUtils.isSameDimension(world, boundGlobalPos)) {
                    BlockEntity blockEntity = world.getBlockEntity(boundGlobalPos.pos());
                    if (blockEntity instanceof BoundRelayBlockEntity boundRelayBlockEntity) {
                        return Optional.of(boundRelayBlockEntity.getClientData());
                    }
                }
            }
        }

        return Optional.empty();
    }

    @Override
    public void removeFromCopiedStackNbt(NbtCompound nbt) {
        super.removeFromCopiedStackNbt(nbt);
        nbt.remove(BOUND_NBT);
    }
}
