package com.github.grizzlt.hypixelapimod;

import com.github.grizzlt.hypixelapimod.api.HypixelPublicAPIModApi;
import com.github.grizzlt.hypixelapimod.api.error.PublicAPIKeyMissingException;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.reply.AbstractReply;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class HypixelAPIManager implements HypixelPublicAPIModApi
{

    @Override
    public <T extends AbstractReply> CompletableFuture<T> handleHypixelAPIRequest(Function<HypixelAPI, CompletableFuture<T>> apiRequestFunc)
    {
        if (!HypixelPublicAPIMod.instance.apiKeySet) {
            CompletableFuture<T> future = new CompletableFuture<>();
            future.completeExceptionally(new PublicAPIKeyMissingException());
            return future;
        }

        return apiRequestFunc.apply(HypixelPublicAPIMod.instance.getHypixelAPI());
    }
}
