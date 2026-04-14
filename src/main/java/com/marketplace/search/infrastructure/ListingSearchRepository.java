package com.marketplace.search.infrastructure;

import com.marketplace.search.application.ListingDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data Elasticsearch repository for ListingDocument.
 * Provides basic CRUD operations and custom query methods
 * for searching and filtering listings in the Elasticsearch index.
 */
public interface ListingSearchRepository extends ElasticsearchRepository<ListingDocument, UUID> {

    /**
     * Find all listings with the given status (e.g., "ACTIVE").
     */
    List<ListingDocument> findByStatus(String status);

    /**
     * Find all listings in a specific category with the given status.
     */
    List<ListingDocument> findByCategoryAndStatus(String category, String status);

    /**
     * Find all listings by provider ID.
     */
    List<ListingDocument> findByProviderId(UUID providerId);

    /**
     * Find listings by location containing the given text and status.
     */
    List<ListingDocument> findByLocationContainingAndStatus(String location, String status);

    /**
     * Find distinct categories among listings with the given status.
     */
    List<ListingDocument> findByStatusIn(List<String> statuses);
}
