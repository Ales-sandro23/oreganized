package galena.oreganized.content.item;

import com.google.common.collect.ImmutableMap;
import galena.oreganized.client.accessors.GuiThermometerAccessor;
import galena.oreganized.client.tooltips.ThermometerTooltip;
import galena.oreganized.index.OItems;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ThermometerItem extends Item {
    protected static final Map<Block,Integer> HEATVALUE = (new ImmutableMap.Builder<Block, Integer>().put(Blocks.LAVA, 8).put(Blocks.LAVA_CAULDRON, 8).put(Blocks.FIRE,4).put(Blocks.MAGMA_BLOCK, 3).put(Blocks.TORCH, 2).put(Blocks.LANTERN, 2).put(Blocks.CAMPFIRE,3).put(Blocks.SOUL_CAMPFIRE,3).put(Blocks.SOUL_FIRE,3).put(Blocks.SOUL_LANTERN,2)).build();
    public ThermometerItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack pStack) {
        int heat = pStack.getOrCreateTag().getInt("OreganizedHeat");
        return Optional.of(new ThermometerTooltip(heat));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        BlockState state = world.getBlockState(pos);
        ItemStack item = context.getItemInHand();
        if(player.getCooldowns().isOnCooldown(OItems.THERMOMETER.get())){
            return InteractionResult.FAIL;
        }
        player.getCooldowns().addCooldown(OItems.THERMOMETER.get(), 60);
        float sumheat = 0;
        for(int x = -5; x <= 5; x++){
            for(int z = -5; z <= 5; z++){
                for(int y = -5; y <= 5; y++){
                    BlockPos posNew = pos.offset(x,y,z);
                    BlockState stateAt = world.getBlockState(posNew);
                    float heat = 0;
                    if(HEATVALUE.get(stateAt.getBlock())!=null){
                        heat=HEATVALUE.get(stateAt.getBlock());
                        heat*= (float) Math.pow(8.5/(new Vec3(x,y,z).length()+1),0.3);
                        sumheat=Math.max(heat*heat,sumheat);
                    }
                    else if(stateAt.getBlock().getLightEmission(stateAt,world,posNew)>0){
                        sumheat=Math.max(stateAt.getBlock().getLightEmission(stateAt,world,posNew)/5f,sumheat);
                    }
                }
            }
        }


            int sumheatInt = Mth.clamp((int) Math.pow(sumheat,0.5),0,8);

            CompoundTag nbt = item.getOrCreateTag();
            nbt.putInt("OreganizedHeat", sumheatInt);
            if(world.isClientSide() && context.getHand() == InteractionHand.MAIN_HAND
            ){
                if(Minecraft.getInstance().gui instanceof GuiThermometerAccessor accessor){
                    accessor.oreganized2$setToolHighlightTimer(60);
                }
            }
            return InteractionResult.SUCCESS;


    }
}
