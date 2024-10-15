package dev.mariany.copperworks.block.entity.custom;

import dev.mariany.copperworks.block.entity.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class StasisChamberBlockEntity extends BlockEntity {
    private static final String OWNER_NBT = "Owner";

    @Nullable
    private UUID owner;

    public StasisChamberBlockEntity(BlockPos pos, BlockState state) {
        this(ModBlockEntities.STASIS_CHAMBER, pos, state);
    }

    public StasisChamberBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }


    public LivingEntity getOwner() {
        if (this.world != null) {
            MinecraftServer server = this.world.getServer();
            if (server != null) {
                return server.getPlayerManager().getPlayer(this.owner);
            }
        }

        return null;
    }

    public void setOwner(LivingEntity player) {
        this.owner = player.getUuid();
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        if (nbt.contains(OWNER_NBT)) {
            this.owner = nbt.getUuid(OWNER_NBT);
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putUuid(OWNER_NBT, this.owner);
    }

    @Override
    public void removeFromCopiedStackNbt(NbtCompound nbt) {
        super.removeFromCopiedStackNbt(nbt);
        nbt.remove(OWNER_NBT);
    }
}
