package com.github.grizzlt.hypixelapimod;

import com.github.grizzlt.hypixelpublicapi.HypixelPublicAPIModLibrary;
import com.github.grizzlt.hypixelpublicapi.error.PublicAPIKeyMissingException;
import com.github.grizzlt.shadowedLibs.net.hypixel.api.HypixelAPI;
import com.github.grizzlt.shadowedLibs.net.hypixel.api.reply.AbstractReply;
import com.github.grizzlt.shadowedLibs.reactor.core.publisher.Flux;
import com.github.grizzlt.shadowedLibs.reactor.core.publisher.Mono;
import com.github.grizzlt.shadowedLibs.reactor.core.publisher.MonoSink;
import com.github.grizzlt.shadowedLibs.reactor.core.publisher.SynchronousSink;
import com.github.grizzlt.shadowedLibs.reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

public class HypixelAPIManager implements HypixelPublicAPIModLibrary
{
    private AtomicInteger requestCount = new AtomicInteger();
    private AtomicInteger requestsReceived = new AtomicInteger();
    private AtomicBoolean firstTimeRequestingLimit = new AtomicBoolean();
    private AtomicBoolean resetRequestLimit = new AtomicBoolean();
    private AtomicBoolean subscribed = new AtomicBoolean(false);

    private Consumer<Integer> timeSink = null;
    private Consumer<Integer> requestSink = null;

    private Mono<Integer> timeMono = Mono.defer(() -> Mono.create((MonoSink<Integer> sink) -> {
        timeSink = time -> {
            if (time > 0) {
                //System.out.println("Time to wait in timeMono: " + time);
                timeSink = null;
                sink.success(time);
            }
        };
    })).subscribeOn(Schedulers.boundedElastic());

    private Mono<Integer> requestMono = Mono.defer(() -> Mono.create((MonoSink<Integer> sink) -> {
        requestSink = limit -> {
            boolean firstTimePassed = firstTimeRequestingLimit.compareAndSet(false, true);
            if (firstTimePassed || limit >= 116) {
                //System.out.println("Request limit in requestMono: " + limit);
                requestSink = null;
                sink.success(limit);
            }
        };
    })).subscribeOn(Schedulers.boundedElastic());

    private Flux<Integer> timeFlux = Flux.generate((SynchronousSink<Integer> sink) -> {
        int waitTime = timeMono.block();
        //System.out.println("Time to wait in timeFlux: " + waitTime);
        Mono.delay(Duration.ofSeconds(waitTime + 1)).block();
        //System.out.println("Normal clock ran out!");
        int receivedCount = requestsReceived.getAndSet(0);
        resetRequestLimit.set(false);
        //System.out.println("Requests: " + requestCount.updateAndGet(requests -> Math.max(0, requests - receivedCount)));
        sink.next(receivedCount);
    }).subscribeOn(Schedulers.boundedElastic()).share();

    private Flux<Integer> requestFlux = Flux.generate((SynchronousSink<Integer> sink) -> {
        int requestLimit = requestMono.block();
        //System.out.println("Request limit in RequestFlux: " + requestLimit);
        sink.next(requestLimit);
    }).subscribeOn(Schedulers.boundedElastic()).share();

    @Override
    public <T extends AbstractReply> Mono<T> handleHypixelAPIRequest(Function<HypixelAPI, CompletableFuture<T>> requestFunc)
    {
        if (!HypixelPublicAPIMod.instance.apiKeySet) return Mono.error(new PublicAPIKeyMissingException());

        return Mono.just(0).delayUntil(o -> getRequiredDelay(0, false))
                .then(Mono.defer(() -> Mono.fromFuture(requestFunc.apply(HypixelPublicAPIMod.instance.getHypixelAPI()))
                        .doOnNext(reply -> {
                            if (timeSink != null)
                            {
                                timeSink.accept(reply.getSecondsTillReset());
                            }
                            if (requestSink != null)
                            {
                                requestSink.accept(reply.getRequestAmountLeft());
                            }
                            requestsReceived.incrementAndGet();
                        })
                )).onErrorResume(throwable -> {
                    throwable.printStackTrace();
                    return Mono.empty();
                }).subscribeOn(Schedulers.parallel());
    }

    public Mono<Boolean> getRequiredDelay(int myCount, boolean delayed)
    {
        return Mono.defer(() -> {
            subscribeToClock(timeFlux);
            int count = myCount > 0 ? myCount : requestCount.incrementAndGet();
            if (!resetRequestLimit.compareAndSet(false, true) || delayed)
            {
                return requestFlux.next()
                        .filter(limit -> count > limit - 9)
                        .flatMap(limit -> timeFlux.next()
                                .delayUntil(received -> getRequiredDelay(Math.max(1, count - received), received < limit - 9)).thenReturn(true)
                        ).switchIfEmpty(Mono.just(false).subscribeOn(Schedulers.immediate()));
            }
            return Mono.just(false).subscribeOn(Schedulers.immediate());
        });
    }

    public void subscribeToClock(Flux<Integer> clock)
    {
        if (subscribed.compareAndSet(false, true))
        {
            //System.out.println("Subscribed to clock!");
            clock.next()
                    .doOnNext(o -> {
                        //System.out.println("Permanent clock woke up!");
                        subscribed.set(false);
                    })
                    .subscribeOn(Schedulers.parallel())
                    .subscribe();
        }
    }
}
