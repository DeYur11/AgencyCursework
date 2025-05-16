package org.example.advertisingagency.model;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "Material")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaterialID", nullable = false)
    private Integer id;

    /**
     * Material Type relationship
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TypeID")
    @JsonIdentityReference(alwaysAsId = true)
    private MaterialType materialType;

    // Field for serialization/deserialization with ID
    @Transient
    @JsonProperty("materialTypeId")
    private Integer materialTypeId;

    public Integer getMaterialTypeId() {
        return materialType != null ? materialType.getId() : null;
    }

    public void setMaterialTypeId(Integer id) {
        if (this.materialType == null) {
            this.materialType = new MaterialType();
        }
        this.materialType.setId(id);
    }

    @Size(max = 150)
    @NotNull
    @Nationalized
    @Column(name = "Name", nullable = false, length = 150)
    private String name;

    /**
     * Status relationship
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "StatusID")
    @JsonIdentityReference(alwaysAsId = true)
    private MaterialStatus status;

    // Field for serialization/deserialization with ID
    @Transient
    @JsonProperty("statusId")
    private Integer statusId;

    public Integer getStatusId() {
        return status != null ? status.getId() : null;
    }

    public void setStatusId(Integer id) {
        if (this.status == null) {
            this.status = new MaterialStatus();
        }
        this.status.setId(id);
    }

    @Nationalized
    @Column(name = "Description", length = 1000)
    private String description;

    /**
     * Usage Restriction relationship
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "UsageRestrictionsID")
    @JsonIdentityReference(alwaysAsId = true)
    private UsageRestriction usageRestriction;

    // Field for serialization/deserialization with ID
    @Transient
    @JsonProperty("usageRestrictionId")
    private Integer usageRestrictionId;

    public Integer getUsageRestrictionId() {
        return usageRestriction != null ? usageRestriction.getId() : null;
    }

    public void setUsageRestrictionId(Integer id) {
        if (id != null) {
            if (this.usageRestriction == null) {
                this.usageRestriction = new UsageRestriction();
            }
            this.usageRestriction.setId(id);
        }
    }

    /**
     * Licence Type relationship
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "LicenceTypeID")
    @JsonIdentityReference(alwaysAsId = true)
    private LicenceType licenceType;

    // Field for serialization/deserialization with ID
    @Transient
    @JsonProperty("licenceTypeId")
    private Integer licenceTypeId;

    public Integer getLicenceTypeId() {
        return licenceType != null ? licenceType.getId() : null;
    }

    public void setLicenceTypeId(Integer id) {
        if (id != null) {
            if (this.licenceType == null) {
                this.licenceType = new LicenceType();
            }
            this.licenceType.setId(id);
        }
    }

    /**
     * Target Audience relationship
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TargetAudienceID")
    @JsonIdentityReference(alwaysAsId = true)
    private TargetAudience targetAudience;

    // Field for serialization/deserialization with ID
    @Transient
    @JsonProperty("targetAudienceId")
    private Integer targetAudienceId;

    public Integer getTargetAudienceId() {
        return targetAudience != null ? targetAudience.getId() : null;
    }

    public void setTargetAudienceId(Integer id) {
        if (id != null) {
            if (this.targetAudience == null) {
                this.targetAudience = new TargetAudience();
            }
            this.targetAudience.setId(id);
        }
    }

    /**
     * Language relationship
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "LanguageID")
    @JsonIdentityReference(alwaysAsId = true)
    private Language language;

    // Field for serialization/deserialization with ID
    @Transient
    @JsonProperty("languageId")
    private Integer languageId;

    public Integer getLanguageId() {
        return language != null ? language.getId() : null;
    }

    public void setLanguageId(Integer id) {
        if (id != null) {
            if (this.language == null) {
                this.language = new Language();
            }
            this.language.setId(id);
        }
    }

    /**
     * Task relationship
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TaskID")
    @JsonIdentityReference(alwaysAsId = true)
    private Task task;

    // Field for serialization/deserialization with ID
    @Transient
    @JsonProperty("taskId")
    private Integer taskId;

    public Integer getTaskId() {
        return task != null ? task.getId() : null;
    }

    public void setTaskId(Integer id) {
        if (id != null) {
            if (this.task == null) {
                this.task = new Task();
            }
            this.task.setId(id);
        }
    }

    /**
     * Keywords relationship
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "MaterialKeywords",
            joinColumns = @JoinColumn(name = "MaterialId"),
            inverseJoinColumns = @JoinColumn(name = "KeywordId")
    )
    @JsonIdentityReference(alwaysAsId = true)
    private List<Keyword> keywords = new ArrayList<>();

    // Field for serialization/deserialization with IDs
    @Transient
    @JsonProperty("keywordIds")
    private List<Integer> keywordIds;

    public List<Integer> getKeywordIds() {
        if (keywords == null) return new ArrayList<>();
        return keywords.stream()
                .map(Keyword::getId)
                .toList();
    }

    public void setKeywordIds(List<Integer> ids) {
        if (ids == null) return;
        this.keywords = new ArrayList<>();
        for (Integer id : ids) {
            Keyword keyword = new Keyword();
            keyword.setId(id);
            this.keywords.add(keyword);
        }
    }

    /**
     * Datetime fields
     */
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

    /**
     * Method to make a deep clone for rollback purposes
     * that properly handles entity relationships.
     */
    public Material deepClone() {
        Material clone = new Material();

        // Copy simple fields
        clone.setId(this.id);
        clone.setName(this.name);
        clone.setDescription(this.description);
        clone.setCreateDatetime(this.createDatetime);
        clone.setUpdateDatetime(this.updateDatetime);

        // Copy relationship IDs
        clone.setMaterialTypeId(this.getMaterialTypeId());
        clone.setStatusId(this.getStatusId());
        clone.setUsageRestrictionId(this.getUsageRestrictionId());
        clone.setLicenceTypeId(this.getLicenceTypeId());
        clone.setTargetAudienceId(this.getTargetAudienceId());
        clone.setLanguageId(this.getLanguageId());
        clone.setTaskId(this.getTaskId());
        clone.setKeywordIds(this.getKeywordIds());

        return clone;
    }
}