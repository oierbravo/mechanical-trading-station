package com.oierbravo.mechanical_trading_station.content.machines.mechanical_trading_station;

import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.oierbravo.trading_station.content.trading_station.ITradingStationBlockEntity;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Quaterniond;
import org.joml.QuaterniondInterpolator;

public class MechanicalTradingStationRenderer  extends KineticBlockEntityRenderer<MechanicalTradingStationBlockEntity> {
    public MechanicalTradingStationRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(MechanicalTradingStationBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {

        if(pBlockEntity instanceof MechanicalTradingStationBlockEntity) {
            ITradingStationBlockEntity blockEntity = (ITradingStationBlockEntity) pBlockEntity;
            if (!blockEntity.getTargetItemStack().isEmpty()) {
                pPoseStack.pushPose();
                pPoseStack.translate(.5, .5, .5);
                if(blockEntity.isWorking()){
                    float rot = ((blockEntity.getLevel().getGameTime() + pPartialTick) % 180F) * 2;
                    pPoseStack.mulPose(Axis.YP.rotationDegrees(rot));

                }
                pPoseStack.translate(0, .7d, 0);

                renderBlock(pPoseStack, pBufferSource, pPackedLight, pPackedOverlay, blockEntity.getTargetItemStack(),pBlockEntity);
                pPoseStack.popPose();
            }
        }

        if (Backend.canUseInstancing(pBlockEntity.getLevel()))
            return;

        VertexConsumer vb = pBufferSource.getBuffer(RenderType.solid());
        BlockState blockState = pBlockEntity.getBlockState();

        Direction facing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);

        SuperByteBuffer superBuffer = CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, blockState, facing.getOpposite());
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
