package com.tvdp.hypixelapimod;

import com.tvdp.hypixelapimod.commands.LoadApiKeyCommand;
import com.tvdp.hypixelapimod.commands.SaveApiKeyCommand;
import com.tvdp.hypixelapimod.commands.SetApiKeyCommand;
import com.tvdp.hypixelapimod.commands.UnSetApiKeyCommand;
import com.tvdp.hypixelpublicapi.event.OnHpPublicAPIReadyEvent;
import net.hypixel.api.HypixelAPI;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.UUID;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, guiFactory = Reference.GUI_FACTORY)
public class HypixelPublicAPIMod
{
    public boolean apiKeySet = false;
    public UUID apiKey;
    private HypixelAPI hypixelAPI;

    private HypixelAPIManager apiManager = new HypixelAPIManager();

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
        Property tempKeyProp = config.get(Configuration.CATEGORY_GENERAL, "apiKey", "");
        Property tempLoadAuto = config.get(Configuration.CATEGORY_GENERAL, "loadAuto", true);
        if (config.hasChanged()) {
            config.save();
        }

        if (config.get(Configuration.CATEGORY_GENERAL, "loadAuto", true).getBoolean()) {
            try {
                this.apiKey = UUID.fromString(config.get(Configuration.CATEGORY_GENERAL, "apiKey", "").getString());
                this.hypixelAPI = new HypixelAPI(this.apiKey);

                System.out.println("Automatically loaded stored api key!");
            } catch (IllegalArgumentException e) {
                //e.printStackTrace();
                System.out.println("Error: invalid key! (might have been empty)");
            }
        }

        //commands
        try {
            ClientCommandHandler.instance.registerCommand(new SaveApiKeyCommand());
            ClientCommandHandler.instance.registerCommand(new LoadApiKeyCommand());
            ClientCommandHandler.instance.registerCommand(new UnSetApiKeyCommand());
            ClientCommandHandler.instance.registerCommand(new SetApiKeyCommand());

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(apiManager);
    }

    @SubscribeEvent
    public void onHypixelAPIEvent(OnHpPublicAPIReadyEvent event)
    {
        event.receiver.onReceivePublicAPIManager(this.apiManager);
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
            this.hypixelAPI = new HypixelAPI(this.apiKey);
            this.apiKeySet = true;
        } catch (IllegalArgumentException e) {
            System.out.println("Error: Invalid key! (might have been null)");
            this.apiKeySet = false;
        }
    }
}
