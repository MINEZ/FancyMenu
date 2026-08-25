package de.keksuccino.fancymenu.mixin.mixins.common.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import de.keksuccino.fancymenu.mixin.support.client.CenteredIconButtonLabelResolver;
import de.keksuccino.fancymenu.util.rendering.ui.widget.CustomizableWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SpriteIconButton.CenteredIcon.class)
public abstract class MixinSpriteIconButton_CenteredIcon extends Button {

    // Dummy constructor
    private MixinSpriteIconButton_CenteredIcon() {
        super(0, 0, 0, 0, Component.empty(), button -> {}, DEFAULT_NARRATION);
    }

    /**
     * @reason Centered icon buttons never render their message, so explicit FancyMenu labels must replace the icon to behave like labels on other vanilla buttons.
     */
    @WrapOperation(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIIIF)V"))
    private void wrap_blitSprite_in_renderWidget_FancyMenu(GuiGraphics graphics, RenderPipeline pipeline, ResourceLocation sprite, int x, int y, int width, int height, float alpha, Operation<Void> original) {
        CustomizableWidget widget = (CustomizableWidget)this;
        Component customLabel = CenteredIconButtonLabelResolver.selectCustomLabel(widget.getCustomLabelFancyMenu(), widget.getHoverLabelFancyMenu(), this.isHoveredOrFocused(), this.visible, this.active);
        if (customLabel != null) {
            Component activeLabel = this.active ? this.getMessage() : customLabel;
            Component renderedLabel = CenteredIconButtonLabelResolver.resolveRenderedLabel(customLabel, activeLabel, this.active);
            // MC 1.21.10 has no ActiveTextCollector / textRendererForWidget, so the label goes through the
            // instance renderScrollingString overload, matching the author's implementation on the 1.21.1 branch.
            int labelColor = ARGB.color(Mth.ceil(this.alpha * 255.0F), this.active ? 0xFFFFFF : 0xA0A0A0);
            this.renderScrollingString(graphics, Minecraft.getInstance().font, 2, labelColor);
            return;
        }

        // Fix for making the icon of icon buttons react to alpha changes.
        // MC 1.21.10 draws the icon through GuiGraphics#blitSprite directly, and that overload already
        // takes an alpha, so the widget alpha is folded into it instead of going through the shader colour.
        original.call(graphics, pipeline, sprite, x, y, width, height, alpha * this.alpha);
    }

}
