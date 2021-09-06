package com.github.grizzlt.hypixelapimod.api.event;

import com.github.grizzlt.hypixelapimod.api.HypixelPublicAPIModApi;

import java.util.ArrayList;
import java.util.List;

public class HypixelAPIReadyEvent
{
    public static final HypixelAPIReadyEvent API_READY_EVENT = new HypixelAPIReadyEvent();

    private final List<HypixelAPIReceiver> listeners = new ArrayList<>();

    private HypixelAPIReadyEvent()
    {
    }

    public void subscribeToEvent(HypixelAPIReceiver receiver)
    {
        this.listeners.add(receiver);
    }

    public void fireEvent(HypixelPublicAPIModApi api)
    {
        for (HypixelAPIReceiver receiver : this.listeners) {
            receiver.onReceiveAPI(api);
        }
    }

    @FunctionalInterface
    public interface HypixelAPIReceiver
    {
        void onReceiveAPI(HypixelPublicAPIModApi apiWrapper);
    }
}
