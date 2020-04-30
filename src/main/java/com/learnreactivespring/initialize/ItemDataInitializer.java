package com.learnreactivespring.initialize;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.document.ItemCapped;
import com.learnreactivespring.repository.ItemCappedRepository;
import com.learnreactivespring.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test")
@Slf4j
public class ItemDataInitializer implements CommandLineRunner {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemCappedRepository itemCappedRepository;

    @Autowired
    MongoOperations mongoOperations;

    @Override
    public void run(String... args) throws Exception {
        initialDataSetup();
        createCappedCollection();
        itemCappedDataSetup();
    }

    private void createCappedCollection() {
        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(ItemCapped.class,
                CollectionOptions.empty().maxDocuments(20).size(50000).capped());
    }

    private List<Item> data() {
        return Arrays.asList(
                new Item(null, "Samsung TV", 400),
                new Item(null, "LG TV", 420),
                new Item(null, "Apple Watch", 500),
                new Item("ABC", "Apple Iphone", 500)
        );
    }

    private void initialDataSetup() {
        itemRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemRepository::save)
                .subscribe();
    }

    public void itemCappedDataSetup() {

        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
                .map(i -> new ItemCapped(null, "Random Item" + i, (100 + i)));
        itemCappedRepository
                .insert(itemCappedFlux)
                .subscribe(itemCapped -> {
                    log.info("ItemCapped inserted as : " + itemCapped);
                });
    }
}
