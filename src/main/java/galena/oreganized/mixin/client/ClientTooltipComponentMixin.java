package galena.oreganized.mixin.client;

import galena.oreganized.client.tooltips.ClientThermometerTooltip;
import galena.oreganized.client.tooltips.ThermometerTooltip;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraftforge.client.gui.ClientTooltipComponentManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientTooltipComponentManager.class)
public class ClientTooltipComponentMixin {
    @Inject(method = "createClientTooltipComponent(Lnet/minecraft/world/inventory/tooltip/TooltipComponent;)Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;",at=@At("HEAD"),cancellable = true,remap = false)
    private static void create(TooltipComponent component, CallbackInfoReturnable<ClientTooltipComponent> cir){
        if(component instanceof ThermometerTooltip thermometerTooltip){
            cir.cancel();
            cir.setReturnValue(new ClientThermometerTooltip(thermometerTooltip));
        }

    }
}
