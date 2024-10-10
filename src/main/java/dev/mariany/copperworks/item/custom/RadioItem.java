package dev.mariany.copperworks.item.custom;

import dev.mariany.copperworks.block.ModBlocks;
import dev.mariany.copperworks.block.custom.relay.ChargedRelayBlock;
import dev.mariany.copperworks.block.custom.relay.RadioBoundRelayBlock;
import dev.mariany.copperworks.item.component.ModComponents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RadioItem extends Item {
    private static final int NOT_LOADED = -1;
    private static final int NOT_FOUND = 0;
    private static final int SUCCESS = 1;
    private static final int NO_DATA = 2;

    public RadioItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        ItemStack stack = context.getStack();

        BlockState blockState = world.getBlockState(blockPos);

        if (blockState.getBlock() instanceof ChargedRelayBlock) {
            world.setBlockState(blockPos, ModBlocks.COPPER_RELAY_RADIO_BOUND.getDefaultState(), Block.NOTIFY_LISTENERS);
            stack.set(ModComponents.RELAY_POSITION, GlobalPos.create(world.getRegistryKey(), blockPos));

            return ActionResult.success(world.isClient);
        }

        return super.useOnBlock(context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack radioStack = user.getStackInHand(hand);
        if (getBoundPos(radioStack) != null) {
            if (world instanceof ServerWorld serverWorld) {
                int result = triggerRelay(serverWorld, radioStack);
                switch (result) {
                    case SUCCESS -> {
                        // Play success sound
                        break;
                    }
                    case NOT_LOADED -> {
                        // Play failure sound
                        break;
                    }
                    case NOT_FOUND -> {
                        radioStack.remove(ModComponents.RELAY_POSITION);
                        break;
                    }
                    case NO_DATA -> {
                        radioStack.remove(ModComponents.RELAY_POSITION);
                    }
                }
            }
        }
        return super.use(world, user, hand);
    }

    public int triggerRelay(ServerWorld world, ItemStack radioStack) {
        GlobalPos globalPos = getBoundPos(radioStack);

        if (globalPos == null) {
            return NO_DATA;
        }

        ServerWorld boundWorld = world.getServer().getWorld(globalPos.dimension());

        if (boundWorld == null) {
            return NO_DATA;
        }

        ChunkPos chunkPos = boundWorld.getChunk(globalPos.pos()).getPos();

        if (!boundWorld.isChunkLoaded(chunkPos.toLong())) {
            return NOT_LOADED;
        }

        BlockState blockState = boundWorld.getBlockState(globalPos.pos());

        if (!(blockState.getBlock() instanceof RadioBoundRelayBlock)) {
            return NOT_FOUND;
        }

        world.scheduleBlockTick(globalPos.pos(), blockState.getBlock(), 0);

        return SUCCESS;
    }

    @Nullable
    private GlobalPos getBoundPos(ItemStack radioStack) {
        return radioStack.get(ModComponents.RELAY_POSITION);
    }
}
