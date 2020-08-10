package com.tvdp.hypixelpublicapi.event;

import com.tvdp.hypixelpublicapi.HypixelAPIReceiver;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event must be triggered by the mod that will use the api manager
 */
public class OnHpPublicAPIReadyEvent extends Event
{
    public final HypixelAPIReceiver receiver;

    /**
     * @param receiver the class that will manage storage of the api manager
     */
    public OnHpPublicAPIReadyEvent(HypixelAPIReceiver receiver)
    {
        this.receiver = receiver;
    }
}
