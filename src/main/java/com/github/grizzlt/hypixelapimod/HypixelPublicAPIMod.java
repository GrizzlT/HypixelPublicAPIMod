package com.github.grizzlt.hypixelapimod;

import com.github.grizzlt.hypixelapimod.api.event.HypixelAPIReadyEvent;
import com.github.grizzlt.hypixelapimod.commands.*;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.reactor.ReactorHttpClient;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, guiFactory = Reference.GUI_FACTORY)
public class HypixelPublicAPIMod
{
    public boolean apiKeySet = false;
    public UUID apiKey;
    private HypixelAPI hypixelAPI;

    private final HypixelAPIManager apiManager = new HypixelAPIManager();

    @Mod.Instance
    public static HypixelPublicAPIMod instance = null;

    public static File configFile;
    public static Configuration config;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //config
        configFile = new File(Loader.instance().getConfigDir(), "HypixelApiMod.cfg");
        config = new Configuration(configFile);
        config.load();
        System.out.println(configFile.getAbsolutePath());

        if (config.get(Configuration.CATEGORY_GENERAL, "loadAuto", true).getBoolean()) {
            try {
                this.setApiKey(UUID.fromString(config.get(Configuration.CATEGORY_GENERAL, "apiKey", "").getString()));

                System.out.println("Automatically loaded stored api key!");
            } catch (IllegalArgumentException e) {
                //e.printStackTrace();
                System.out.println("Error: invalid key! (might have been empty)");
            }
        }

        if (config.hasChanged()) {
            config.save();
        }

        //commands
        try {
            ClientCommandHandler.instance.registerCommand(new SaveApiKeyCommand());
            ClientCommandHandler.instance.registerCommand(new LoadApiKeyCommand());
            ClientCommandHandler.instance.registerCommand(new UnSetApiKeyCommand());
            ClientCommandHandler.instance.registerCommand(new SetApiKeyCommand());
            //quickstart
            ClientCommandHandler.instance.registerCommand(new QuickStartCommand());

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(apiManager);
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event)
    {
        HypixelAPIReadyEvent.API_READY_EVENT.fireEvent(this.apiManager);
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event)
    {
        Pattern pattern = Pattern.compile("^Your new API key is (?<key>.*?)$");
        Matcher m = pattern.matcher(event.message.getUnformattedText());
        if (m.find())
        {
            this.setApiKey(UUID.fromString(m.group("key")));
            try {
                HypixelPublicAPIMod.config.get(Configuration.CATEGORY_GENERAL, "apiKey", "").set(this.apiKey.toString());
                HypixelPublicAPIMod.config.save();
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "Successful quickstart!"));
            } catch (Exception e) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Unexpected error: APIkey could not be stored!"));
            }
            event.setCanceled(true);
        }
    }

    public HypixelAPI getHypixelAPI() {
        return hypixelAPI;
    }

    public void setApiKey(UUID key)
    {
        this.apiKey = key;

        if (this.apiKey == null) {
            this.apiKeySet = false;
            return;
        }

        try {
            this.hypixelAPI = new HypixelAPI(new ReactorHttpClient(this.apiKey));
            this.apiKeySet = true;
        } catch (IllegalArgumentException e) {
            System.out.println("Error: Invalid key! (might have been null)");
            this.apiKeySet = false;
        }
    }
}
