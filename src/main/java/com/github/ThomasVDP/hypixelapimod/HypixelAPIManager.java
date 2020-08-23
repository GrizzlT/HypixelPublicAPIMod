package com.github.ThomasVDP.hypixelapimod;

import com.github.ThomasVDP.hypixelpublicapi.HypixelPublicAPIModLibrary;
import com.github.ThomasVDP.hypixelpublicapi.error.PublicAPIKeyMissingException;
import com.github.ThomasVDP.shadowedLibs.net.hypixel.api.HypixelAPI;
import com.github.ThomasVDP.shadowedLibs.net.hypixel.api.exceptions.APIThrottleException;
import com.github.ThomasVDP.shadowedLibs.net.hypixel.api.exceptions.HypixelAPIException;
import com.github.ThomasVDP.shadowedLibs.net.tascalate.concurrent.*;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.http.client.ClientProtocolException;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class HypixelAPIManager implements HypixelPublicAPIModLibrary
{
    private final AtomicInteger requestCount = new AtomicInteger(0);
    private final AtomicBoolean chatMessagePrinted = new AtomicBoolean(false);
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private int ticks;
    private final Object blockingObject = new Object();

    @Override
    public <T> Promise<T> handleHypixelAPIRequest(Function<HypixelAPI, CompletableFuture<T>> requestFunc) throws PublicAPIKeyMissingException
    {
        if (!HypixelPublicAPIMod.instance.apiKeySet) throw new PublicAPIKeyMissingException();

        /*executorService.submit(() -> {
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
        });*/

        RetryPolicy<? super T> retryPolicy = new RetryPolicy<>()
                .withoutTimeout()
                .withBackoff(DelayPolicy.fixedInterval(2000L).withFirstRetryNoDelay())
                .retryInfinitely()
                .retryOn(APIThrottleException.class, HypixelAPIException.class, RejectedExecutionException.class, IOException.class, ClientProtocolException.class)
                .abortOn(InterruptedException.class);

        return Promises.retryFuture(() -> DependentPromise.from(CompletableTask.asyncOn(this.executorService))
                .thenRunAsync(() -> {
                    synchronized (blockingObject) {
                        while (requestCount.get() >= 115) {
                            try {
                                if (!this.chatMessagePrinted.get()) {
                                    this.chatMessagePrinted.set(true);
                                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_GREEN + "You have reached your limit of 115 requests/min! Wait a few seconds to receive requests!"));
                                }
                                blockingObject.wait();
                            } catch (InterruptedException e) {
                                //we must have canceled this execution
                            }
                        }
                    }})
                .thenComposeAsync(aVoid -> {
                    requestCount.getAndIncrement();
                    return CompletableTask.waitFor(requestFunc.apply(HypixelPublicAPIMod.instance.getHypixelAPI()), this.executorService);
                }), retryPolicy);

        /*return DependentPromise
                .from(CompletableTask.asyncOn(this.executorService))
                .thenRunAsync(() -> {
                    synchronized (blockingObject) {
                        while (requestCount.get() >= 115) {
                            try {
                                if (!this.chatMessagePrinted.get()) {
                                    this.chatMessagePrinted.set(true);
                                    Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_GREEN + "You have reached your limit of 115 requests/min! Wait a few seconds to receive requests!"));
                                }
                                blockingObject.wait();
                            } catch (InterruptedException e) {
                                //we must have canceled this execution
                            }
                        }
                    }})
                .thenComposeAsync(aVoid -> {
                    requestCount.getAndIncrement();
                    return CompletableTask.waitFor(requestFunc.apply(HypixelPublicAPIMod.instance.getHypixelAPI()), this.executorService);
                });*/
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
