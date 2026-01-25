package com.modulythe.framework.infrastructure.common.pagination;

import com.modulythe.framework.domain.common.pagination.PageModel;
import com.modulythe.framework.domain.common.pagination.PageableModel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Utility class to build a Domain {@link PageModel} from Reactive sources.
 * <p>
 * This class facilitates the construction of a paginated response in a reactive environment (e.g., using R2DBC/WebFlux).
 * It combines a {@link Flux} of content and a {@link Mono} of total elements count into a single {@link Mono} of {@link PageModel}.
 * </p>
 */
public final class ReactivePageBuilder {

    private ReactivePageBuilder() {
        // Utility class
    }

    /**
     * Builds a {@link Mono} containing a {@link PageModel} by combining the content and total count.
     * <p>
     * This method subscribes to the provided content {@link Flux}, collects it into a list,
     * and zips it with the total element count from the {@link Mono}.
     * </p>
     *
     * @param <T>           the type of the content elements.
     * @param content       the {@link Flux} emitting the content elements for the current page.
     * @param totalElements the {@link Mono} emitting the total number of elements across all pages.
     * @param pageable      the original {@link PageableModel} request containing page number and size.
     * @return a {@link Mono} that emits the constructed {@link PageModel}.
     */
    public static <T> Mono<PageModel<T>> buildPage(
            Flux<T> content,
            Mono<Long> totalElements,
            PageableModel pageable) {

        return Mono.zip(content.collectList(), totalElements)
                .map(tuple -> {
                    List<T> list = tuple.getT1();
                    Long total = tuple.getT2();

                    return PageModel.builder(list)
                            .itemsPerPage(pageable.getItemsPerPage())
                            .pageNumber(pageable.getPageNumber())
                            .totalItems(total)
                            .build();
                });
    }
}
