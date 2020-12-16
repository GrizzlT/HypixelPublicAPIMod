package com.github.grizzlt.hypixelpublicapi;

import com.github.grizzlt.hypixelpublicapi.error.PublicAPIKeyMissingException;
import net.hypixel.api.HypixelAPI;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public interface HypixelPublicAPIModLibrary
{
    <T> Mono<T> handleHypixelAPIRequest(Function<HypixelAPI, CompletableFuture<T>> apiRequestFunc) throws PublicAPIKeyMissingException;
}
