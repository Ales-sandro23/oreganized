package galena.oreganized.compat.create;

import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import galena.oreganized.Oreganized;
import net.minecraft.core.Registry;
import net.minecraftforge.registries.DeferredRegister;

public class CreateCompat {

    public static final DeferredRegister<ArmInteractionPointType> ARM_INTERACTION_POINT_TYPES = DeferredRegister.create(CreateBuiltInRegistries.ARM_INTERACTION_POINT_TYPE.key(), Oreganized.MOD_ID);

    public static void register() {
        ARM_INTERACTION_POINT_TYPES.register("gargoyle", GargoyleArmPointType::new);
    }

}
