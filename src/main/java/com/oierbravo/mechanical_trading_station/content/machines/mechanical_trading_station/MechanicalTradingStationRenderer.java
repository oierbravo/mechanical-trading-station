package com.oierbravo.mechanical_trading_station.content.machines.mechanical_trading_station;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.oierbravo.trading_station.content.trading_recipe.TradingRecipe;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class MechanicalTradingStationRenderer  extends KineticBlockEntityRenderer<MechanicalTradingStationBlockEntity> {
    public MechanicalTradingStationRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(MechanicalTradingStationBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {

        if (pBlockEntity.getRecipe().isPresent()) {
            pPoseStack.pushPose();
            pPoseStack.translate(.5, .5, .5);
            float rot = 0F;
            if(pBlockEntity.dynamicCycleBehaviour.isRunning()){
                rot = ((pBlockEntity.getLevel().getGameTime() + pPartialTick) % 90F) * 8;
            }
            pPoseStack.mulPose(Axis.YP.rotationDegrees(rot));
            pPoseStack.translate(0, .7d, 0);

            renderBlock(pPoseStack, pBufferSource, pPackedLight, pPackedOverlay,((TradingRecipe) pBlockEntity.getRecipe().get().value()).getResult(),pBlockEntity);
            pPoseStack.popPose();
        }

        if (VisualizationManager.supportsVisualization(pBlockEntity.getLevel()))
            return;

        VertexConsumer vb = pBufferSource.getBuffer(RenderType.solid());
        BlockState blockState = pBlockEntity.getBlockState();

        SuperByteBuffer superBuffer = CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, blockState, blockState.getValue(HORIZONTAL_FACING).getOpposite());
        standardKineticRotationTransform(superBuffer, pBlockEntity, pPackedLight).renderInto(pPoseStack, vb);
    }

    /*protected void renderBlock(PoseStack ms, MultiBufferSource buffer, int light, int overlay, ItemStack stack) {
        BakedModel model =  Minecraft.getInstance().getItemRenderer().getModel(stack,null, Minecraft.getInstance().player, 0);
        Minecraft.getInstance()
                .getItemRenderer()
                .render(stack, ItemDisplayContext.GROUND, false, ms, buffer, light, overlay, model);

    }*/
    protected void renderBlock(PoseStack ms, MultiBufferSource buffer, int light, int overlay, ItemStack stack, BlockEntity pBlockEntity) {
        Minecraft.getInstance()
                .getItemRenderer()
                .renderStatic(stack, ItemDisplayContext.GROUND, light, overlay, ms, buffer,pBlockEntity.getLevel(), 0);
    }
}
