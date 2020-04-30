package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {

    @Test
    public void fluxTest() {
        Flux<String> flux = Flux.just("Spring", "Spring boot")
                .log();

        flux.subscribe(System.out::println,
                (e) -> System.err.println(e),
                () -> System.out.println("Completed"));
    }

    @Test
    public void fluxTestWithoutError() {
        Flux<String> flux = Flux.just("Spring", "Spring boot")
                .log();

        // StepVerifier subscribe and assert the flux
        // verify call in the end is mandatory
        StepVerifier.create(flux)
                .expectNext("Spring")
                .expectNext("Spring boot")
                .verifyComplete();
    }

    @Test
    public void fluxTestWithError() {
        Flux<String> flux = Flux.just("Spring", "Spring boot")
                .concatWith(Flux.error(new RuntimeException("Exception")))
                .log();

        // StepVerifier subscribe and assert the flux
        StepVerifier.create(flux)
                .expectNext("Spring")
                .expectNext("Spring boot")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void fluxTestCountWithError() {
        Flux<String> flux = Flux.just("Spring", "Spring boot")
                .concatWith(Flux.error(new RuntimeException("Exception")))
                .log();

        // StepVerifier subscribe and assert the flux
        StepVerifier.create(flux)
                .expectNextCount(2)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void monoTest() {
        Mono<String> mono = Mono.just("Spring");
        StepVerifier.create(mono)
                .expectNext("Spring")
                .verifyComplete();
    }

    @Test
    public void monoTestError() {
        Mono<String> mono = Mono.error(new RuntimeException("Exception"));
        StepVerifier.create(mono.log())
                .expectError(RuntimeException.class)
                .verify();
    }
}
