package com.tvdp.hypixelpublicapi;

import net.hypixel.api.HypixelAPI;

import java.util.function.Function;

public interface HypixelPublicAPIModLibrary
{
    <T> void handleHypixelAPIRequest(Function<HypixelAPI, T> requestFunc);
}
