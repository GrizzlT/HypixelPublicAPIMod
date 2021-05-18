package com.github.grizzlt.hypixelapimod;

import com.github.grizzlt.hypixelpublicapi.HypixelPublicAPIModApi;
import com.github.grizzlt.hypixelpublicapi.error.PublicAPIKeyMissingException;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.reply.AbstractReply;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public class HypixelAPIManager implements HypixelPublicAPIModApi
{
    @Override
    public <T extends AbstractReply> Mono<T> handleHypixelAPIRequest(Function<HypixelAPI, Mono<T>> function)
    {
        if (!HypixelPublicAPIMod.instance.apiKeySet) return Mono.error(new PublicAPIKeyMissingException());

        return function.apply(HypixelPublicAPIMod.instance.getHypixelAPI());
    }
}
