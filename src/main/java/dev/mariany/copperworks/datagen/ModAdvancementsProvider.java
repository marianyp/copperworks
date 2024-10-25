package dev.mariany.copperworks.datagen;

import dev.mariany.copperworks.Copperworks;
import dev.mariany.copperworks.advancement.criterion.ModCriteria;
import dev.mariany.copperworks.block.ModBlocks;
import dev.mariany.copperworks.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.*;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAdvancementsProvider extends FabricAdvancementProvider {
    public ModAdvancementsProvider(FabricDataOutput output,
                                   CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup registryLookup, Consumer<AdvancementEntry> consumer) {
        AdvancementEntry rootAdvancement = createRootAdvancement(consumer);
        AdvancementEntry firstTracksAdvancement = createFirstTracksAdvancement(rootAdvancement, consumer);
        AdvancementEntry braceYourselfAdvancement = createBraceYourSelfAdvancement(rootAdvancement, consumer);
        AdvancementEntry electrifyingBeginningsAdvancement = createElectrifyingBeginnings(rootAdvancement, consumer);
        AdvancementEntry surprisinglyLightAdvancement = createSurprisinglyLightAdvancement(braceYourselfAdvancement,
                consumer);
        AdvancementEntry multipurposeAdvancement = createMultipurposeAdvancement(surprisinglyLightAdvancement,
                consumer);
        AdvancementEntry skyboundAdvancement = createSkyboundAdvancement(multipurposeAdvancement, consumer);
        AdvancementEntry wirelessPossibilitiesAdvancement = createWirelessPossibilitiesAdvancement(
                electrifyingBeginningsAdvancement, consumer);

        createStickyFingersAdvancement(rootAdvancement, consumer);
        createTickTockAdvancement(rootAdvancement, consumer);
        createPatinaAdvancement(rootAdvancement, consumer);
        createDragonsEssenceAdvancement(rootAdvancement, consumer);
        createWrenchItAdvancement(rootAdvancement, consumer);
        createQuantumLeapAdvancement(rootAdvancement, consumer);

        createBuiltToLastAdvancement(firstTracksAdvancement, consumer);

        createANewSparkAdvancement(braceYourselfAdvancement, consumer);

        createNullifiedAdvancement(electrifyingBeginningsAdvancement, consumer);
        createMyLittleEyeAdvancement(electrifyingBeginningsAdvancement, consumer);

        createTheSkyIsTheLimitAdvancement(skyboundAdvancement, consumer);

        createHighFrequencyAdvancement(wirelessPossibilitiesAdvancement, consumer);
        createNoStringsAttachedAdvancement(wirelessPossibilitiesAdvancement, consumer);
    }

    private AdvancementEntry createRootAdvancement(Consumer<AdvancementEntry> consumer) {
        String id = "root";
        return Advancement.Builder.create().display(Items.COPPER_INGOT, Text.translatable(titleTranslationKey(id)),
                        Text.translatable(descriptionTranslationKey(id)), Identifier.ofVanilla("textures/block/deepslate.png"),
                        AdvancementFrame.TASK, true, false, false)
                .criterion(hasItem(Items.COPPER_INGOT), InventoryChangedCriterion.Conditions.items(Items.COPPER_INGOT))
                .build(consumer, advancementId(id));
    }

    private void createStickyFingersAdvancement(AdvancementEntry parent, Consumer<AdvancementEntry> consumer) {
        String id = "sticky_fingers";
        Advancement.Builder.create().parent(parent)
                .display(ModBlocks.STICKY_COPPER, Text.translatable(titleTranslationKey(id)),
                        Text.translatable(descriptionTranslationKey(id)), null, AdvancementFrame.TASK, true, true,
                        false).criteriaMerger(AdvancementRequirements.CriterionMerger.OR)
                .criterion(hasItem(ModBlocks.STICKY_COPPER),
                        InventoryChangedCriterion.Conditions.items(ModBlocks.STICKY_COPPER))
                .criterion(hasItem(ModBlocks.STICKY_COPPER_HONEY),
                        InventoryChangedCriterion.Conditions.items(ModBlocks.STICKY_COPPER_HONEY))
                .build(consumer, advancementId(id));
    }

    private void createTickTockAdvancement(AdvancementEntry parent, Consumer<AdvancementEntry> consumer) {
        String id = "tick_tock";
        Advancement.Builder.create().parent(parent)
                .display(ModBlocks.COPPER_CLOCK, Text.translatable(titleTranslationKey(id)),
                        Text.translatable(descriptionTranslationKey(id)), null, AdvancementFrame.TASK, true, true,
                        false).criterion(hasItem(ModBlocks.COPPER_CLOCK),
                        InventoryChangedCriterion.Conditions.items(ModBlocks.COPPER_CLOCK)).build(consumer, advancementId(id));
    }

    private void createPatinaAdvancement(AdvancementEntry parent, Consumer<AdvancementEntry> consumer) {
        String id = "patina";
        Advancement.Builder.create().parent(parent)
                .display(ModBlocks.PATINA, Text.translatable(titleTranslationKey(id)),
                        Text.translatable(descriptionTranslationKey(id)), null, AdvancementFrame.TASK, true, true,
                        false).criterion("scraped_patina", createCriterion(ModCriteria.SCRAPED_PATINA))
                .build(consumer, advancementId(id));
    }

    private void createDragonsEssenceAdvancement(AdvancementEntry parent, Consumer<AdvancementEntry> consumer) {
        String id = "dragons_essence";
        Advancement.Builder.create().parent(parent)
                .display(Items.DRAGON_BREATH, Text.translatable(titleTranslationKey(id)),
                        Text.translatable(descriptionTranslationKey(id)), null, AdvancementFrame.TASK, true, true,
                        false)
                .criterion("collect_partial_dragon_breath", createCriterion(ModCriteria.COLLECT_PARTIAL_DRAGON_BREATH))
                .build(consumer, advancementId(id));
    }

    private void createWrenchItAdvancement(AdvancementEntry parent, Consumer<AdvancementEntry> consumer) {
        String id = "wrench_it";
        Advancement.Builder.create().parent(parent).display(ModItems.WRENCH, Text.translatable(titleTranslationKey(id)),
                        Text.translatable(descriptionTranslationKey(id)), null, AdvancementFrame.TASK, true, true, false)
                .criterion("use_wrench", createCriterion(ModCriteria.USE_WRENCH)).build(consumer, advancementId(id));
    }

    private void createQuantumLeapAdvancement(AdvancementEntry parent, Consumer<AdvancementEntry> consumer) {
        String id = "quantum_leap";
        Advancement.Builder.create().parent(parent)
                .display(ModBlocks.COPPER_STASIS_CHAMBER_CHARGED, Text.translatable(titleTranslationKey(id)),
                        Text.translatable(descriptionTranslationKey(id)), null, AdvancementFrame.TASK, true, true,
                        false).criterion("use_stasis_chamber", createCriterion(ModCriteria.USE_STASIS_CHAMBER))
                .build(consumer, advancementId(id));
    }

    private AdvancementEntry createFirstTracksAdvancement(AdvancementEntry parent,
                                                          Consumer<AdvancementEntry> consumer) {
        String id = "first_tracks";
        return Advancement.Builder.create().parent(parent)
                .display(ModBlocks.WOODEN_RAIL, Text.translatable(titleTranslationKey(id)),
                        Text.translatable(descriptionTranslationKey(id)), null, AdvancementFrame.TASK, true, true,
                        false).criterion(hasItem(ModBlocks.WOODEN_RAIL),
                        InventoryChangedCriterion.Conditions.items(ModBlocks.WOODEN_RAIL))
                .build(consumer, advancementId(id));
    }

    private void createBuiltToLastAdvancement(AdvancementEntry parent, Consumer<AdvancementEntry> consumer) {
        String id = "built_to_last";
        Advancement.Builder.create().parent(parent)
                .display(ModBlocks.COPPER_RAIL, Text.translatable(titleTranslationKey(id)),
                        Text.translatable(descriptionTranslationKey(id)), null, AdvancementFrame.TASK, true, true,
                        false).criterion("upgraded_wooden_rail", createCriterion(ModCriteria.UPGRADE_WOODEN_RAIL))
                .build(consumer, advancementId(id));
    }

    private AdvancementEntry createBraceYourSelfAdvancement(AdvancementEntry parent,
                                                            Consumer<AdvancementEntry> consumer) {
        String id = "brace_yourself";
        return Advancement.Builder.create().parent(parent)
                .display(ModItems.COPPER_BRACER, Text.translatable(titleTranslationKey(id)),
                        Text.translatable(descriptionTranslationKey(id)), null, AdvancementFrame.TASK, true, true,
                        false).criterion(hasItem(ModItems.COPPER_BRACER),
                        InventoryChangedCriterion.Conditions.items(ModItems.COPPER_BRACER))
                .build(consumer, advancementId(id));
    }

    private void createANewSparkAdvancement(AdvancementEntry parent, Consumer<AdvancementEntry> consumer) {
        String id = "a_new_spark";
        Advancement.Builder.create().parent(parent)
                .display(Blocks.REDSTONE_BLOCK, Text.translatable(titleTranslationKey(id)),
                        Text.translatable(descriptionTranslationKey(id)), null, AdvancementFrame.TASK, true, true,
                        false).criterion("upgrade_item", createCriterion(ModCriteria.UPGRADED_ITEM))
                .build(consumer, advancementId(id));
    }

    private AdvancementEntry createSurprisinglyLightAdvancement(AdvancementEntry parent,
                                                                Consumer<AdvancementEntry> consumer) {
        String id = "surprisingly_light";
        return Advancement.Builder.create().parent(parent)
                .display(ModItems.COPPER_BROADSWORD, Text.translatable(titleTranslationKey(id)),
                        Text.translatable(descriptionTranslationKey(id)), null, AdvancementFrame.TASK, true, true,
                        false).criterion(hasItem(ModItems.COPPER_BROADSWORD),
                        InventoryChangedCriterion.Conditions.items(ModItems.COPPER_BROADSWORD))
                .build(consumer, advancementId(id));
    }

    private AdvancementEntry createMultipurposeAdvancement(AdvancementEntry parent,
                                                           Consumer<AdvancementEntry> consumer) {
        String id = "multipurpose";
        return Advancement.Builder.create().parent(parent)
                .display(ModItems.COPPER_DRILL, Text.translatable(titleTranslationKey(id)),
                        Text.translatable(descriptionTranslationKey(id)), null, AdvancementFrame.TASK, true, true,
                        false).criterion(hasItem(ModItems.COPPER_DRILL),
                        InventoryChangedCriterion.Conditions.items(ModItems.COPPER_DRILL))
                .build(consumer, advancementId(id));
    }

    private AdvancementEntry createSkyboundAdvancement(AdvancementEntry parent, Consumer<AdvancementEntry> consumer) {
        String id = "skybound";
        return Advancement.Builder.create().parent(parent)
                .display(ModItems.ROCKET_BOOTS, Text.translatable(titleTranslationKey(id)),
                        Text.translatable(descriptionTranslationKey(id)), null, AdvancementFrame.TASK, true, true,
                        false).criterion("use_rocket_boots", createCriterion(ModCriteria.USE_ROCKET_BOOTS))
                .build(consumer, advancementId(id));
    }

    private void createTheSkyIsTheLimitAdvancement(AdvancementEntry parent, Consumer<AdvancementEntry> consumer) {
        String id = "the_sky_is_the_limit";
        Advancement.Builder.create().parent(parent).display(Items.FEATHER, Text.translatable(titleTranslationKey(id)),
                        Text.translatable(descriptionTranslationKey(id)), null, AdvancementFrame.CHALLENGE, true, true, false)
                .criterion("reach_out_of_bounds", createCriterion(ModCriteria.REACH_OUT_OF_BOUNDS))
                .build(consumer, advancementId(id));
    }

    private AdvancementEntry createElectrifyingBeginnings(AdvancementEntry parent,
                                                          Consumer<AdvancementEntry> consumer) {
        String id = "electrifying_beginnings";
        return Advancement.Builder.create().parent(parent)
                .display(ModBlocks.COPPER_BATTERY, Text.translatable(titleTranslationKey(id)),
                        Text.translatable(descriptionTranslationKey(id)), null, AdvancementFrame.TASK, true, true,
                        false).criterion(hasItem(ModBlocks.COPPER_BATTERY),
                        InventoryChangedCriterion.Conditions.items(ModBlocks.COPPER_BATTERY))
                .build(consumer, advancementId(id));
    }

    private void createNullifiedAdvancement(AdvancementEntry parent, Consumer<AdvancementEntry> consumer) {
        String id = "nullified";
        Advancement.Builder.create().parent(parent)
                .display(ModBlocks.DEACTIVATED_REDSTONE_BLOCK, Text.translatable(titleTranslationKey(id)),
                        Text.translatable(descriptionTranslationKey(id)), null, AdvancementFrame.TASK, true, true,
                        false).criterion(hasItem(ModBlocks.DEACTIVATED_REDSTONE_BLOCK),
                        InventoryChangedCriterion.Conditions.items(ModBlocks.DEACTIVATED_REDSTONE_BLOCK))
                .build(consumer, advancementId(id));
    }

    private void createMyLittleEyeAdvancement(AdvancementEntry parent, Consumer<AdvancementEntry> consumer) {
        String id = "my_little_eye";
        Advancement.Builder.create().parent(parent)
                .display(ModBlocks.COPPER_SENSOR_CHARGED, Text.translatable(titleTranslationKey(id)),
                        Text.translatable(descriptionTranslationKey(id)), null, AdvancementFrame.TASK, true, true,
                        false).criterion(hasItem(ModBlocks.COPPER_SENSOR_CHARGED),
                        InventoryChangedCriterion.Conditions.items(ModBlocks.COPPER_SENSOR_CHARGED))
                .build(consumer, advancementId(id));
    }

    private AdvancementEntry createWirelessPossibilitiesAdvancement(AdvancementEntry parent,
                                                                    Consumer<AdvancementEntry> consumer) {
        String id = "wireless_possibilities";
        return Advancement.Builder.create().parent(parent)
                .display(ModBlocks.COPPER_RELAY_CHARGED, Text.translatable(titleTranslationKey(id)),
                        Text.translatable(descriptionTranslationKey(id)), null, AdvancementFrame.TASK, true, true,
                        false).criterion(hasItem(ModBlocks.COPPER_RELAY_CHARGED),
                        InventoryChangedCriterion.Conditions.items(ModBlocks.COPPER_RELAY_CHARGED))
                .build(consumer, advancementId(id));
    }

    private void createHighFrequencyAdvancement(AdvancementEntry parent, Consumer<AdvancementEntry> consumer) {
        String id = "high_frequency";
        Advancement.Builder.create().parent(parent).display(ModItems.RADIO, Text.translatable(titleTranslationKey(id)),
                        Text.translatable(descriptionTranslationKey(id)), null, AdvancementFrame.TASK, true, true, false)
                .criterion("triggered_radio_bound_relay", createCriterion(ModCriteria.TRIGGERED_RADIO_BOUND_RELAY))
                .build(consumer, advancementId(id));
    }

    private void createNoStringsAttachedAdvancement(AdvancementEntry parent, Consumer<AdvancementEntry> consumer) {
        String id = "no_strings_attached";
        Advancement.Builder.create().parent(parent)
                .display(ModBlocks.COPPER_RELAY_BOUND, Text.translatable(titleTranslationKey(id)),
                        Text.translatable(descriptionTranslationKey(id)), null, AdvancementFrame.TASK, true, true,
                        false).criterion("bound_relay", createCriterion(ModCriteria.BOUND_RELAY))
                .build(consumer, advancementId(id));
    }

    private AdvancementCriterion<TickCriterion.Conditions> createCriterion(TickCriterion criterion) {
        return criterion.create(new TickCriterion.Conditions(Optional.empty()));
    }

    private String titleTranslationKey(String id) {
        return "advancements.copperworks." + id + ".title";
    }

    private String descriptionTranslationKey(String id) {
        return "advancements.copperworks." + id + ".description";
    }

    private String hasItem(ItemConvertible item) {
        return "has_" + Registries.ITEM.getId(item.asItem()).getPath();
    }

    private String advancementId(String id) {
        return Copperworks.id(id).toString();
    }
}
