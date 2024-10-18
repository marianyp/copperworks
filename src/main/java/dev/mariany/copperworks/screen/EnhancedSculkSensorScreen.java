package dev.mariany.copperworks.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.mariany.copperworks.Copperworks;
import dev.mariany.copperworks.screen.frequency.FrequencyIcons;
import dev.mariany.copperworks.screen.frequency.FrequencyTranslations;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class EnhancedSculkSensorScreen extends HandledScreen<EnhancedSculkSensorScreenHandler> {
    private static final Identifier TEXTURE = Copperworks.id(
            "textures/gui/container/enhanced_sculk_sensor/enhanced_sculk_sensor_gui.png");

    private static final Identifier FREQUENCY_BACKGROUND_TEXTURE = Copperworks.id(
            "container/enhanced_sculk_sensor/frequency_background");
    private static final Identifier FREQUENCY_SELECTED_TEXTURE = Copperworks.id(
            "container/enhanced_sculk_sensor/frequency_background_selected");

    private static final Identifier RANGE_SLIDER_HANDLE_TEXTURE = Copperworks.id(
            "container/enhanced_sculk_sensor/range_slider_handle");
    private static final Identifier RANGE_SLIDER_HANDLE_SELECTED_TEXTURE = Copperworks.id(
            "container/enhanced_sculk_sensor/range_slider_handle_selected");

    private static final String RANGE_TOOLTIP_TRANSLATION_KEY = "container.copperworks.enhanced_sculk_sensor.range.tooltip";

    private static final int[] FREQUENCY_TOP_LEFT = {7, 21};
    private static final int[] RANGE_SLIDER_HANDLE_TOP_LEFT = {136, 72};

    private static final int[] FREQUENCY_BUTTON_SIZE = {20, 20};
    private static final int[] RANGE_SLIDER_HANDLE_SIZE = {23, 6};

    private static final int RANGE_SLIDER_HEIGHT = 44;

    private static final float RANGE_SLIDER_DAMPING = 0.75F;

    private boolean initializedRange = false;
    private boolean sliding;
    private float scrollAmount;
    private int step = -1;

    public EnhancedSculkSensorScreen(EnhancedSculkSensorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 189;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!initializedRange) {
            int currentRange = this.handler.getRange();
            int stepIndex = this.handler.getStepIndex(currentRange);
            int steps = this.handler.getRangeSteps();
            this.scrollAmount = stepIndex / (float) (steps - 1);
            this.step = stepIndex;
            this.initializedRange = true;
        }

        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        this.drawFrequencyOptions(context, mouseX, mouseY);
        this.drawRangeSlider(context, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && this.client != null && this.client.interactionManager != null) {
            this.sliding = false;

            int buttonX = this.x + FREQUENCY_TOP_LEFT[0];
            int buttonY = this.y + FREQUENCY_TOP_LEFT[1];

            // Button
            for (int i = 0; i < 15; i++) {
                int col = i % 5;
                int row = i / 5;
                int x = buttonX + col * (FREQUENCY_BUTTON_SIZE[0] + 2);
                int y = buttonY + row * (FREQUENCY_BUTTON_SIZE[1] + 2);

                if (mouseX >= x && mouseX < x + FREQUENCY_BUTTON_SIZE[0] && mouseY >= y && mouseY < y + FREQUENCY_BUTTON_SIZE[1]) {
                    int frequency = i + 1;
                    this.client.interactionManager.clickButton(this.handler.syncId, frequency);
                    playClickSound();
                    return true;
                }
            }

            // Slider
            int sliderX = this.x + RANGE_SLIDER_HANDLE_TOP_LEFT[0];
            int sliderY = this.y + RANGE_SLIDER_HANDLE_TOP_LEFT[1] - (int) (RANGE_SLIDER_HEIGHT * this.scrollAmount);
            if (mouseX >= sliderX && mouseX < sliderX + RANGE_SLIDER_HANDLE_SIZE[0] && mouseY >= sliderY && mouseY < sliderY + RANGE_SLIDER_HANDLE_SIZE[1]) {
                this.sliding = true;
                playClickSound();
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.sliding) {
            int minY = this.y + RANGE_SLIDER_HANDLE_TOP_LEFT[1];
            int maxY = minY + RANGE_SLIDER_HEIGHT - RANGE_SLIDER_HANDLE_SIZE[1];

            float range = (float) (minY - maxY);

            this.scrollAmount = MathHelper.clamp(this.scrollAmount + (float) (deltaY * RANGE_SLIDER_DAMPING / range),
                    0.0F, 1.0F);

            int steps = this.handler.getRangeSteps();
            this.step = Math.round(this.scrollAmount * (steps - 1));

            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }


    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.sliding) {
            this.sliding = false;

            int steps = this.handler.getRangeSteps();
            int closestStep = Math.round(this.scrollAmount * (steps - 1));

            this.scrollAmount = closestStep / (float) (steps - 1);

            updateRange(closestStep);

            playClickSound(0.95F, 0.15F);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void updateRange(int value) {
        if (this.client != null && this.client.interactionManager != null) {
            this.client.interactionManager.clickButton(this.handler.syncId, 16 + value);
        }
    }

    private void playClickSound() {
        playClickSound(1F, 0.3F);
    }

    private void playClickSound(float pitch, float volume) {
        if (this.client != null) {
            this.client.getSoundManager()
                    .play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK.value(), pitch, volume));
        }
    }

    private void drawFrequencyOptions(DrawContext context, int mouseX, int mouseY) {
        int buttonX = this.x + FREQUENCY_TOP_LEFT[0];
        int buttonY = this.y + FREQUENCY_TOP_LEFT[1];

        List<Identifier> textures = FrequencyIcons.all();
        List<String> tooltipTranslationKeys = FrequencyTranslations.all();

        for (int i = 0; i < 15; i++) {
            int col = i % 5;
            int row = i / 5;

            int x = buttonX + col * (FREQUENCY_BUTTON_SIZE[0] + 2);
            int y = buttonY + row * (FREQUENCY_BUTTON_SIZE[1] + 2);

            boolean isEnabled = this.handler.isFrequencyEnabled(i + 1);
            boolean isHovered = mouseX >= x && mouseX < x + FREQUENCY_BUTTON_SIZE[0] && mouseY >= y && mouseY < y + FREQUENCY_BUTTON_SIZE[1];

            Identifier texture = isEnabled || isHovered ? FREQUENCY_SELECTED_TEXTURE : FREQUENCY_BACKGROUND_TEXTURE;
            context.drawGuiTexture(texture, x, y, FREQUENCY_BUTTON_SIZE[0], FREQUENCY_BUTTON_SIZE[1]);
            context.drawGuiTexture(textures.get(i), x + 2, y + 2, 16, 16);

            if (isHovered) {
                int tooltipX = mouseX + 6;
                int tooltipY = mouseY + 10;
                String tooltipTranslationKey = tooltipTranslationKeys.get(i);
                context.drawTooltip(this.textRenderer, Text.translatable(tooltipTranslationKey), tooltipX, tooltipY);
            }
        }
    }

    private void drawRangeSlider(DrawContext context, int mouseX, int mouseY) {
        if (this.step > -1) {
            int sliderX = this.x + RANGE_SLIDER_HANDLE_TOP_LEFT[0];
            int minY = this.y + RANGE_SLIDER_HANDLE_TOP_LEFT[1];
            int maxY = minY - RANGE_SLIDER_HEIGHT;

            int steps = this.handler.getRangeSteps();
            float currentScrollAmount = this.step / (float) (steps - 1);
            int sliderY = (int) (minY + (maxY - minY) * currentScrollAmount);

            boolean isHovered = mouseX >= sliderX && mouseX < sliderX + RANGE_SLIDER_HANDLE_SIZE[0] && mouseY >= sliderY && mouseY < sliderY + RANGE_SLIDER_HANDLE_SIZE[1];

            Identifier texture = this.sliding || isHovered ? RANGE_SLIDER_HANDLE_SELECTED_TEXTURE : RANGE_SLIDER_HANDLE_TEXTURE;
            context.drawGuiTexture(texture, sliderX, sliderY, RANGE_SLIDER_HANDLE_SIZE[0], RANGE_SLIDER_HANDLE_SIZE[1]);

            if (isHovered || this.sliding) {
                int tooltipX = sliderX + 24;
                int tooltipY = Math.clamp(mouseY, maxY, minY) + 10;

                context.drawTooltip(this.textRenderer,
                        Text.translatable(RANGE_TOOLTIP_TRANSLATION_KEY, this.handler.calculateStepValue(this.step)),
                        tooltipX, tooltipY);
            }
        }
    }
}
