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
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

public class HypixelAPIManager implements HypixelPublicAPIModLibrary
{
    private AtomicBoolean subscribed = new AtomicBoolean(false);
    private AtomicBoolean firstRequestSent = new AtomicBoolean();

    private AtomicInteger requestCount = new AtomicInteger(0);
    private AtomicInteger requestReceived = new AtomicInteger(0);

    private AtomicReference<Consumer<Integer>> timeSink = new AtomicReference<>();
    private AtomicReference<Consumer<Integer>> requestSink = new AtomicReference<>();

    public Flux<Integer> requestLimitFlux = Flux.generate((SynchronousSink<Integer> sink) -> {
        Integer limit = Mono.create((MonoSink<Integer> monoSink) -> {
            requestSink.set(i -> {
                //System.out.println("Request limit in sub-mono: " + i);
                requestSink.set(null);
                monoSink.success(i);
            });
        }).block();
        sink.next(limit);
    }).subscribeOn(Schedulers.boundedElastic()).share();

    public Flux<Integer> timeFlux = Flux.generate((SynchronousSink<Integer> sink) -> {
        Integer time = Mono.create((MonoSink<Integer> monoSink) -> {
            timeSink.set(i -> {
                if (i == 0)
                {
                    firstRequestSent.set(false);
                    System.out.println("Error detected!");
                } else {
                    //System.out.println("Time left in sub-mono: " + i);
                    timeSink.set(null);
                    monoSink.success(i);
                }
            });
        }).block();
        //System.out.println("Waiting on timer waiting!");
        Mono.delay(Duration.ofSeconds(time + 2)).block();
        int received = requestReceived.getAndSet(0);
        //System.out.println("REceived requests: " + received);
        int requested = requestCount.updateAndGet(count -> count - received);
        //System.out.println("Pending request count: " + requested);
        firstRequestSent.set(false);
        requestLimitMono = this.requestLimitFlux.next().cache();
        this.subscribeToClock(this.timeFlux, this.requestLimitMono).subscribe();
        sink.next(received);
    }).subscribeOn(Schedulers.boundedElastic()).share();

    private Mono<Integer> requestLimitMono = requestLimitFlux.next().cache();

    @Override
    public <T extends AbstractReply> Mono<T> handleHypixelAPIRequest(Function<HypixelAPI, CompletableFuture<T>> requestFunc)
    {
        if (!HypixelPublicAPIMod.instance.apiKeySet) return Mono.error(new PublicAPIKeyMissingException());

        return Mono.just(0)
                .delayUntil(o -> getRequiredDelay(0, false, 0))
                .then(Mono.defer(() -> Mono.fromFuture(requestFunc.apply(HypixelPublicAPIMod.instance.getHypixelAPI()))
                        .doOnNext(reply -> {
                            Consumer<Integer> consumer = timeSink.get();
                            if (consumer != null)
                            {
                                consumer.accept(reply.getSecondsTillReset());
                            }
                            consumer = requestSink.get();
                            if (consumer != null)
                            {
                                consumer.accept(reply.getRequestAmountLeft());
                            }
                            requestReceived.incrementAndGet();
                            //System.out.println("Received request!");
                        })))
                .onErrorResume(throwable -> {
                    throwable.printStackTrace();
                    return Mono.empty();
                }).subscribeOn(Schedulers.parallel());
    }

    public Mono<Boolean> getRequiredDelay(int myCount, boolean delayed, int index)
    {
        return Mono.defer(() -> {
            //int count = myCount > 0 ? myCount : requestCount.incrementAndGet();
            if (!firstRequestSent.compareAndSet(false, true) || delayed)
            {
                //System.out.println("Waiting: " + index);
                return Mono.defer(() -> this.requestLimitMono)
                        .flatMap(limit -> Mono.defer(() -> Mono.just(myCount > 0 ? myCount : requestCount.getAndIncrement()))
                                .filter(count -> count > limit - 10)
                                .flatMap(count -> timeFlux.next()
                                        .delayUntil(received -> getRequiredDelay(Math.max(1, count - received), received < limit - 9, index)).thenReturn(true)
                                )
                        ).switchIfEmpty(Mono.just(false).subscribeOn(Schedulers.immediate()));
            }
            //System.out.println("Passing on!");
            this.subscribeToClock(timeFlux, this.requestLimitMono).subscribe();
            return Mono.just(this.requestCount.getAndIncrement()).map(o -> false).subscribeOn(Schedulers.immediate());
        });
    }

    public Mono<Integer> subscribeToClock(Flux<Integer> clock, Mono<Integer> requestClock)
    {
        return Mono.defer(() -> {
            if (subscribed.compareAndSet(false, true))
            {
                //System.out.println("Subscribed to clock!");
                clock.next()
                        .doOnNext(o -> {
                            //System.out.println("Permanent clock woke up!");
                            subscribed.set(false);
                        }).subscribeOn(Schedulers.parallel())
                        .subscribe();
                requestClock.subscribe();
            }
            return Mono.just(0);
        });
    }
}
