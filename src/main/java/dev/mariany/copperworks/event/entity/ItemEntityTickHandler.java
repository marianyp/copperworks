package dev.mariany.copperworks.event.entity;

import dev.mariany.copperworks.event.server.ServerWorldTickHandler;
import dev.mariany.copperworks.item.component.CopperworksComponents;
import dev.mariany.copperworks.tag.CopperworksTags;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;

public class ItemEntityTickHandler implements ServerWorldTickHandler {
    @Override
    public void onServerWorldTick(ServerWorld world) {
        for (ItemEntity itemEntity : world.getEntitiesByType(TypeFilter.instanceOf(ItemEntity.class),
                entity -> !entity.isRemoved())) {
            onItemEntityTick(itemEntity);
        }
    }

    private void onItemEntityTick(ItemEntity itemEntity) {
        handleLastThrown(itemEntity);
    }

    private void handleLastThrown(ItemEntity itemEntity) {
        ItemStack itemStack = itemEntity.getStack();

        // Only works for item stacks with a max size of 1 to avoid inventory stacking issues
        if (itemStack.getMaxCount() == 1 && itemStack.isIn(CopperworksTags.Items.ENGINEER_CAN_UPGRADE)) {
            if (!itemStack.contains(CopperworksComponents.LAST_THROWN)) {
                if (itemEntity.getOwner() instanceof PlayerEntity player) {
                    itemStack.set(CopperworksComponents.LAST_THROWN, player.getUuid());
                    itemEntity.setStack(itemStack);
                }
            }
        }
    }
}
