package com.smoothlightning.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.smoothlightning.client.config.ModConfig;
import com.smoothlightning.client.render.GradientData;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Model.class)
public class ModelMixin {

    @Inject(method = "setupAnim", at = @At("HEAD"))
    private void onSetupAnim(Object state, CallbackInfo ci) {
        if (state instanceof LivingEntityRenderState livingState && (Object)this instanceof EntityModel) {
            Camera camera = Minecraft.getInstance().gameRenderer.mainCamera();
            if (camera != null) {
                double camY = camera.position().y;

                float visualHeight = livingState.boundingBoxHeight * livingState.scale;
                float baseWorldY = (float)livingState.y;

                if (livingState.passengerOffset != null) {
                    baseWorldY += (float)livingState.passengerOffset.y;
                }

                baseWorldY -= visualHeight * 0.1f;
                float headWorldY = baseWorldY + (visualHeight * 1.1f);

                float feetCamY = (float)(baseWorldY - camY);
                float headCamY = (float)(headWorldY - camY);

                GradientData.setBaseCameraY(feetCamY);
                GradientData.setVisualHeight(headCamY - feetCamY);

                EntityType<?> type = livingState.entityType;
                Identifier entityId = EntityType.getKey(type);
                String entityIdStr = entityId != null ? entityId.toString() : "unknown";

                ModConfig.EntityOverride override = ModConfig.PER_ENTITY_OVERRIDES.get(entityIdStr);

                if (override != null) {
                    GradientData.setGradientRange((float) override.range);
                    GradientData.setBaseDarkness((float) override.darkness);
                    GradientData.setSolidDarkHeight((float) override.solidHeight);
                } else {
                    GradientData.setGradientRange(ModConfig.GRADIENT_RANGE.get().floatValue());
                    GradientData.setBaseDarkness(ModConfig.BASE_DARKNESS.get().floatValue());
                    GradientData.setSolidDarkHeight(ModConfig.SOLID_DARK_HEIGHT.get().floatValue());
                }
            }
        } else {
            GradientData.clearBaseCameraY();
            GradientData.clearVisualHeight();
            GradientData.clearGradientRange();
            GradientData.clearBaseDarkness();
            GradientData.clearSolidDarkHeight();
        }
    }

    @Inject(method = "renderToBuffer", at = @At("TAIL"))
    private void onRenderEnd(PoseStack poseStack, VertexConsumer buffer, int lightCoords, int overlayCoords, int color, CallbackInfo ci) {
        GradientData.clearBaseCameraY();
        GradientData.clearVisualHeight();
        GradientData.clearGradientRange();
        GradientData.clearBaseDarkness();
        GradientData.clearSolidDarkHeight();
    }
}