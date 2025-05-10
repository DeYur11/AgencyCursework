package org.example.advertisingagency.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "Material")
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaterialID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TypeID")
    private MaterialType type;

    @Size(max = 150)
    @NotNull
    @Nationalized
    @Column(name = "Name", nullable = false, length = 150)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StatusID")
    private MaterialStatus status;

    @Nationalized
    @Lob
    @Column(name = "Description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UsageRestrictionsID")
    private UsageRestriction usageRestriction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LicenceTypeID")
    private LicenceType licenceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TargetAudienceID")
    private TargetAudience targetAudience;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LanguageID")
    private Language language;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TaskID")
    private Task task;

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