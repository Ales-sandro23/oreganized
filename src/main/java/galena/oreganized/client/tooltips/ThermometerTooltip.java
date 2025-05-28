package galena.oreganized.client.tooltips;

import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class ThermometerTooltip implements TooltipComponent {

    private final int heat;

    public ThermometerTooltip( int pWeight) {

        this.heat = pWeight;
    }



    public int getHeat() {
        return this.heat;
    }
}
