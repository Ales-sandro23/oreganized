package galena.oreganized.client.tooltips;

import net.minecraft.world.inventory.tooltip.TooltipComponent;

public class ThermometerTooltip implements TooltipComponent {

    private final int heat;

    public ThermometerTooltip( int pWeight) {

        this.heat = pWeight;
    }



    public int getHeat() {
        return this.heat;
    }
}
