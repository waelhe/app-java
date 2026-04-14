package com.marketplace.search.application;

import com.marketplace.listing.domain.ListingActivatedEvent;
import com.marketplace.search.domain.Suggestion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Event processor that listens to listing domain events and synchronizes
 * the Elasticsearch search index accordingly:
 * - ListingActivatedEvent → index the listing for search
 * - ListingDeactivatedEvent → remove the listing from search index
 *
 * <p>Uses @ApplicationModuleListener for reliable, transactional event
 * processing across Spring Modulith module boundaries.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ListingEventProcessor {

    private final SearchService searchService;

    /**
     * Handle listing activation events by indexing the listing
     * into the Elasticsearch search index.
     *
     * <p>The ListingActivatedEvent currently carries listingId, providerId,
     * title, and category. For full indexing, we construct a ListingDocument
     * with the available data. Additional fields (description, price, location,
     * etc.) would ideally be populated by querying the Listing module
     * or by extending the event payload.</p>
     */
    @ApplicationModuleListener
    public void on(ListingActivatedEvent event) {
        log.info("Received ListingActivatedEvent for listing {}", event.listingId());
        try {
            ListingDocument document = ListingDocument.builder()
                    .id(event.listingId())
                    .providerId(event.providerId())
                    .title(event.title())
                    .category(event.category())
                    .status("ACTIVE")
                    .currency("USD")
                    .build();

            searchService.indexListing(document);
            log.info("Successfully indexed listing {} from activation event", event.listingId());
        } catch (Exception e) {
            log.error("Failed to index listing {} from activation event: {}",
                    event.listingId(), e.getMessage(), e);
        }
    }

    /**
     * Handle listing deactivation events by removing the listing
     * from the Elasticsearch search index.
     *
     * <p>Note: The Listing module currently does not fire a ListingDeactivatedEvent
     * when a listing is deactivated. This listener is prepared for when that event
     * is added to the Listing module (com.marketplace.listing.domain.ListingDeactivatedEvent).
     * Until then, listings can be removed from the index via the
     * {@link SearchService#removeListing(UUID)} method directly.</p>
     *
     * <p>To enable this listener, the Listing module should:
     * 1. Create a ListingDeactivatedEvent record implementing DomainEvent
     * 2. Fire the event in Listing.deactivate() method
     * 3. Update the import below to reference the listing module's event class</p>
     */
    // TODO: Uncomment when ListingDeactivatedEvent is added to the listing module
    // @ApplicationModuleListener
    // public void onListingDeactivated(ListingDeactivatedEvent event) {
    //     log.info("Received ListingDeactivatedEvent for listing {}", event.listingId());
    //     try {
    //         searchService.removeListing(event.listingId());
    //         log.info("Successfully removed listing {} from search index", event.listingId());
    //     } catch (Exception e) {
    //         log.error("Failed to remove listing {} from search index: {}",
    //                 event.listingId(), e.getMessage(), e);
    //     }
    // }
}
