package dev.mariany.copperworks.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.MathHelper;

public class EnhancedSculkSensorScreenHandler extends ScreenHandler {
    private static final int[] STEPS = {1, 8, 16, 32, 64};

    private static final int[] INVENTORY_TOP_LEFT = {8, 107};
    private static final int[] HOTBAR_TOP_LEFT = {8, 165};

    private final PropertyDelegate propertyDelegate;

    public EnhancedSculkSensorScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new ArrayPropertyDelegate(16));
    }

    public EnhancedSculkSensorScreenHandler(int syncId, PlayerInventory playerInventory,
                                            PropertyDelegate propertyDelegate) {
        super(ModScreenHandlers.ENHANCED_SCULK_SENSOR_HANDLER, syncId);
        checkDataCount(propertyDelegate, 2);
        this.propertyDelegate = propertyDelegate;
        this.addProperties(propertyDelegate);

        this.addPlayerInventory(playerInventory);
        this.addPlayerHotbar(playerInventory);
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 9; ++column) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18,
                        INVENTORY_TOP_LEFT[1] + row * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
            this.addSlot(new Slot(playerInventory, hotbarSlot, 8 + hotbarSlot * 18, HOTBAR_TOP_LEFT[1]));
        }
    }

    public int calculateStepValue(int stepIndex) {
        return STEPS[MathHelper.clamp(stepIndex, 0, STEPS.length - 1)];
    }

    public int getStepIndex(int sliderValue) {
        for (int i = STEPS.length - 1; i > 0; i--) {
            if (sliderValue >= STEPS[i]) {
                return i;
            }
        }
        return 0;
    }

    public int getRangeSteps() {
        return STEPS.length;
    }

    public boolean isFrequencyEnabled(int frequency) {
        return this.propertyDelegate.get(frequency) == 1;
    }

    public int getRange() {
        return this.propertyDelegate.get(0);
    }

    public void toggleFrequency(int value) {
        this.propertyDelegate.set(value, 0);
    }

    public void setRange(int value) {
        this.propertyDelegate.set(0, value);
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        if (id >= 1 && id <= 15) {
            toggleFrequency(id);
        } else if (id > 15) {
            int stepIndex = id - 16;
            setRange(calculateStepValue(stepIndex));
        }
        return true;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
