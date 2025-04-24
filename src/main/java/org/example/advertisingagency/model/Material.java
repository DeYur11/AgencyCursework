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
    private MaterialType typeID;

    @Size(max = 150)
    @NotNull
    @Nationalized
    @Column(name = "Name", nullable = false, length = 150)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "StatusID")
    private MaterialStatus statusID;

    @Nationalized
    @Lob
    @Column(name = "Description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UsageRestrictionsID")
    private UsageRestriction usageRestrictionsID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LicenceTypeID")
    private LicenceType licenceTypeID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TargetAudienceID")
    private TargetAudience targetAudienceID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LanguageID")
    private Language languageID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TaskID")
    private Task taskID;

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