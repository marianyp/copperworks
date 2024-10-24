package dev.mariany.copperworks.compat.jei;

import dev.mariany.copperworks.Copperworks;
import dev.mariany.copperworks.api.registry.BatteryInteractionRegistry;
import dev.mariany.copperworks.block.ModBlocks;
import dev.mariany.copperworks.compat.jei.charging.ChargingRecipeCategory;
import dev.mariany.copperworks.compat.jei.charging.ChargingRecipeMaker;
import dev.mariany.copperworks.compat.jei.interact.InteractRecipeCategory;
import dev.mariany.copperworks.compat.jei.interact.InteractRecipeMaker;
import dev.mariany.copperworks.compat.jei.interact.battery.BatteryInteractionRecipeCategory;
import dev.mariany.copperworks.compat.jei.interact.battery.BatteryInteractionRecipeMaker;
import dev.mariany.copperworks.interaction.type.ExtendPulseInteractionType;
import dev.mariany.copperworks.item.ModItems;
import dev.mariany.copperworks.tag.CopperworksTags;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@JeiPlugin
public class JEICopperworksPlugin implements IModPlugin {
    @NotNull
    @Override
    public Identifier getPluginUid() {
        return Copperworks.id("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
        registration.addRecipeCategories(new ChargingRecipeCategory(guiHelper));
        registration.addRecipeCategories(new InteractRecipeCategory(guiHelper));
        registration.addRecipeCategories(new BatteryInteractionRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        IIngredientManager ingredientManager = registration.getIngredientManager();

        registration.addRecipes(ModRecipeTypes.CHARGING, ChargingRecipeMaker.getChargingRecipes(ingredientManager));
        registration.addRecipes(ModRecipeTypes.INTERACT,
                new InteractRecipeMaker().getInteractRecipes(ingredientManager));
        registration.addRecipes(ModRecipeTypes.BATTERY_INTERACTION, BatteryInteractionRecipeMaker.getInteractions());

        registerDescriptions(registration);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalysts(ModRecipeTypes.CHARGING, ModBlocks.COPPER_BATTERY);
        registration.addRecipeCatalyst(ModBlocks.COPPER_BATTERY, ModRecipeTypes.BATTERY_INTERACTION);
        registerExtendPulseCatalyst(registration);
    }

    private void registerExtendPulseCatalyst(IRecipeCatalystRegistration registration) {
        BatteryInteractionRegistry.getAllInteractions().entrySet().stream()
                .filter(interactionEntry -> (interactionEntry.getValue()
                        .getInteractionType() instanceof ExtendPulseInteractionType))
                .map(interactionEntry -> Registries.BLOCK.get(interactionEntry.getKey())).filter(Objects::nonNull)
                .forEach(block -> {
                    registration.addRecipeCatalyst(block, ModRecipeTypes.BATTERY_INTERACTION);
                });
    }

    private void registerDescriptions(IRecipeRegistration registration) {
        registration.addIngredientInfo(ModBlocks.COMPARATOR_MIRROR,
                Text.translatable("gui.jei.info.block.copperworks.comparator_mirror"));
        registration.addIngredientInfo(ModBlocks.COPPER_BATTERY,
                Text.translatable("gui.jei.info.block.copperworks.battery"));
        registration.addIngredientInfo(ModBlocks.COPPER_CLOCK,
                Text.translatable("gui.jei.info.block.copperworks.copper_clock"));
        registration.addIngredientInfo(ModBlocks.COPPER_FRAME,
                Text.translatable("gui.jei.info.block.copperworks.copper_frame"));
        registration.addIngredientInfo(ModBlocks.COPPER_LEVER,
                Text.translatable("gui.jei.info.block.copperworks.copper_lever"));
        registration.addIngredientInfo(ModBlocks.COPPER_RAIL,
                Text.translatable("gui.jei.info.block.copperworks.copper_rail"));
        registration.addIngredientInfo(ModBlocks.COPPER_RELAY,
                Text.translatable("gui.jei.info.block.copperworks.relay"));
        registration.addIngredientInfo(ModBlocks.COPPER_RELAY_BOUND,
                Text.translatable("gui.jei.info.block.copperworks.relay_bound"));
        registration.addIngredientInfo(ModBlocks.COPPER_RELAY_RADIO_BOUND,
                Text.translatable("gui.jei.info.block.copperworks.relay_radio_bound"));
        registration.addIngredientInfo(ModBlocks.COPPER_RELAY_CHARGED,
                Text.translatable("gui.jei.info.block.copperworks.relay"));
        registration.addIngredientInfo(ModBlocks.COPPER_SENSOR,
                Text.translatable("gui.jei.info.block.copperworks.sensor"));
        registration.addIngredientInfo(ModBlocks.COPPER_SENSOR_CHARGED,
                Text.translatable("gui.jei.info.block.copperworks.sensor"));
        registration.addIngredientInfo(ModBlocks.COPPER_STASIS_CHAMBER,
                Text.translatable("gui.jei.info.block.copperworks.stasis_chamber"));
        registration.addIngredientInfo(ModBlocks.COPPER_STASIS_CHAMBER_CHARGED,
                Text.translatable("gui.jei.info.block.copperworks.stasis_chamber"));
        registration.addIngredientInfo(ModBlocks.ENHANCED_SCULK_SENSOR,
                Text.translatable("gui.jei.info.block.copperworks.enhanced_sculk_sensor"));
        registration.addIngredientInfo(ModBlocks.MUFFLER, Text.translatable("gui.jei.info.block.copperworks.muffler"));
        registration.addIngredientInfo(ModBlocks.PATINA, Text.translatable("gui.jei.info.block.copperworks.patina"));
        registration.addIngredientInfo(ModBlocks.STICKY_COPPER,
                Text.translatable("gui.jei.info.block.copperworks.sticky_copper"));
        registration.addIngredientInfo(ModBlocks.STICKY_COPPER_HONEY,
                Text.translatable("gui.jei.info.block.copperworks.sticky_copper"));
        registration.addIngredientInfo(ModBlocks.WOODEN_RAIL,
                Text.translatable("gui.jei.info.block.copperworks.wooden_rail"));
        registration.addIngredientInfo(ModItems.COPPER_UPGRADE_SMITHING_TEMPLATE,
                Text.translatable("gui.jei.info.item.copperworks.copper_upgrade_smithing_template"));
        registration.addIngredientInfo(ModItems.COPPER_DRILL, Text.translatable("gui.jei.info.item.copperworks.drill"));
        registration.addIngredientInfo(ModItems.RADIO, Text.translatable("gui.jei.info.item.copperworks.radio"));
        registration.addIngredientInfo(ModItems.ROCKET_BOOTS,
                Text.translatable("gui.jei.info.item.copperworks.rocket_boots"));
        registration.addIngredientInfo(ModItems.WRENCH, Text.translatable("gui.jei.info.item.copperworks.wrench"));

        registration.getIngredientManager().getAllItemStacks().stream()
                .filter(stack -> stack.isIn(CopperworksTags.Items.ENGINEER_CAN_UPGRADE)).map(ItemStack::getItem)
                .distinct().forEach(item -> registration.addIngredientInfo(item,
                        Text.translatable("gui.jei.info.misc.copperworks.engineer_can_upgrade")));
    }
}
