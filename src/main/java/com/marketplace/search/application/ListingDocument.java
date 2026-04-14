package com.marketplace.search.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Elasticsearch document representing a searchable listing.
 * This is the read-model projection of the Listing aggregate,
 * denormalized for efficient full-text search and filtering.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "listings")
@Setting(replicas = 0)
public class ListingDocument {

    @Id
    private UUID id;

    @Field(type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard")
    private String title;

    @Field(type = FieldType.Text, analyzer = "standard", searchAnalyzer = "standard")
    private String description;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Keyword)
    private String subcategory;

    @Field(type = FieldType.Double)
    private BigDecimal price;

    @Field(type = FieldType.Keyword)
    private String currency;

    @Field(type = FieldType.Keyword)
    private String location;

    @Field(type = FieldType.Double)
    private Double latitude;

    @Field(type = FieldType.Double)
    private Double longitude;

    @Field(type = FieldType.Keyword)
    private UUID providerId;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String amenities;
}
