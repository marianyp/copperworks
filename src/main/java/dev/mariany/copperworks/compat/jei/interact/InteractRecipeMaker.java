package dev.mariany.copperworks.compat.jei.interact;

import dev.mariany.copperworks.block.ModBlocks;
import dev.mariany.copperworks.item.ModItems;
import dev.mariany.copperworks.item.component.CopperworksComponents;
import dev.mariany.copperworks.util.ModConstants;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Oxidizable;
import net.minecraft.item.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InteractRecipeMaker {
    private final List<InteractRecipe> recipes = new ArrayList<>();

    public List<InteractRecipe> getInteractRecipes(IIngredientManager ingredientManager) {
        register("wooden_rail_upgrade", ModItems.COPPER_UPGRADE_SMITHING_TEMPLATE, ModBlocks.WOODEN_RAIL,
                ModBlocks.COPPER_RAIL);

        register("bind_radio", ModItems.RADIO, ModBlocks.COPPER_RELAY_CHARGED, ModBlocks.COPPER_RELAY_RADIO_BOUND);

        register("initiate_relay_binding", Items.AMETHYST_SHARD, ModBlocks.COPPER_RELAY_CHARGED,
                ModItems.AMETHYST_PIECE);
        register("finish_relay_binding", ModItems.AMETHYST_PIECE, ModBlocks.COPPER_RELAY_CHARGED,
                ModBlocks.COPPER_RELAY_BOUND);

        registerCollectDragonsBreath();

        registerPatina(ingredientManager);

        return recipes;
    }

    private void registerPatina(IIngredientManager ingredientManager) {
        List<ItemStack> axes = ingredientManager.getAllItemStacks().stream()
                .filter(stack -> stack.getItem() instanceof AxeItem).toList();

        List<Block> blocks = ingredientManager.getAllItemStacks().stream().filter(stack -> {
            if (stack.getItem() instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();
                if (block instanceof Oxidizable) {
                    Optional<BlockState> optionalDecreasedOxidationState = Oxidizable.getDecreasedOxidationState(
                            block.getDefaultState());
                    return optionalDecreasedOxidationState.isPresent();
                }
            }

            return false;
        }).map(stack -> ((BlockItem) stack.getItem()).getBlock()).toList();

        register("harvest_patina", axes, blocks, ModBlocks.PATINA.asItem().getDefaultStack(),
                ModConstants.HARVEST_PATINA_CHANCE);
    }

    private ItemStack getLeveledDragonBreath(int level) {
        ItemStack stack = ModItems.PARTIAL_DRAGON_BREATH.getDefaultStack();
        stack.set(CopperworksComponents.DRAGON_BREATH_FILL, level);
        return stack;
    }

    private void registerCollectDragonsBreath() {
        register("collect_some_dragon_breath", Items.GLASS_BOTTLE.getDefaultStack(), Blocks.CRYING_OBSIDIAN,
                getLeveledDragonBreath(0), 1);
        register("collect_some_dragon_breath_1", getLeveledDragonBreath(0), Blocks.CRYING_OBSIDIAN,
                getLeveledDragonBreath(1), ModConstants.COLLECT_DRAGON_BREATH_CHANCE);
        register("collect_some_dragon_breath_2", getLeveledDragonBreath(1), Blocks.CRYING_OBSIDIAN,
                getLeveledDragonBreath(2), ModConstants.COLLECT_DRAGON_BREATH_CHANCE);
        register("collect_dragon_breath", getLeveledDragonBreath(2), Blocks.CRYING_OBSIDIAN,
                Items.DRAGON_BREATH.getDefaultStack(), ModConstants.COLLECT_DRAGON_BREATH_CHANCE);
    }

    private void register(String name, ItemConvertible item, Block block, ItemConvertible output) {
        register(name, List.of(item.asItem().getDefaultStack()), List.of(block), output.asItem().getDefaultStack(), 1);
    }

    private void register(String name, ItemStack item, Block block, ItemStack output, float chance) {
        register(name, List.of(item), List.of(block), output, chance);
    }

    private void register(String name, List<ItemStack> validItems, List<Block> validBlocks, ItemStack output,
                          float chance) {
        recipes.add(new InteractRecipe(name, validItems, validBlocks, output, chance));
    }
}
