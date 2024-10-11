package dev.mariany.copperworks.item.custom;

import dev.mariany.copperworks.block.ModBlocks;
import dev.mariany.copperworks.block.custom.relay.ChargedRelayBlock;
import dev.mariany.copperworks.block.custom.relay.RadioBoundRelayBlock;
import dev.mariany.copperworks.item.component.ModComponents;
import dev.mariany.copperworks.sound.ModSoundEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkLevelType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

public class RadioItem extends AbstractBindingItem {
    private static final int NOT_LOADED = -1;
    private static final int NOT_FOUND = 0;
    private static final int SUCCESS = 1;
    private static final int NO_DATA = 2;

    private static final int COOLDOWN = 8;

    public RadioItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos blockPos = context.getBlockPos();
        ItemStack stack = context.getStack();

        BlockState blockState = world.getBlockState(blockPos);

        if (blockState.getBlock() instanceof ChargedRelayBlock) {
            world.setBlockState(blockPos, ModBlocks.COPPER_RELAY_RADIO_BOUND.getDefaultState(), Block.NOTIFY_LISTENERS);
            stack.set(ModComponents.RELAY_POSITION, GlobalPos.create(world.getRegistryKey(), blockPos));

            if (player != null && world.isClient) {
                playUsedSound(player);
            }

            return ActionResult.success(world.isClient);
        }

        return super.useOnBlock(context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack radioStack = user.getStackInHand(hand);

        if (getBoundPos(radioStack) == null) {
            return super.use(world, user, hand);
        }

        if (world instanceof ServerWorld serverWorld) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) user;

            int result = triggerRelay(serverWorld, radioStack);

            switch (result) {
                case SUCCESS -> playUsedSound(serverPlayer);
                case NOT_LOADED, NO_DATA, NOT_FOUND -> {
                    playFailSound(serverPlayer);
                    if (result == NO_DATA || result == NOT_FOUND) {
                        radioStack.remove(ModComponents.RELAY_POSITION);
                    }
                }
            }

            user.getItemCooldownManager().set(this, COOLDOWN);
            user.swingHand(hand, true);

            return TypedActionResult.success(radioStack);
        }

        return TypedActionResult.pass(radioStack);
    }

    public int triggerRelay(ServerWorld world, ItemStack radioStack) {
        GlobalPos globalPos = getBoundPos(radioStack);

        if (globalPos == null) {
            return NO_DATA;
        }

        BlockPos boundBlockPos = globalPos.pos();
        ServerWorld boundWorld = world.getServer().getWorld(globalPos.dimension());

        if (boundWorld == null) {
            return NO_DATA;
        }

        if (!isChunkLoaded(boundWorld, boundBlockPos)) {
            return NOT_LOADED;
        }

        BlockState blockState = boundWorld.getBlockState(boundBlockPos);

        if (!(blockState.getBlock() instanceof RadioBoundRelayBlock)) {
            return NOT_FOUND;
        }

        if (!blockState.get(Properties.POWERED)) {
            world.getBlockTickScheduler().clearNextTicks(new BlockBox(boundBlockPos));
            world.scheduleBlockTick(boundBlockPos, blockState.getBlock(), 0);
        }

        return SUCCESS;
    }

    @Nullable
    private GlobalPos getBoundPos(ItemStack radioStack) {
        return radioStack.get(ModComponents.RELAY_POSITION);
    }

    private static boolean isChunkLoaded(ServerWorld world, BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        WorldChunk worldChunk = world.getChunkManager().getWorldChunk(chunkPos.x, chunkPos.z);
        return worldChunk != null && worldChunk.getLevelType() == ChunkLevelType.ENTITY_TICKING && world.isChunkLoaded(
                chunkPos.toLong());
    }

    private void playUsedSound(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            playSound(serverPlayer, ModSoundEvents.RADIO_USED);
        } else {
            player.getWorld()
                    .playSound(player, player.getBlockPos(), ModSoundEvents.RADIO_USED, SoundCategory.NEUTRAL, 1F, 1F);
        }
    }

    private void playFailSound(ServerPlayerEntity player) {
        playSound(player, ModSoundEvents.RADIO_FAIL);
    }

    private void playSound(ServerPlayerEntity player, SoundEvent sound) {
        ServerWorld world = player.getServerWorld();
        RegistryEntry<SoundEvent> soundEventRegistryEntry = Registries.SOUND_EVENT.getEntry(sound);

        player.networkHandler.sendPacket(
                new PlaySoundFromEntityS2CPacket(soundEventRegistryEntry, SoundCategory.NEUTRAL, player, 1F, 1F,
                        world.getRandom().nextLong()));
    }
}
