package galena.oreganized.client.tooltips;

import galena.oreganized.content.item.ThermometerItem;
import java.awt.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.resources.language.I18n;

public class ClientThermometerTooltip implements ClientTooltipComponent {

    public static String getDescriptionId(int heat) {
        return "tooltip.oreganized.heat_" + heat;
    }

    public static int getColor(int heat) {
        float heatF = ((float) heat) / ThermometerItem.HEAT_LEVELS;
        return new Color(0.5f + heatF / 2, 1 - heatF / 2, 1 - heatF / 2).getRGB();
    }

    private final int heat;

    public ClientThermometerTooltip(ThermometerTooltip thermometerTooltip) {
        this.heat = thermometerTooltip.heat();
    }

    @Override
    public int getHeight() {
        return 14;
    }

    public void renderImage(Font pFont, int pX, int pY, GuiGraphics pGuiGraphics) {
        int i = 0;
        int offset = 0;
        long time = Minecraft.getInstance().level.getGameTime();
        double delta = Minecraft.getInstance().getDeltaFrameTime();
        var text = I18n.get(getDescriptionId(heat));
        for (char a : text.toCharArray()) {
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(0, (Math.sin(i * 1.2 + (time + delta) / 24 * heat) * heat / 3), 0);
            pGuiGraphics.drawString(pFont, Character.toString(a), pX + offset, pY, getColor(heat), true);
            i += 1;
            offset += pFont.width(Character.toString(a));
            pGuiGraphics.pose().popPose();
        }

    }

    @Override
    public int getWidth(Font pFont) {
        return 20;
    }
}
