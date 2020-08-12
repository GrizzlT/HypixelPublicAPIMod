package com.tvdp.hypixelapimod;

import com.tvdp.hypixelpublicapi.HypixelPublicAPIModLibrary;
import com.tvdp.hypixelpublicapi.error.PublicAPIKeyMissingException;
import net.hypixel.api.HypixelAPI;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class HypixelAPIManager implements HypixelPublicAPIModLibrary
{
    private AtomicInteger requestCount = new AtomicInteger(0);
    private ExecutorService executorService = Executors.newCachedThreadPool();

    private int ticks;
    private final Object blockingObject = new Object();

    @Override
    public <T> void handleHypixelAPIRequest(Function<HypixelAPI, T> requestFunc) throws PublicAPIKeyMissingException
    {
        if (!HypixelPublicAPIMod.instance.apiKeySet) throw new PublicAPIKeyMissingException();

        executorService.submit(() -> {
            synchronized (blockingObject) {
                while (requestCount.get() >= 110) {
                    try {
                        blockingObject.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        System.out.println("Something broke our loop");
                    }
                }

                //else
                requestCount.getAndIncrement();
                return requestFunc.apply(HypixelPublicAPIMod.instance.getHypixelAPI());
            }
        });
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase != TickEvent.Phase.START) return;

        synchronized (blockingObject) {
            if (++ticks >= 1240) {
                ticks = 0;
                requestCount.set(0);
                blockingObject.notifyAll();
            }
        }
    }
}
