package dev.mariany.copperworks.block.entity.custom;

import dev.mariany.copperworks.block.entity.ModBlockEntities;
import dev.mariany.copperworks.screen.EnhancedSculkSensorScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.SculkSensorBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EnhancedSculkSensorBlockEntity extends SculkSensorBlockEntity implements NamedScreenHandlerFactory {
    private static final String FREQUENCY_WHITELIST_NBT = "FrequencyWhitelist";
    private static final String RANGE_NBT = "Range";

    private final PropertyDelegate propertyDelegate;

    private Set<Integer> frequencyWhitelist = new HashSet<>();
    private int range = 16;

    public EnhancedSculkSensorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.ENHANCED_SCULK_SENSOR, blockPos, blockState);

        this.propertyDelegate = new PropertyDelegate() {
            private int containsFrequency(int frequency) {
                return frequencyWhitelist.contains(frequency) ? 1 : 0;
            }

            private void toggleFrequency(int frequency) {
                if (frequencyWhitelist.contains(frequency)) {
                    frequencyWhitelist.remove(frequency);
                } else {
                    frequencyWhitelist.add(frequency);
                }
            }

            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> EnhancedSculkSensorBlockEntity.this.range;
                    case 1 -> containsFrequency(1);
                    case 2 -> containsFrequency(2);
                    case 3 -> containsFrequency(3);
                    case 4 -> containsFrequency(4);
                    case 5 -> containsFrequency(5);
                    case 6 -> containsFrequency(6);
                    case 7 -> containsFrequency(7);
                    case 8 -> containsFrequency(8);
                    case 9 -> containsFrequency(9);
                    case 10 -> containsFrequency(10);
                    case 11 -> containsFrequency(11);
                    case 12 -> containsFrequency(12);
                    case 13 -> containsFrequency(13);
                    case 14 -> containsFrequency(14);
                    case 15 -> containsFrequency(15);
                    default -> throw new IllegalStateException("Unexpected value: " + index);
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> EnhancedSculkSensorBlockEntity.this.range = value;
                    case 1 -> toggleFrequency(1);
                    case 2 -> toggleFrequency(2);
                    case 3 -> toggleFrequency(3);
                    case 4 -> toggleFrequency(4);
                    case 5 -> toggleFrequency(5);
                    case 6 -> toggleFrequency(6);
                    case 7 -> toggleFrequency(7);
                    case 8 -> toggleFrequency(8);
                    case 9 -> toggleFrequency(9);
                    case 10 -> toggleFrequency(10);
                    case 11 -> toggleFrequency(11);
                    case 12 -> toggleFrequency(12);
                    case 13 -> toggleFrequency(13);
                    case 14 -> toggleFrequency(14);
                    case 15 -> toggleFrequency(15);
                }
            }

            @Override
            public int size() {
                return 16;
            }
        };
    }

    public void setFrequencyWhitelist(List<Integer> newFrequencyWhitelist) {
        this.frequencyWhitelist = new HashSet<>(newFrequencyWhitelist);
    }

    public void setRange(int value) {
        this.range = value;
    }

    public Set<Integer> getFrequencyWhitelist() {
        return this.frequencyWhitelist;
    }

    public int getRange() {
        return this.range;
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.range = nbt.getInt(RANGE_NBT);

        if (nbt.contains(FREQUENCY_WHITELIST_NBT, NbtElement.INT_ARRAY_TYPE)) {
            this.frequencyWhitelist = Arrays.stream(nbt.getIntArray(FREQUENCY_WHITELIST_NBT)).boxed()
                    .collect(Collectors.toSet());
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt(RANGE_NBT, this.range);
        nbt.putIntArray(FREQUENCY_WHITELIST_NBT, this.frequencyWhitelist.stream().toList());
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
            Set<Integer> frequencyWhitelist = EnhancedSculkSensorBlockEntity.this.getFrequencyWhitelist();
            if (frequencyWhitelist.isEmpty()) {
                return false;
            }
            int frequency = Vibrations.getFrequency(event);
            return frequencyWhitelist.contains(frequency) && super.accepts(world, pos, event, emitter);
        }
    }
}
