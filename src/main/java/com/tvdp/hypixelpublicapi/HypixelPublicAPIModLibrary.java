package com.tvdp.hypixelpublicapi;

import com.tvdp.hypixelpublicapi.error.PublicAPIKeyMissingException;
import net.hypixel.api.HypixelAPI;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface HypixelPublicAPIModLibrary
{
    <T> CompletableFuture<T> handleHypixelAPIRequest(Function<HypixelAPI, T> requestFunc) throws PublicAPIKeyMissingException;
}
