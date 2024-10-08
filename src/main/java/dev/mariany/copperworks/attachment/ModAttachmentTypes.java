package dev.mariany.copperworks.attachment;

import com.mojang.serialization.Codec;
import dev.mariany.copperworks.Copperworks;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.item.ItemStack;

public final class ModAttachmentTypes {
    public static final AttachmentType<ItemStack> UPGRADING_ITEM = AttachmentRegistry.<ItemStack>builder()
            .persistent(ItemStack.OPTIONAL_CODEC).initializer(() -> ItemStack.EMPTY)
            .buildAndRegister(Copperworks.id("upgrading_item"));
    public static final AttachmentType<Integer> UPGRADE_PROGRESS = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT).initializer(() -> 0).buildAndRegister(Copperworks.id("upgrade_progress"));

    public static void registerAttachmentTypes() {
        Copperworks.LOGGER.info("Registering Attachment Types for " + Copperworks.MOD_ID);
    }
}
