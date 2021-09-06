package com.github.grizzlt.hypixelapimod.api;

import com.github.grizzlt.hypixelapimod.api.error.PublicAPIKeyMissingException;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.reply.AbstractReply;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface HypixelPublicAPIModApi
{
    /**
     * Sends a request and returns a {@link CompletableFuture} containing the response
     * @param apiRequestFunc uses {@link HypixelAPI} to fetch a request
     * @return {@link CompletableFuture} containing the response or a potential {@link PublicAPIKeyMissingException}
     */
    <T extends AbstractReply> CompletableFuture<T> handleHypixelAPIRequest(Function<HypixelAPI, CompletableFuture<T>> apiRequestFunc);
}
