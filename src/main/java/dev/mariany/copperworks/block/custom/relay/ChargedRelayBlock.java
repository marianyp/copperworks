package dev.mariany.copperworks.block.custom.relay;

import com.mojang.serialization.MapCodec;
import dev.mariany.copperworks.block.ModBlocks;
import dev.mariany.copperworks.block.entity.custom.relay.BoundRelayBlockEntity;
import dev.mariany.copperworks.item.ModItems;
import dev.mariany.copperworks.item.component.ModComponents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

public class ChargedRelayBlock extends AbstractRelayBlock {
    public ChargedRelayBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends ChargedRelayBlock> getCodec() {
        return null;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
                                             PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.isOf(Items.AMETHYST_SHARD)) {
            player.giveItemStack(initiateBinding(world, pos));
        } else if (stack.isOf(ModItems.AMETHYST_PIECE)) {
            if (!completeBinding(player, stack, GlobalPos.create(world.getRegistryKey(), pos))) {
                return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
            }
        } else {
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        }

        if (!world.isClient) {
            stack.decrement(1);
        }

        return ItemActionResult.success(world.isClient);
    }

    private ItemStack initiateBinding(World world, BlockPos pos) {
        ItemStack stack = ModItems.AMETHYST_PIECE.getDefaultStack();
        stack.set(ModComponents.RELAY_POSITION, GlobalPos.create(world.getRegistryKey(), pos));
        return stack;
    }

    private boolean completeBinding(PlayerEntity player, ItemStack piece, GlobalPos thisPos) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            MinecraftServer server = serverPlayer.getServer();

            if (server == null) {
                return false;
            }

            GlobalPos otherPos = piece.get(ModComponents.RELAY_POSITION);

            if (otherPos == null || otherPos.equals(thisPos)) {
                return false;
            }

            BlockPos thisBlockPos = thisPos.pos();
            ServerWorld thisWorld = server.getWorld(thisPos.dimension());

            BlockPos otherBlockPos = otherPos.pos();
            ServerWorld otherWorld = server.getWorld(otherPos.dimension());

            if (thisWorld == null || otherWorld == null) {
                return false;
            }

            BlockState state = otherWorld.getBlockState(otherBlockPos);

            if (state.getBlock().equals(this)) {
                createBoundRelay(thisWorld, thisBlockPos, otherPos);
                createBoundRelay(otherWorld, otherBlockPos, thisPos);
                return true;
            }
        }

        return false;
    }

    private void createBoundRelay(ServerWorld world, BlockPos pos, GlobalPos boundPos) {
        world.setBlockState(pos, ModBlocks.COPPER_RELAY_BOUND.getDefaultState(), Block.NOTIFY_LISTENERS);
        if (world.getBlockEntity(pos) instanceof BoundRelayBlockEntity boundRelayBlockEntity) {
            boundRelayBlockEntity.bind(boundPos);
        }
    }
}
