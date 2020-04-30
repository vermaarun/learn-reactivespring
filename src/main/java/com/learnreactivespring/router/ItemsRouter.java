package com.learnreactivespring.router;

import com.learnreactivespring.handler.ItemsHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.learnreactivespring.constants.ItemConstants.ITEM_FUNCTIONAL_END_POINT_VI;
import static com.learnreactivespring.constants.ItemConstants.ITEM_STREAM_FUNCTIONAL_END_POINT_VI;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ItemsRouter {
    @Bean
    public RouterFunction<ServerResponse> itemsRoute(ItemsHandler itemsHandler) {
        return RouterFunctions
                .route(GET(ITEM_FUNCTIONAL_END_POINT_VI).and(accept(MediaType.APPLICATION_JSON)), itemsHandler::getAllItems)
                .andRoute(GET(ITEM_FUNCTIONAL_END_POINT_VI + "/exception").and(accept(MediaType.APPLICATION_JSON)), itemsHandler::itemException)
                .andRoute(GET(ITEM_FUNCTIONAL_END_POINT_VI + "/{id}").and(accept(MediaType.APPLICATION_JSON)), itemsHandler::getOneItem)
                .andRoute(POST(ITEM_FUNCTIONAL_END_POINT_VI).and(accept(MediaType.APPLICATION_JSON)), itemsHandler::createItem)
                .andRoute(DELETE(ITEM_FUNCTIONAL_END_POINT_VI + "/{id}").and(accept(MediaType.APPLICATION_JSON)), itemsHandler::deleteItem)
                .andRoute(PUT(ITEM_FUNCTIONAL_END_POINT_VI + "/{id}").and(accept(MediaType.APPLICATION_JSON)), itemsHandler::updateItem);
    }

//    @Bean
//    public RouterFunction<ServerResponse> errorRoute(ItemsHandler itemsHandler) {
//        return RouterFunctions
//                .route(GET("/v1/fun/exception").and(accept(MediaType.APPLICATION_JSON)), itemsHandler::itemException);
//    }

    @Bean
    public RouterFunction<ServerResponse> itemsStreamRoute(ItemsHandler itemsHandler) {
        return RouterFunctions
                .route(GET(ITEM_STREAM_FUNCTIONAL_END_POINT_VI).and(accept(MediaType.APPLICATION_JSON)), itemsHandler::getAllItemsStream);
    }
}
