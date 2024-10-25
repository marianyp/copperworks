package dev.mariany.copperworks.item.custom;

import dev.mariany.copperworks.Copperworks;
import dev.mariany.copperworks.advancement.criterion.ModCriteria;
import dev.mariany.copperworks.block.ModBlocks;
import dev.mariany.copperworks.mixin.ItemAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.SmithingTemplateItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.List;

public class CopperSmithingTemplateItem extends SmithingTemplateItem {
    private static final Formatting TITLE_FORMATTING = Formatting.GRAY;
    private static final Formatting DESCRIPTION_FORMATTING = Formatting.BLUE;
    private static final Text UPGRADE_TEXT = Text.translatable(
            Util.createTranslationKey("upgrade", Copperworks.id("copper_upgrade"))).formatted(TITLE_FORMATTING);
    private static final Text APPLIES_TO_TEXT = Text.translatable(
                    Util.createTranslationKey("item", Copperworks.id("smithing_template.copper_upgrade.applies_to")))
            .formatted(DESCRIPTION_FORMATTING);
    private static final Text INGREDIENTS_TEXT = Text.translatable(
                    Util.createTranslationKey("item", Copperworks.id("smithing_template.copper_upgrade.ingredients")))
            .formatted(DESCRIPTION_FORMATTING);
    private static final Text BASE_SLOT_DESCRIPTION_TEXT = Text.translatable(Util.createTranslationKey("item",
            Copperworks.id("smithing_template.copper_upgrade.base_slot_description")));
    private static final Text ADDITIONS_SLOT_DESCRIPTION_TEXT = Text.translatable(Util.createTranslationKey("item",
            Copperworks.id("smithing_template.copper_upgrade.additions_slot_description")));
    private static final Identifier EMPTY_SLOT_REDSTONE_DUST_TEXTURE = Identifier.ofVanilla(
            "item/empty_slot_redstone_dust");
    private static final Identifier EMPTY_SLOT_INGOT_TEXTURE = Identifier.ofVanilla("item/empty_slot_ingot");

    public CopperSmithingTemplateItem(int maxDamage) {
        super(APPLIES_TO_TEXT, INGREDIENTS_TEXT, UPGRADE_TEXT, BASE_SLOT_DESCRIPTION_TEXT,
                ADDITIONS_SLOT_DESCRIPTION_TEXT, List.of(EMPTY_SLOT_REDSTONE_DUST_TEXTURE),
                List.of(EMPTY_SLOT_INGOT_TEXTURE));
        ((ItemAccessor) this).setComponents(ComponentMap.of(this.getComponents(),
                ComponentMap.builder().add(DataComponentTypes.MAX_STACK_SIZE, 1).add(DataComponentTypes.DAMAGE, 0)
                        .add(DataComponentTypes.MAX_DAMAGE, maxDamage).build()));
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.isOf(Items.COPPER_INGOT);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        Hand hand = context.getHand();
        ItemStack itemStack = context.getStack();
        BlockPos blockPos = context.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);

        if (blockState.getBlock().equals(ModBlocks.WOODEN_RAIL)) {
            if (player != null) {
                EquipmentSlot slot = hand.equals(Hand.MAIN_HAND) ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
                itemStack.damage(1, player, slot);
            }

            BlockState newBlockState = ModBlocks.COPPER_RAIL.getDefaultState()
                    .with(Properties.RAIL_SHAPE, blockState.get(Properties.RAIL_SHAPE));

            world.setBlockState(blockPos, newBlockState);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Emitter.of(player, newBlockState));
            world.playSoundAtBlockCenter(blockPos, SoundEvents.BLOCK_SMITHING_TABLE_USE, SoundCategory.BLOCKS, 0.33F,
                    MathHelper.nextBetween(world.random, 0.7F, 1F), false);

            if (player instanceof ServerPlayerEntity serverPlayer) {
                ModCriteria.UPGRADE_WOODEN_RAIL.trigger(serverPlayer);
            }

            return ActionResult.success(world.isClient);
        }

        return super.useOnBlock(context);
    }
}
