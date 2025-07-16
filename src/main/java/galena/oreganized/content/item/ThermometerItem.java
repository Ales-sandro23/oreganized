package galena.oreganized.content.item;

import galena.oreganized.client.accessors.GuiThermometerAccessor;
import galena.oreganized.client.tooltips.ThermometerTooltip;
import galena.oreganized.content.block.IMeltableBlock;
import galena.oreganized.index.OTags;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class ThermometerItem extends Item {

    private static final int AMBIENT_RANGE = 5;
    public static final int HEAT_LEVELS = 8;

    public ThermometerItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        int heat = stack.getOrCreateTag().getInt("OreganizedHeat");
        return Optional.of(new ThermometerTooltip(heat));
    }

    private static int heatLevel(BlockState state, LevelAccessor level, BlockPos pos) {
        if (state.is(OTags.Blocks.LAVA_HEAT_LEVEL)) return 7;
        if (state.is(OTags.Blocks.FIRE_HEAT_LEVEL)) return 6;
        if (state.getLightEmission(level, pos) > 2) return 3;
        if (state.getBlock() instanceof IMeltableBlock block) {
            var goopyness = block.getGoopyness(state);
            if (goopyness > 1) return 6;
            if (goopyness > 0) return 4;
        }
        return 0;
    }

    public static int ambientMeasurement(Level level, BlockPos pos) {
        var biome = level.getBiome(pos).value();
        var temperature = biome.getBaseTemperature();

        var biomeHeatLevel = (int) (Math.max(0, Math.min(2, temperature) / 2) * (HEAT_LEVELS - 1));

        return biomeHeatLevel;
    }

    public static int activeMeasurement(Level level, BlockPos pos) {
        var box = new AABB(pos).inflate(AMBIENT_RANGE);
        var lavaDistance = BlockPos.betweenClosedStream(box)
                .filter(it -> level.getBlockState(it).is(OTags.Blocks.LAVA_HEAT_LEVEL))
                .mapToInt(it -> it.distManhattan(pos))
                .min()
                .orElse(Integer.MAX_VALUE);

        var state = level.getBlockState(pos);
        var blockHeatLevel = heatLevel(state, level, pos);

        if (lavaDistance < 8) {
            var lavaHeatLevel = 7 - lavaDistance;
            if (lavaHeatLevel > blockHeatLevel) return lavaHeatLevel;
        }

        return blockHeatLevel;
    }

    private static int heatLevel(LivingEntity entity) {
        if(entity.getType() == EntityType.MAGMA_CUBE) return 7;
        if(entity.getType() == EntityType.BLAZE) return 6;
        if(entity.isOnFire()) return 4;

        if (entity.isInvertedHealAndHarm()) return 0;

        if(entity.getActiveEffects().stream().anyMatch(it -> !it.getEffect().isBeneficial())) return 3;

        return 2;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        var heatLevel = heatLevel(target);
        setHeatLevel(stack, player.level(), heatLevel);
        player.getCooldowns().addCooldown(stack.getItem(), 60);
        setLocked(stack, true);
        return super.interactLivingEntity(stack, player, target, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var heatLevel = activeMeasurement(context.getLevel(), context.getClickedPos());
        setHeatLevel(context.getItemInHand(), context.getLevel(), heatLevel);
        if (context.getPlayer() != null) {
            context.getPlayer().getCooldowns().addCooldown(context.getItemInHand().getItem(), 60);
        }
        setLocked(context.getItemInHand(), true);
        return InteractionResult.SUCCESS;
    }

    public static void setHeatLevel(ItemStack stack, Level level, int heatLevel) {
        var nbt = stack.getOrCreateTag();
        nbt.putInt("OreganizedHeat", heatLevel);
        if (level.isClientSide()) {
            if (Minecraft.getInstance().gui instanceof GuiThermometerAccessor accessor) {
                accessor.oreganized$setToolHighlightTimer(60);
            }
        }
    }

    public static boolean isLocked(ItemStack stack) {
        return Optional.ofNullable(stack.getTag())
                .filter(it -> it.getBoolean("Locked"))
                .isPresent();
    }

    private static void setLocked(ItemStack stack, boolean locked) {
        stack.getOrCreateTag().putBoolean("Locked", locked);
    }

    /*
    public InteractionResult useOnOld(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        BlockState state = world.getBlockState(pos);
        ItemStack item = context.getItemInHand();
        if (player.getCooldowns().isOnCooldown(OItems.THERMOMETER.get())) {
            return InteractionResult.FAIL;
        }
        player.getCooldowns().addCooldown(OItems.THERMOMETER.get(), 60);
        float sumheat = 0;
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                for (int y = -5; y <= 5; y++) {
                    BlockPos posNew = pos.offset(x, y, z);
                    BlockState stateAt = world.getBlockState(posNew);
                    float heat = 0;
                    if (HEATVALUE.get(stateAt.getBlock()) != null) {
                        heat = HEATVALUE.get(stateAt.getBlock());
                        heat *= (float) Math.pow(8.5 / (new Vec3(x, y, z).length() + 1), 0.3);
                        sumheat = Math.max(heat * heat, sumheat);
                    } else if (stateAt.getBlock().getLightEmission(stateAt, world, posNew) > 0) {
                        sumheat = Math.max(stateAt.getBlock().getLightEmission(stateAt, world, posNew) / 5f, sumheat);
                    }
                }
            }
        }


        int sumheatInt = Mth.clamp((int) Math.pow(sumheat, 0.5), 0, 8);

        CompoundTag nbt = item.getOrCreateTag();
        nbt.putInt("OreganizedHeat", sumheatInt);
        if (world.isClientSide() && context.getHand() == InteractionHand.MAIN_HAND
        ) {
            if (Minecraft.getInstance().gui instanceof GuiThermometerAccessor accessor) {
                accessor.oreganized$setToolHighlightTimer(60);
            }
        }
        return InteractionResult.SUCCESS;


    }
     */

}
