package com.github.grizzlt.hypixelpublicapi;

import net.hypixel.api.HypixelAPI;
import net.hypixel.api.reply.AbstractReply;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public interface HypixelPublicAPIModApi
{
    /**
     * Sends a request and returns a {@link Mono} containing the response
     * @param apiRequestFunc produces requestMono using {@link HypixelAPI}
     * @return {@link Mono} containing the response or a potential {@link com.github.grizzlt.hypixelpublicapi.error.PublicAPIKeyMissingException}
     */
    <T extends AbstractReply> Mono<T> handleHypixelAPIRequest(Function<HypixelAPI, Mono<T>> apiRequestFunc);
}
