package galena.oreganized.content.item;

import galena.oreganized.Oreganized;
import galena.oreganized.client.tooltips.DeviceTooltip;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DeviceItem extends Item {

    public static final int FRAMES = 10;
    public static final ResourceLocation PROPERTY_KEY = Oreganized.modLoc("device_value");
    public static final int TOOLTIP_COLOR = 0x8c2115;

    public DeviceItem(Properties properties) {
        super(properties);
    }

    private static void generateValue(ItemStack stack, RandomSource random) {
        stack.getOrCreateTag().putInt("Value", random.nextInt(999999));
    }

    public static OptionalInt getValue(ItemStack stack) {
        if (!stack.hasTag() || !stack.getOrCreateTag().contains("Value")) return OptionalInt.empty();
        return OptionalInt.of(stack.getOrCreateTag().getInt("Value"));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (getValue(stack).isPresent()) return super.use(level, player, hand);
        generateValue(stack, player.getRandom());
        player.playSound(SoundEvents.LODESTONE_COMPASS_LOCK, 1F, 1.5F);
        return InteractionResultHolder.success(stack);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return getValue(stack).stream()
                .<TooltipComponent>mapToObj(DeviceTooltip::new)
                .findAny();
    }

}
