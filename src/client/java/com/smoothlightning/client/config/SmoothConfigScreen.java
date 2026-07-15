package com.smoothlightning.client.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

public class SmoothConfigScreen extends OptionsSubScreen {

    public SmoothConfigScreen(Screen lastScreen) {
        super(lastScreen, Minecraft.getInstance().options, Component.translatable("smooth-lighted-entities.options.title"));
    }

    @Override
    protected void addOptions() {
        this.list.addHeader(Component.translatable("smooth-lighted-entities.options.header.default"));
        this.list.addBig(ModConfig.GRADIENT_RANGE);
        this.list.addBig(ModConfig.SOLID_DARK_HEIGHT); // NUEVO SLIDER
        this.list.addBig(ModConfig.BASE_DARKNESS);
    }

    @Override
    protected void addTitle() {
        LinearLayout header = LinearLayout.vertical().spacing(2);
        header.defaultCellSetting().alignHorizontallyCenter().alignVerticallyMiddle();
        header.addChild(new StringWidget(this.title, this.font));
        this.layout.addToHeader(header);
    }

    @Override
    public void onClose() {
        ModConfig.save();
        super.onClose();
    }

    @Override
    public void removed() {
        ModConfig.save();
        super.removed();
    }
}