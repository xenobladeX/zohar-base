/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xenoblade.zohar.base.gateway;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sun.jvm.hotspot.runtime.Threads;
import sun.jvm.hotspot.utilities.Interval;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * GatewayApplicationTests
 * @author xenoblade
 * @since 1.0.0
 */
@RunWith(SpringRunner.class) @SpringBootTest public class GatewayApplicationTests {

    @Test public void initFluxTest() {

        Flux.just("Hello", "World").subscribe(System.out::println);
        Flux.fromArray(new Integer[] { 1, 2, 3 }).subscribe(System.out::println);
        Flux.empty().subscribe(System.out::println);
        Flux.range(1, 10).subscribe(System.out::println);
        Flux.interval(Duration.of(10, ChronoUnit.SECONDS)).subscribe(System.out::println);
        try {
            Thread.sleep(100000);
        } catch (InterruptedException ex) {

        }
    }

    @Test public void FluxGenerateTest() {

        Flux.generate(sink -> {
            sink.next("hello");
            sink.complete();
        }).subscribe(System.out::println);

        final Random random = new Random();
        Flux.generate(ArrayList::new, (list, sink) -> {
            int value = random.nextInt(100);
            list.add(value);
            sink.next(value);
            if (list.size() == 10) {
                sink.complete();
            }
            return list;
        }).subscribe(System.out::println);

    }

    @Test public void FluxCreateTest() {
        Flux.create(sink -> {
            for (int i = 0; i < 10; i++) {
                sink.next(i);
            }
            sink.complete();
        }).subscribe(System.out::println);
    }

    @Test public void initMonoTest() {

        Mono.fromSupplier(() -> "Hello").subscribe(System.out::println);
        Mono.justOrEmpty(Optional.of("Hello")).subscribe(System.out::println);
        Mono.create(sink -> sink.success("Hello")).subscribe(System.out::println);
    }

    @Test public void bufferTest() {

        Flux.range(1, 100).buffer(20).subscribe(System.out::println);
        Flux.interval(Duration.of(1, ChronoUnit.SECONDS)).buffer(Duration.of(2, ChronoUnit.SECONDS))
                .take(10).toStream().forEach(System.out::println);
        Flux.range(1, 10).bufferUntil(i -> i % 2 == 0).subscribe(System.out::println);
        Flux.range(1, 10).bufferWhile(i -> i % 2 == 0).subscribe(System.out::println);

    }

    @Test public void windowTest() {

        Flux.range(1, 100).window(20).subscribe(System.out::println);
        Flux.interval(Duration.of(1, ChronoUnit.SECONDS)).window(Duration.of(2, ChronoUnit.SECONDS))
                .take(2).toStream().forEach(System.out::println);
    }

    @Test public void zipTest() {
        Flux.just("a", "b").zipWith(Flux.just("c", "d")).subscribe(System.out::println);
        Flux.just("a", "b").zipWith(Flux.just("c", "d"), (s1, s2) -> String.format("%s-%s", s1, s2))
                .subscribe(System.out::println);
    }

    @Test public void takeTest() {
        Flux.range(1, 1000).take(10).subscribe(System.out::println);
        Flux.range(1, 1000).takeLast(10).subscribe(System.out::println);
        Flux.range(1, 1000).takeWhile(i -> i < 10).subscribe(System.out::println);
        Flux.range(1, 1000).takeUntil(i -> i == 10).subscribe(System.out::println);

    }

    @Test public void reduceTest() {
        Flux.range(1, 100).reduce((x, y) -> x + y).subscribe(System.out::println);
        Flux.range(1, 100).reduceWith(() -> 100, (x, y) -> x + y).subscribe(System.out::println);

    }

    @Test public void mergeTest() {

        Flux.merge(Flux.interval(Duration.of(0, ChronoUnit.SECONDS),
                Duration.of(1, ChronoUnit.SECONDS)).take(5),
                Flux.interval(Duration.of(500, ChronoUnit.MILLIS),
                        Duration.of(1, ChronoUnit.SECONDS)).take(5)).toStream()
                .forEach(System.out::println);
        Flux.mergeSequential(Flux.interval(Duration.of(0, ChronoUnit.SECONDS),
                Duration.of(1, ChronoUnit.SECONDS)).take(5),
                Flux.interval(Duration.of(500, ChronoUnit.MILLIS),
                        Duration.of(1, ChronoUnit.SECONDS)).take(5)).toStream()
                .forEach(System.out::println);

    }

    @Test
    public void flatTest() {

        Flux.just(5, 10).flatMap(x -> {
            return Flux.interval(Duration.ofMillis(x*10), Duration.ofMillis(100)).take(x);
        }).toStream().forEach(System.out::println);

    }

    @Test
    public void concatTest() {
        Flux.just(5, 10)
                .concatMap(x -> Flux.interval(Duration.ofMillis(x*10), Duration.ofMillis(100)).take(x))
                .toStream()
                .forEach(System.out::println);
    }

    @Test
    public void combineLatestTest() {
        Flux.combineLatest(
                Arrays::toString,
                Flux.interval(Duration.ofMillis(100)).take(5),
                Flux.interval(Duration.ofMillis(50), Duration.ofMillis(100)).take(5)
        ).toStream().forEach(System.out::println);
    }

    @Test
    public void subscribeTest() {
        Flux.just(1, 2)
                .concatWith(Mono.error(new IllegalStateException()))
                .subscribe(System.out::println, System.err::println);
    }

}
