package dev.mariany.copperworks.item.custom;

import dev.mariany.copperworks.item.component.CopperworksComponents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractBindingItem extends Item {
    private static final Map<RegistryKey<World>, String> DIMENSIONS = new HashMap<>();

    static {
        DIMENSIONS.put(World.OVERWORLD, "item.copperworks.radio.dimension.overworld.tooltip");
        DIMENSIONS.put(World.NETHER, "item.copperworks.radio.dimension.nether.tooltip");
        DIMENSIONS.put(World.END, "item.copperworks.radio.dimension.end.tooltip");
    }

    public AbstractBindingItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        GlobalPos globalPos = stack.get(CopperworksComponents.RELAY_POSITION);
        if (globalPos != null) {
            Text positionText = Text.translatable("item.copperworks.radio.position.tooltip",
                    Text.of(globalPos.pos().toShortString())).copy().withColor(Colors.GRAY);
            RegistryKey<World> dimension = globalPos.dimension();
            Text dimensionText;

            if (DIMENSIONS.containsKey(dimension)) {
                dimensionText = Text.translatable("item.copperworks.radio.dimension.tooltip",
                        Text.translatable(DIMENSIONS.get(dimension)).withColor(Colors.GRAY));
            } else {
                dimensionText = Text.translatable("item.copperworks.radio.dimension.tooltip",
                        Text.of(dimension.getValue()).copy().withColor(Colors.GRAY));
            }


            tooltip.add(dimensionText);
            tooltip.add(positionText);
        }
    }
}
