package com.github.grizzlt.hypixelapimod.config;

import com.github.grizzlt.hypixelapimod.Reference;
import com.google.common.collect.Lists;
import com.github.grizzlt.hypixelapimod.HypixelPublicAPIMod;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

public class GuiConfigHypixelPublicAPI extends GuiConfig
{
    public GuiConfigHypixelPublicAPI(GuiScreen parent)
    {
        super(parent,
                Lists.newArrayList(
                        new ConfigElement(HypixelPublicAPIMod.config.get(Configuration.CATEGORY_GENERAL, "loadAuto", true))
                ),
                Reference.MOD_ID,
                false,
                false,
                "Hypixel Ingame Stat Viewer configuration!");
        titleLine2 = HypixelPublicAPIMod.configFile.getAbsolutePath();
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
    }
}
