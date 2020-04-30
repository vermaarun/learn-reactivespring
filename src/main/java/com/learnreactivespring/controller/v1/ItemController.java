package com.learnreactivespring.controller.v1;

import com.learnreactivespring.constants.ItemConstants;
import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ItemController {

    @Autowired
    ItemRepository itemRepository;

    @GetMapping(ItemConstants.ITEM_END_POINT_VI)
    public Flux<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @GetMapping(ItemConstants.ITEM_END_POINT_VI + "/{id}")
    public Mono<ResponseEntity<Item>> getOneItem(@PathVariable String id) {
        return itemRepository.findById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(ItemConstants.ITEM_END_POINT_VI)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createItem(@RequestBody Item item) {
        return itemRepository.save(item);
    }

    @DeleteMapping(ItemConstants.ITEM_END_POINT_VI + "/{id}")
    public Mono<Void> deleteItem(@PathVariable String id) {
        return itemRepository.deleteById(id);
    }

    @GetMapping(ItemConstants.ITEM_END_POINT_VI + "/runtimeException")
    public Flux<Item> runTimeException() {
        return itemRepository.findAll()
                .concatWith(Mono.error(new RuntimeException("Exception Occurred")));
    }

    @PutMapping(ItemConstants.ITEM_END_POINT_VI + "/{id}")
    public Mono<ResponseEntity<Item>> updateItem(@RequestBody Item item, @PathVariable String id) {
        return itemRepository.findById(id)
                .flatMap(currentItem -> {
                    currentItem.setPrice(item.getPrice());
                    currentItem.setDescription(item.getDescription());
                    return itemRepository.save(currentItem);
                })
                .map(updatedItem -> new ResponseEntity<>(updatedItem, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
