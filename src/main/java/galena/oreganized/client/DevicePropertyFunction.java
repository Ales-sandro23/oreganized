package galena.oreganized.client;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class DevicePropertyFunction implements ClampedItemPropertyFunction {
    private double rotation;
    private double rota;
    private long lastUpdateTick;

    @Override
    public float unclampedCall(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int i) {
        if (level == null) return 0;
        return wobble(level, Math.random());
    }

    private float wobble(Level level, double towards) {
        if (level.getGameTime() != lastUpdateTick) {
            lastUpdateTick = level.getGameTime();
            double diff = towards - rotation;
            diff = Mth.positiveModulo(diff + 0.5, 1.0) - (double) 0.5F;
            rota += diff * 0.1;
            rota *= 0.9;
            rotation = Mth.positiveModulo(rotation + rota, 1.0);
        }

        return (float) rotation;
    }

}
