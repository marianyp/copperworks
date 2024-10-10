package dev.mariany.copperworks.attachment;

import com.mojang.serialization.Codec;
import dev.mariany.copperworks.Copperworks;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.UUID;

public final class ModAttachmentTypes {
    public static final AttachmentType<ItemStack> UPGRADING_ITEM = AttachmentRegistry.<ItemStack>builder()
            .persistent(ItemStack.OPTIONAL_CODEC).initializer(() -> ItemStack.EMPTY)
            .buildAndRegister(Copperworks.id("upgrading_item"));
    public static final AttachmentType<Integer> UPGRADE_PROGRESS = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT).initializer(() -> 0).buildAndRegister(Copperworks.id("upgrade_progress"));
    public static final AttachmentType<Vec3d> SHOCK_ORIGIN = AttachmentRegistry.<Vec3d>builder().persistent(Vec3d.CODEC)
            .buildAndRegister(Copperworks.id("shock_origin"));
    public static final AttachmentType<List<UUID>> SHOCK_IGNORE = AttachmentRegistry.<List<UUID>>builder()
            .persistent(Uuids.CODEC.listOf()).initializer(List::of).buildAndRegister(Copperworks.id("shock_ignore"));
    public static final AttachmentType<Integer> SHOCK_CHAIN = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT).initializer(() -> 0).buildAndRegister(Copperworks.id("shock_chain"));
    public static final AttachmentType<Float> SHOCK_DAMAGE = AttachmentRegistry.<Float>builder().persistent(Codec.FLOAT)
            .initializer(() -> 0F).buildAndRegister(Copperworks.id("shock_damage"));
    public static final AttachmentType<Integer> SHOCK_DELAY = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT).initializer(() -> 0).buildAndRegister(Copperworks.id("shock_delay"));
    public static final AttachmentType<Integer> SHOCK_DELAY_PROGRESS = AttachmentRegistry.<Integer>builder()
            .persistent(Codec.INT).initializer(() -> 0).buildAndRegister(Copperworks.id("shock_delay_progress"));

    public static void registerAttachmentTypes() {
        Copperworks.LOGGER.info("Registering Attachment Types for " + Copperworks.MOD_ID);
    }
}
