package dev.mariany.copperworks.api.interaction;

import com.google.gson.JsonObject;
import net.minecraft.block.Block;

public interface BatteryInteractionType {
    AbstractBatteryInteraction createInteraction(Block block, InteractionSound sound, JsonObject json);
}
