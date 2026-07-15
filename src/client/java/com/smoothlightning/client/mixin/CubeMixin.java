package com.smoothlightning.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.smoothlightning.client.render.GradientData;
import net.minecraft.client.model.geom.ModelPart;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ModelPart.Cube.class)
public class CubeMixin {

    @Shadow @Final private ModelPart.Polygon[] polygons;

    @Overwrite
    public void compile(final PoseStack.Pose pose, final VertexConsumer builder, final int lightCoords, final int overlayCoords, final int color) {
        Matrix4f matrix = pose.pose();
        Vector3f scratchVector = new Vector3f();
        Float baseCameraY = GradientData.getBaseCameraY();
        Float visualHeight = GradientData.getVisualHeight();
        Float activeRange = GradientData.getGradientRange();
        Float baseDark = GradientData.getBaseDarkness();
        Float solidDarkHeight = GradientData.getSolidDarkHeight(); // NUEVO

        for(ModelPart.Polygon polygon : this.polygons) {
            Vector3f normal = pose.transformNormal(polygon.normal(), scratchVector);
            float nx = normal.x();
            float ny = normal.y();
            float nz = normal.z();

            for(ModelPart.Vertex vertex : polygon.vertices()) {
                float x = vertex.worldX();
                float y = vertex.worldY();
                float z = vertex.worldZ();
                Vector3f pos = matrix.transformPosition(x, y, z, scratchVector);

                float gradientFactor = 1.0f;

                if (baseCameraY != null && visualHeight != null && activeRange != null && baseDark != null && solidDarkHeight != null) {
                    float relativeY = pos.y() - baseCameraY;

                    float maxGradientY = visualHeight * activeRange;
                    float solidY = visualHeight * solidDarkHeight;

                    if (relativeY <= solidY) {
                        gradientFactor = baseDark;
                    } else if (relativeY >= maxGradientY) {
                        gradientFactor = 1.0f;
                    } else {
                        if (maxGradientY <= solidY) {
                            gradientFactor = baseDark;
                        } else {
                            float progress = (relativeY - solidY) / (maxGradientY - solidY);
                            gradientFactor = baseDark + (1.0f - baseDark) * progress;
                        }
                    }
                }

                int a = (color >> 24) & 0xFF;
                int r = (int)(((color >> 16) & 0xFF) * gradientFactor);
                int g = (int)(((color >> 8) & 0xFF) * gradientFactor);
                int b = (int)((color & 0xFF) * gradientFactor);

                int newColor = (a << 24) | (r << 16) | (g << 8) | b;

                builder.addVertex(pos.x(), pos.y(), pos.z(), newColor, vertex.u(), vertex.v(), overlayCoords, lightCoords, nx, ny, nz);
            }
        }
    }
}