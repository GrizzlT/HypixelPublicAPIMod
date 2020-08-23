package com.github.ThomasVDP.hypixelpublicapi.event;

import com.github.ThomasVDP.hypixelpublicapi.HypixelPublicAPIModLibrary;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event will be triggered by the mod that will hold the api
 */
public class OnHpPublicAPIReadyEvent extends Event
{
    public final HypixelPublicAPIModLibrary publicAPILibrary;

    /**
     * @param publicAPILibrary the class that will the api
     */
    public OnHpPublicAPIReadyEvent(HypixelPublicAPIModLibrary publicAPILibrary)
    {
        this.publicAPILibrary = publicAPILibrary;
    }
}
