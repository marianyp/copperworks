package dev.mariany.copperworks.compat.jei.interact;

import dev.mariany.copperworks.Copperworks;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;

public record InteractRecipe(String name, List<ItemStack> validItems, List<Block> validBlocks, ItemStack output,
                             float chance) {
    public Identifier getUid() {
        return Copperworks.id("interact." + name);
    }
}
