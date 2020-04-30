package com.learnreactivespring.repository;

import com.learnreactivespring.document.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
public class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;

    List<Item> itemList = Arrays.asList(
            new Item(null, "Samsung TV", 400),
            new Item(null, "LG TV", 420),
            new Item(null, "Apple Watch", 500),
            new Item("ABC", "Apple Iphone", 500)
    );

    @BeforeEach
    public void setUp() {
        itemRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemRepository::save)
                .blockLast();
    }

    @Test
    public void getAllItems() {
        StepVerifier.create(itemRepository.findAll())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void getItemById() {
        StepVerifier.create(itemRepository.findById("ABC"))
                .expectSubscription()
                .expectNextMatches((item -> item.getDescription().equals("Apple Iphone")))
                .verifyComplete();
    }

    @Test
    public void getItemByDescription() {
        StepVerifier.create(itemRepository.findByDescription("LG TV"))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveItem() {
        Item item = new Item(null, "Google Home Mini", 300);
        Mono<Item> savedItem = itemRepository.save(item);
        StepVerifier.create(savedItem)
                .expectSubscription()
                .expectNextMatches(item1 -> item1.getId() != null && item1.getDescription().equals("Google Home Mini"))
                .verifyComplete();
    }

    @Test
    public void updateItem() {
        double newPrice = 520;
        Mono<Item> updatedItem = itemRepository.findByDescription("LG TV")
                .map(item -> {
                    item.setPrice(newPrice);
                    return item;
                })
                .flatMap(item -> {
                    return itemRepository.save(item);
                });

        StepVerifier.create(updatedItem)
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice() == 520)
                .verifyComplete();
    }

    @Test
    public void deleteItem() {
        Mono<Void> deletedItem = itemRepository.findById("ABC")
                .map(Item::getId)
                .flatMap(id -> itemRepository.deleteById(id)
                );

        StepVerifier.create(deletedItem)
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemRepository.findAll())
                .expectSubscription()
                .expectNextCount(3)
                .verifyComplete();
    }
}
