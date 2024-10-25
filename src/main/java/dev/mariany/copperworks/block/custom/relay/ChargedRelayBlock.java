package dev.mariany.copperworks.block.custom.relay;

import dev.mariany.copperworks.advancement.criterion.ModCriteria;
import dev.mariany.copperworks.block.ModBlocks;
import dev.mariany.copperworks.block.entity.custom.relay.BoundRelayBlockEntity;
import dev.mariany.copperworks.item.ModItems;
import dev.mariany.copperworks.item.component.CopperworksComponents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

import java.util.List;

public class ChargedRelayBlock extends AbstractRelayBlock {
    private static final List<Item> BINDING_ITEMS = List.of(Items.AMETHYST_SHARD, ModItems.AMETHYST_PIECE);

    public ChargedRelayBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
                                             PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!BINDING_ITEMS.contains(stack.getItem())) {
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        }

        if (world instanceof ServerWorld serverWorld) {
            if (stack.isOf(Items.AMETHYST_SHARD)) {
                ItemStack piece = ItemUsage.exchangeStack(stack, player, initiateBinding(serverWorld, pos), false);
                player.setStackInHand(hand, piece);
                playInsertSound(serverWorld, pos, 0);
            }

            if (stack.isOf(ModItems.AMETHYST_PIECE)) {
                if (!completeBinding(player, stack, GlobalPos.create(serverWorld.getRegistryKey(), pos))) {
                    return ItemActionResult.FAIL;
                }

                stack.decrement(1);
                playInsertSound(serverWorld, pos, 0.65F);
            }

            player.swingHand(hand, true);
        }

        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private ItemStack initiateBinding(World world, BlockPos pos) {
        ItemStack stack = ModItems.AMETHYST_PIECE.getDefaultStack();
        stack.set(CopperworksComponents.RELAY_POSITION, GlobalPos.create(world.getRegistryKey(), pos));
        return stack;
    }

    private boolean completeBinding(PlayerEntity player, ItemStack piece, GlobalPos thisPos) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            MinecraftServer server = serverPlayer.getServer();

            if (server == null) {
                return false;
            }

            GlobalPos otherPos = piece.get(CopperworksComponents.RELAY_POSITION);

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
                ModCriteria.BOUND_RELAY.trigger(serverPlayer);
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

    private void playInsertSound(ServerWorld world, BlockPos pos, float pitch) {
        world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                RegistryEntry.of(SoundEvents.BLOCK_AMETHYST_BLOCK_STEP), SoundCategory.BLOCKS, 0.7F, pitch);
    }
}
