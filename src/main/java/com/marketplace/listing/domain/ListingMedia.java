package com.marketplace.listing.domain;

import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "listing_media")
public class ListingMedia extends BaseEntity {

    @Column(nullable = false)
    private UUID listingId;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(length = 50)
    private String mediaType; // IMAGE, VIDEO

    private Integer displayOrder;

    @Column(length = 500)
    private String altText;
}
