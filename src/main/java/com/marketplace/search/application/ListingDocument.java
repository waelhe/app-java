package com.marketplace.search.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "listings")
public class ListingDocument {

    @Id
    private UUID id;
    private String title;
    private String description;
    private String category;
    private String subcategory;
    private BigDecimal price;
    private String currency;
    private String location;
    private Double latitude;
    private Double longitude;
    private UUID providerId;
    private String status;
    private String amenities;
}
