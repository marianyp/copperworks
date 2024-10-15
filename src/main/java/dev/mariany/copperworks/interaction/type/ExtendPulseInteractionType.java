package dev.mariany.copperworks.interaction.type;

import com.google.gson.JsonObject;
import dev.mariany.copperworks.api.interaction.AbstractBatteryInteraction;
import dev.mariany.copperworks.api.interaction.BatteryInteractionType;
import dev.mariany.copperworks.api.interaction.ExtendPulseInteraction;
import dev.mariany.copperworks.api.interaction.InteractionSound;
import net.minecraft.block.Block;
import org.jetbrains.annotations.Nullable;

public class ExtendPulseInteractionType implements BatteryInteractionType {
    @Override
    public @Nullable AbstractBatteryInteraction createInteraction(Block block, InteractionSound sound,
                                                                  JsonObject json) {
        boolean sameAxis = json.has("same_axis") && json.get("same_axis").getAsBoolean();
        return new ExtendPulseInteraction(this, sound, sameAxis);
    }
}
