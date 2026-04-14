package com.marketplace.listing.domain;

import com.marketplace.shared.domain.AggregateRoot;
import com.marketplace.shared.domain.Money;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "listings")
public class Listing extends AggregateRoot {

    @Column(nullable = false)
    private UUID providerId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(length = 100)
    private String subcategory;

    @Embedded
    private Money price;

    @Column(length = 50)
    private String pricingType; // HOURLY, DAILY, FIXED

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private ListingStatus status;

    @Column(length = 100)
    private String location;

    private Double latitude;

    private Double longitude;

    @Column(columnDefinition = "TEXT")
    private String amenities; // JSON stored as string

    @Column(columnDefinition = "TEXT")
    private String rules; // JSON stored as string

    private Instant activatedAt;

    public void activate() {
        this.status = ListingStatus.ACTIVE;
        this.activatedAt = Instant.now();
        registerEvent(new ListingActivatedEvent(getId(), providerId, title, category));
    }

    public void deactivate() {
        this.status = ListingStatus.INACTIVE;
    }

    public void pause() {
        this.status = ListingStatus.PAUSED;
    }

    public void updateDetails(String title, String description, String category, String subcategory,
                               Money price, String pricingType, String location, Double latitude, Double longitude,
                               String amenities, String rules) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.subcategory = subcategory;
        this.price = price;
        this.pricingType = pricingType;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.amenities = amenities;
        this.rules = rules;
    }
}
