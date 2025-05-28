package galena.oreganized.client.tooltips;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

import java.awt.*;

public class ClientThermometerTooltip implements ClientTooltipComponent {
    private int heat = 0;
    public static Component[] TEXT = new Component[]{Component.translatable("tooltip.oreganized.heat1")
            ,Component.translatable("tooltip.oreganized.heat2"),Component.translatable("tooltip.oreganized.heat3")
            ,Component.translatable("tooltip.oreganized.heat4"),Component.translatable("tooltip.oreganized.heat5")
            ,Component.translatable("tooltip.oreganized.heat6"),Component.translatable("tooltip.oreganized.heat7")
            ,Component.translatable("tooltip.oreganized.heat8"),
            Component.translatable("tooltip.oreganized.heat9")};
    public ClientThermometerTooltip(ThermometerTooltip thermometerTooltip) {
        this.heat = thermometerTooltip.getHeat();
    }
    @Override
    public int getHeight() {
        return 14;
    }

    public void renderImage(Font pFont, int pX, int pY, GuiGraphics pGuiGraphics) {
        float heatF = heat/8f;
        int i = 0;
        int offset = 0;
        long time = Minecraft.getInstance().level.getGameTime();
        double delta = Minecraft.getInstance().getDeltaFrameTime();
        String textGot = TEXT[heat].getString();
        for(char a : textGot.toCharArray()){
            pGuiGraphics.pose().pushPose();
            pGuiGraphics.pose().translate(0,(Math.sin(i*1.2+(time+delta)/24*heat)*heat/3),0);
            pGuiGraphics.drawString(pFont,Character.toString(a),pX+offset, pY,new Color(0.5f+heatF/2,1-heatF/2,1-heatF/2).getRGB(),true);
            i+=1;
            offset+=pFont.width(Character.toString(a));
            pGuiGraphics.pose().popPose();
        }

    }

    @Override
    public int getWidth(Font pFont) {
        return 20;
    }
}
