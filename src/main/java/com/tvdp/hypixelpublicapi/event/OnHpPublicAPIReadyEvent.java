package com.tvdp.hypixelpublicapi.event;

import com.tvdp.hypixelpublicapi.HypixelPublicAPIModLibrary;
import net.minecraftforge.fml.common.eventhandler.Event;

public class OnHpPublicAPIReadyEvent extends Event
{
    public final HypixelPublicAPIModLibrary apiLibrary;

    public OnHpPublicAPIReadyEvent(HypixelPublicAPIModLibrary apiLibrary)
    {
        this.apiLibrary = apiLibrary;
    }
}
