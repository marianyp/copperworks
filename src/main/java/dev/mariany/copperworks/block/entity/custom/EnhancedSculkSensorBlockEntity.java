package dev.mariany.copperworks.block.entity.custom;

import dev.mariany.copperworks.block.entity.ModBlockEntities;
import dev.mariany.copperworks.screen.EnhancedSculkSensorScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SculkSensorBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.Vibrations;
import org.jetbrains.annotations.Nullable;

public class EnhancedSculkSensorBlockEntity extends SculkSensorBlockEntity implements NamedScreenHandlerFactory {
    private static final String FREQUENCY_NBT = "Frequency";
    private static final String RANGE_NBT = "Range";

    private final PropertyDelegate propertyDelegate;

    private int frequency = -1;
    private int range = 16;

    public EnhancedSculkSensorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.ENHANCED_SCULK_SENSOR, blockPos, blockState);

        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> EnhancedSculkSensorBlockEntity.this.frequency;
                    case 1 -> EnhancedSculkSensorBlockEntity.this.range;
                    default -> throw new IllegalStateException("Unexpected value: " + index);
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> EnhancedSculkSensorBlockEntity.this.frequency = value;
                    case 1 -> EnhancedSculkSensorBlockEntity.this.range = value;
                }
            }

            @Override
            public int size() {
                return 2;
            }
        };
    }

    private int getFrequency() {
        return frequency;
    }

    private int getRange() {
        return range;
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.frequency = nbt.getInt(FREQUENCY_NBT);
        this.range = nbt.getInt(RANGE_NBT);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt(FREQUENCY_NBT, this.frequency);
        nbt.putInt(RANGE_NBT, this.range);
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return this.createComponentlessNbt(registryLookup);
    }

    @Override
    public Vibrations.Callback createCallback() {
        return new Callback(this.getPos());
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.copperworks.enhanced_sculk_sensor");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new EnhancedSculkSensorScreenHandler(syncId, playerInventory, this.propertyDelegate);
    }

    protected class Callback extends VibrationCallback {
        public Callback(final BlockPos pos) {
            super(pos);
        }

        @Override
        public int getRange() {
            return EnhancedSculkSensorBlockEntity.this.getRange();
        }

        @Override
        public boolean accepts(ServerWorld world, BlockPos pos, RegistryEntry<GameEvent> event,
                               @Nullable GameEvent.Emitter emitter) {
            int calibratedFrequency = EnhancedSculkSensorBlockEntity.this.getFrequency();

            if (calibratedFrequency == -1) {
                return false;
            }

            return (calibratedFrequency == 0 || Vibrations.getFrequency(event) == calibratedFrequency) && super.accepts(
                    world, pos, event, emitter);
        }
    }
}
