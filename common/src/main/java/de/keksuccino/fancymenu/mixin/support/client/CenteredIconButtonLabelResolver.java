package de.keksuccino.fancymenu.mixin.support.client;

import net.minecraft.network.chat.Component;

import javax.annotation.Nullable;

public final class CenteredIconButtonLabelResolver {

    private CenteredIconButtonLabelResolver() {
    }

    @Nullable
    public static Component selectCustomLabel(@Nullable Component customLabel, @Nullable Component hoverLabel, boolean hoveredOrFocused, boolean visible, boolean active) {
        if (hoverLabel != null && hoveredOrFocused && visible && active) return hoverLabel;
        return customLabel;
    }

    public static Component resolveRenderedLabel(Component customLabel, Component activeLabel, boolean active) {
        // MC 1.21.10 has no AbstractWidget.WithInactiveMessage. Inactive widgets are greyed out by their
        // render colour instead of by substituting the message, so the label is returned unchanged.
        return active ? activeLabel : customLabel;
    }

}
