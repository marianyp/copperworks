package dev.mariany.copperworks.interaction.type;

import com.google.gson.JsonObject;
import dev.mariany.copperworks.api.interaction.AbstractBatteryInteraction;
import dev.mariany.copperworks.api.interaction.BatteryInteractionType;
import dev.mariany.copperworks.api.interaction.InteractionSound;
import dev.mariany.copperworks.block.entity.custom.ClockBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ResetClockInteractionType implements BatteryInteractionType {
    @Nullable
    @Override
    public AbstractBatteryInteraction createInteraction(Block block, InteractionSound sound, JsonObject json) {
        return new AbstractBatteryInteraction(this, sound) {
            @Override
            public void executeInteraction(World world, BlockPos pos) {
                if (world.getBlockEntity(pos) instanceof ClockBlockEntity clockBlockEntity) {
                    clockBlockEntity.resetProgress();
                }
            }
        };
    }
}
