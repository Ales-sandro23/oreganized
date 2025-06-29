package galena.oreganized.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import galena.oreganized.client.accessors.GuiThermometerAccessor;
import galena.oreganized.client.render.gui.OGui;
import galena.oreganized.client.tooltips.ClientThermometerTooltip;
import galena.oreganized.index.OEffects;
import galena.oreganized.index.OItems;
import galena.oreganized.world.IMotionHolder;
import java.awt.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin implements GuiThermometerAccessor {

    @Shadow
    protected int toolHighlightTimer;

    @Shadow
    protected ItemStack lastToolHighlight;

    @Shadow
    public abstract Font getFont();

    @Shadow
    protected int screenWidth;

    @Shadow
    protected int screenHeight;

    @Shadow
    @Final
    protected Minecraft minecraft;

    @WrapOperation(
            method = "renderHearts(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/player/Player;IIIIFIIIZ)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHeart(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Gui$HeartType;IIIZZ)V"),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHeart(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Gui$HeartType;IIIZZ)V", ordinal = 1),
                    to = @At("TAIL")
            )
    )
    private void renderStunnedHeart(Gui instance, GuiGraphics graphics, Gui.HeartType type, int x, int y, int v, boolean blinking, boolean half, Operation<Void> original, @Local Player player) {
        if (player.hasEffect(OEffects.STUNNING.get()) && (type == Gui.HeartType.NORMAL || type == Gui.HeartType.POISIONED)) {
            var u = type.getX(half, blinking);
            OGui.renderStunnedHeart(graphics, u - 52, x, y, v / 5);
        } else {
            original.call(instance, graphics, type, x, y, v, blinking, half);
        }
    }

    @Override
    public void oreganized$setToolHighlightTimer(int toolHighlightTimer) {
        this.toolHighlightTimer = toolHighlightTimer;
    }

    @Inject(method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V", remap = false, at = @At("HEAD"))
    public void renderTooltips(GuiGraphics graphics, int yShift, CallbackInfo ci) {

        if (ci.isCancelled()) {
            return;
        }

        if (this.toolHighlightTimer > 0 && this.lastToolHighlight.getItem() == OItems.THERMOMETER.get()) {
            float heatF = this.lastToolHighlight.getTag().getInt("OreganizedHeat") / 8f;
            var heat = Component.empty().append(ClientThermometerTooltip.TEXT[this.lastToolHighlight.getTag().getInt("OreganizedHeat")]).withStyle((style) -> {
                return style.withColor(new Color(0.5f + heatF / 2, 1 - heatF / 2, 1 - heatF / 2).getRGB());
            });

            if (this.lastToolHighlight.hasCustomHoverName()) {
                heat.withStyle(ChatFormatting.ITALIC);
            }

            var highlightTip = this.lastToolHighlight.getHighlightTip(heat);
            int i = this.getFont().width(highlightTip);
            int j = (this.screenWidth - i) / 2;
            int k = this.screenHeight - Math.max(yShift, 59) - 12;
            if (!this.minecraft.gameMode.canHurtPlayer()) {
                k += 14;
            }

            int l = (int) ((float) this.toolHighlightTimer * 256.0F / 10.0F);
            if (l > 255) {
                l = 255;
            }

            if (l > 0) {
                graphics.fill(j - 2, k - 2, j + i + 2, k + 9 + 2, this.minecraft.options.getBackgroundColor(0));
                Font font = IClientItemExtensions.of(this.lastToolHighlight).getFont(this.lastToolHighlight, IClientItemExtensions.FontContext.SELECTED_ITEM_NAME);
                if (font == null) {
                    graphics.drawString(this.getFont(), highlightTip, j, k, 16777215 + (l << 24));
                } else {
                    j = (this.screenWidth - font.width(highlightTip)) / 2;
                    graphics.drawString(font, highlightTip, j, k, 16777215 + (l << 24));
                }
            }
        }

        if (this.toolHighlightTimer > 0 && this.lastToolHighlight.getItem() == OItems.SPEEDOMETER.get()) {
            long time = Minecraft.getInstance().level.getGameTime();
            var vehicle = (IMotionHolder) Minecraft.getInstance().player.getRootVehicle();
            double speed = vehicle.oreganised$getMotion();
            var tooltip = Component.empty().append(Component.translatable("tooltip.oreganized.speed", speed)).withStyle((style) -> {
                return style.withColor(new Color(1f, 1, 0.9f).getRGB());
            });
            var arrow = Component.empty().append("->").withStyle((style) -> {
                return style.withColor(new Color(1f, 1, 0.6f).getRGB());
            });

            if (this.lastToolHighlight.hasCustomHoverName()) {
                tooltip.withStyle(ChatFormatting.ITALIC);
            }

            var highlightTip = this.lastToolHighlight.getHighlightTip(tooltip);
            int i = this.getFont().width(highlightTip);
            int j = (this.screenWidth - i) / 2;
            int k = this.screenHeight - Math.max(yShift, 59) - 12;
            if (!this.minecraft.gameMode.canHurtPlayer()) {
                k += 14;
            }

            int l = (int) ((float) this.toolHighlightTimer * 256.0F / 10.0F);
            if (l > 255) {
                l = 255;
            }

            if (l > 0) {
                graphics.fill(j - 2, k - 2, j + i + 2, k + 9 + 2, this.minecraft.options.getBackgroundColor(0));
                Font font = IClientItemExtensions.of(this.lastToolHighlight).getFont(this.lastToolHighlight, IClientItemExtensions.FontContext.SELECTED_ITEM_NAME);
                if (font == null) {
                    graphics.drawString(this.getFont(), highlightTip, j, k, 16777215 + (l << 24));
                    graphics.drawString(this.getFont(), this.lastToolHighlight.getHighlightTip(arrow), j - 20 + (int) (Math.cos(time / 10f) * 5), k, 16777215 + (l << 24));
                } else {
                    j = (this.screenWidth - font.width(highlightTip)) / 2;
                    graphics.drawString(font, highlightTip, j, k, 16777215 + (l << 24));
                }
            }
        }
    }

}
