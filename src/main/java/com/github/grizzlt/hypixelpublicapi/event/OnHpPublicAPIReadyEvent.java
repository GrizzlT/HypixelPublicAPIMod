package com.github.grizzlt.hypixelpublicapi.event;

import com.github.grizzlt.hypixelpublicapi.HypixelPublicAPIModApi;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event will be triggered by the mod that holds the api
 */
public class OnHpPublicAPIReadyEvent extends Event
{
    private final HypixelPublicAPIModApi hypixelApiMod;

    /**
     * @param hypixelApiMod Instance of the api manager
     */
    public OnHpPublicAPIReadyEvent(HypixelPublicAPIModApi hypixelApiMod)
    {
        this.hypixelApiMod = hypixelApiMod;
    }

    public HypixelPublicAPIModApi getApiManager()
    {
        return this.hypixelApiMod;
    }
}
