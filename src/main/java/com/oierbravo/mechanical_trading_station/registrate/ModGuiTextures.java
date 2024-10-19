package com.oierbravo.mechanical_trading_station.registrate;

import com.mojang.blaze3d.systems.RenderSystem;
import com.oierbravo.mechanical_trading_station.MechanicalTradingStation;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.gui.UIRenderHelper;
import com.simibubi.create.foundation.gui.element.ScreenElement;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum ModGuiTextures implements ScreenElement  {
    MECHANICAL_TRADING_STATION("mechanical_trading_station", 0, 0, 175, 68);


    public static final int FONT_COLOR = 0x575F7A;

    public final ResourceLocation location;
    public int width, height;
    public int startX, startY;


    private ModGuiTextures(String location, int width, int height) {
        this(location, 0, 0, width, height);
    }

    private ModGuiTextures(int startX, int startY) {
        this("icons", startX * 16, startY * 16, 16, 16);
    }

    private ModGuiTextures(String location, int startX, int startY, int width, int height) {
        this(MechanicalTradingStation.MODID, location, startX, startY, width, height);
    }

    private ModGuiTextures(String namespace, String location, int startX, int startY, int width, int height) {
        this.location = new ResourceLocation(namespace, "textures/gui/" + location + ".png");
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
    }
    @OnlyIn(Dist.CLIENT)
    public void bind() {
        RenderSystem.setShaderTexture(0, location);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y) {
        graphics.blit(location, x, y, startX, startY, width, height);
    }

    @OnlyIn(Dist.CLIENT)
    public void render(GuiGraphics graphics, int x, int y, Color c) {
        bind();
        UIRenderHelper.drawColoredTexture(graphics, c, x, y, startX, startY, width, height);
    }
}
