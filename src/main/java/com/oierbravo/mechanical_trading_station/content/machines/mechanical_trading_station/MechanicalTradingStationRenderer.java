package com.oierbravo.mechanical_trading_station.content.machines.mechanical_trading_station;

import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class MechanicalTradingStationRenderer  extends KineticBlockEntityRenderer<MechanicalTradingStationBlockEntity> {
    public MechanicalTradingStationRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(MechanicalTradingStationBlockEntity pBlockEntity, float partialTicks, PoseStack pPoseStack, MultiBufferSource pBufferSource, int light, int overlay) {

        if(pBlockEntity instanceof MechanicalTradingStationBlockEntity) {
            MechanicalTradingStationBlockEntity blockEntity = pBlockEntity;
            if (!blockEntity.getTargetItemStack().isEmpty()) {
                pPoseStack.pushPose();
                pPoseStack.translate(0.5d, 1.1d, 0.5d);
                pPoseStack.mulPose(new Quaternion(Vector3f.YN, blockEntity.getProgressPercent() * 360, true));
                renderBlock(pPoseStack, pBufferSource, light, overlay, blockEntity.getTargetItemStack());
                pPoseStack.popPose();
            }
        }

        if (Backend.canUseInstancing(pBlockEntity.getLevel()))
            return;
        VertexConsumer vb = pBufferSource.getBuffer(RenderType.solid());
        BlockState blockState = pBlockEntity.getBlockState();

        SuperByteBuffer superBuffer = CachedBufferer.partial(AllPartialModels.SHAFT_HALF, blockState);
        standardKineticRotationTransform(superBuffer, pBlockEntity, light).renderInto(pPoseStack, vb);
    }

    protected void renderBlock(PoseStack ms, MultiBufferSource buffer, int light, int overlay, ItemStack stack) {
        BakedModel model =  Minecraft.getInstance().getItemRenderer().getModel(stack,null, Minecraft.getInstance().player, 0);
        Minecraft.getInstance()
                .getItemRenderer()
                .render(stack, ItemTransforms.TransformType.GROUND, false, ms, buffer, light, overlay, model);

    }
}
