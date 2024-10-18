package dev.mariany.copperworks.interaction.type;

import com.google.gson.JsonObject;
import dev.mariany.copperworks.Copperworks;
import dev.mariany.copperworks.api.interaction.AbstractBatteryInteraction;
import dev.mariany.copperworks.api.interaction.BatteryInteractionType;
import dev.mariany.copperworks.api.interaction.InteractionSound;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ConvertBlockInteractionType implements BatteryInteractionType {
    @Nullable
    @Override
    public AbstractBatteryInteraction createInteraction(Block block, InteractionSound sound, JsonObject json) {
        String convertToString = json.get("convert_to").getAsString();
        Identifier convertToIdentifier = Identifier.tryParse(convertToString);

        if (convertToIdentifier == null) {
            Copperworks.LOGGER.info("Block id {} is invalid", convertToString);
            return null;
        }

        Optional<Block> convertToBlockOptional = Registries.BLOCK.getOrEmpty(convertToIdentifier);

        if (convertToBlockOptional.isEmpty()) {
            Copperworks.LOGGER.info("Block with id {} could not be found for block conversion", convertToIdentifier);
            return null;
        }

        Block convertToBlock = convertToBlockOptional.get();

        return new AbstractBatteryInteraction(this, sound) {
            @Override
            public void executeInteraction(World world, BlockPos pos) {
                world.setBlockState(pos, convertToBlock.getDefaultState());
                world.updateNeighborsAlways(pos, convertToBlock);
            }
        };
    }
}