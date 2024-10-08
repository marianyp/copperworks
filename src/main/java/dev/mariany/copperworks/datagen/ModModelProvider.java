package dev.mariany.copperworks.datagen;

import dev.mariany.copperworks.block.ModBlocks;
import dev.mariany.copperworks.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.data.client.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.COPPER_CLOCK);
        registerBattery(blockStateModelGenerator, ModBlocks.COPPER_BATTERY);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.COPPER_BRACER, Models.GENERATED);
        itemModelGenerator.register(ModItems.COPPER_BROADSWORD, Models.HANDHELD);
        itemModelGenerator.register(ModItems.COPPER_PLATE, Models.GENERATED);
        itemModelGenerator.register(ModItems.IRON_PLATE, Models.GENERATED);
        itemModelGenerator.register(ModBlocks.COPPER_FRAME.asItem(), Models.GENERATED);
        itemModelGenerator.register(ModBlocks.COPPER_LEVER.asItem(), Models.GENERATED);
        itemModelGenerator.register(ModItems.ENDER_POWDER, Models.GENERATED);
    }

    private void registerBattery(BlockStateModelGenerator blockStateModelGenerator, Block battery) {
        TextureMap textureMap = new TextureMap().put(TextureKey.TOP, TextureMap.getSubId(battery, "_top"))
                .put(TextureKey.SIDE, TextureMap.getSubId(battery, "_side"))
                .put(TextureKey.BOTTOM, TextureMap.getSubId(battery, "_bottom"));

        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(battery,
                        BlockStateVariant.create().put(VariantSettings.MODEL,
                                Models.CUBE_BOTTOM_TOP.upload(battery, textureMap, blockStateModelGenerator.modelCollector)))
                .coordinate(BlockStateVariantMap.create(Properties.BLOCK_FACE, Properties.HORIZONTAL_FACING)
                        .register(BlockFace.FLOOR, Direction.NORTH, BlockStateVariant.create())
                        .register(BlockFace.FLOOR, Direction.EAST,
                                BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R90))
                        .register(BlockFace.FLOOR, Direction.SOUTH,
                                BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R180))
                        .register(BlockFace.FLOOR, Direction.WEST,
                                BlockStateVariant.create().put(VariantSettings.Y, VariantSettings.Rotation.R270))
                        .register(BlockFace.WALL, Direction.NORTH,
                                BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90))
                        .register(BlockFace.WALL, Direction.EAST,
                                BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90)
                                        .put(VariantSettings.Y, VariantSettings.Rotation.R90))
                        .register(BlockFace.WALL, Direction.SOUTH,
                                BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90)
                                        .put(VariantSettings.Y, VariantSettings.Rotation.R180))
                        .register(BlockFace.WALL, Direction.WEST,
                                BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R90)
                                        .put(VariantSettings.Y, VariantSettings.Rotation.R270))
                        .register(BlockFace.CEILING, Direction.SOUTH,
                                BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180))
                        .register(BlockFace.CEILING, Direction.WEST,
                                BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180)
                                        .put(VariantSettings.Y, VariantSettings.Rotation.R90))
                        .register(BlockFace.CEILING, Direction.NORTH,
                                BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180)
                                        .put(VariantSettings.Y, VariantSettings.Rotation.R180))
                        .register(BlockFace.CEILING, Direction.EAST,
                                BlockStateVariant.create().put(VariantSettings.X, VariantSettings.Rotation.R180)
                                        .put(VariantSettings.Y, VariantSettings.Rotation.R270))));
    }

//    private void registerBattery(BlockStateModelGenerator blockStateModelGenerator, Block battery) {
//        TextureMap textureMap = new TextureMap().put(TextureKey.TOP, TextureMap.getSubId(battery, "_top"))
//                .put(TextureKey.SIDE, TextureMap.getSubId(battery, "_side"))
//                .put(TextureKey.BOTTOM, TextureMap.getSubId(battery, "_bottom"));
//
//        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(battery,
//                        BlockStateVariant.create().put(VariantSettings.MODEL,
//                                Models.CUBE_BOTTOM_TOP.upload(battery, textureMap, blockStateModelGenerator.modelCollector)))
//                .coordinate(blockStateModelGenerator.createUpDefaultFacingVariantMap()));
//    }
}

