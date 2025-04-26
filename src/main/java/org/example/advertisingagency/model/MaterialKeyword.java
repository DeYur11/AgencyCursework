package org.example.advertisingagency.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "MaterialKeywords")
public class MaterialKeyword {
    @EmbeddedId
    private MaterialKeywordId id;

    @MapsId("materialID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MaterialID", nullable = false)
    private Material material;

    @MapsId("keywordID")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "KeywordID", nullable = false)
    private Keyword keyword;

    @NotNull
    @Column(name = "CreateDatetime", nullable = false)
    private Instant createDatetime;

    @Column(name = "UpdateDatetime")
    private Instant updateDatetime;

    @PrePersist
    protected void onCreate() {
        createDatetime = OffsetDateTime.now().toInstant();
    }

    @PreUpdate
    protected void onUpdate() {
        updateDatetime = OffsetDateTime.now().toInstant();
    }
}