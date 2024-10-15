package dev.mariany.copperworks.event;

import dev.mariany.copperworks.item.component.CopperworksComponents;
import dev.mariany.copperworks.tag.ModTags;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.world.World;

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
        World world = itemEntity.getWorld();
        ItemStack itemStack = itemEntity.getStack();

        // Only works for item stacks with a max size of 1 to avoid inventory stacking issues
        if (itemStack.getMaxCount() == 1 && itemStack.isIn(ModTags.Items.ENGINEER_CAN_UPGRADE)) {
            boolean justCreated = itemEntity.age == 1 || !itemStack.contains(CopperworksComponents.LAST_THROWN);
            if (justCreated) {
                PlayerEntity player = world.getClosestPlayer(itemEntity, 3);
                if (player != null) {
                    itemStack.set(CopperworksComponents.LAST_THROWN, player.getUuid());
                    itemEntity.setStack(itemStack);
                }
            }
        }
    }
}
