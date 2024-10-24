package dev.mariany.copperworks.compat.jei.interact.battery;

import dev.mariany.copperworks.Copperworks;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record BatteryInteractionRecipe(String name, Block block, ItemStack convertsTo, @Nullable Text details) {
    public Identifier getUid() {
        return Copperworks.id("battery_interaction." + block.asItem().getName().getLiteralString());
    }
}
