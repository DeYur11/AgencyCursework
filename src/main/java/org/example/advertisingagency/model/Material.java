package org.example.advertisingagency.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaterialID", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TypeID")
    private MaterialType materialType;

    // Додаємо поле для серіалізації ID типу матеріалу
    @Transient
    private Integer materialTypeId;

    public Integer getMaterialTypeId() {
        return materialType != null ? materialType.getId() : null;
    }

    @Size(max = 150)
    @NotNull
    @Nationalized
    @Column(name = "Name", nullable = false, length = 150)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "StatusID")
    private MaterialStatus status;

    // Додаємо поле для серіалізації ID статусу
    @Transient
    private Integer statusId;

    public Integer getStatusId() {
        return status != null ? status.getId() : null;
    }

    @Nationalized
    @Column(name = "Description", length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "UsageRestrictionsID")
    private UsageRestriction usageRestriction;

    // Додаємо поле для серіалізації ID обмежень використання
    @Transient
    private Integer usageRestrictionId;

    public Integer getUsageRestrictionId() {
        return usageRestriction != null ? usageRestriction.getId() : null;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "LicenceTypeID")
    private LicenceType licenceType;

    // Додаємо поле для серіалізації ID типу ліцензії
    @Transient
    private Integer licenceTypeId;

    public Integer getLicenceTypeId() {
        return licenceType != null ? licenceType.getId() : null;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TargetAudienceID")
    private TargetAudience targetAudience;

    // Додаємо поле для серіалізації ID цільової аудиторії
    @Transient
    private Integer targetAudienceId;

    public Integer getTargetAudienceId() {
        return targetAudience != null ? targetAudience.getId() : null;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "LanguageID")
    private Language language;

    // Додаємо поле для серіалізації ID мови
    @Transient
    private Integer languageId;

    public Integer getLanguageId() {
        return language != null ? language.getId() : null;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TaskID")
    private Task task;

    // Додаємо поле для серіалізації ID завдання
    @Transient
    private Integer taskId;

    public Integer getTaskId() {
        return task != null ? task.getId() : null;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "MaterialKeywords",
            joinColumns = @JoinColumn(name = "MaterialId"),
            inverseJoinColumns = @JoinColumn(name = "KeywordId")
    )
    private List<Keyword> keywords = new ArrayList<>();

    // Додаємо поле для серіалізації ID ключових слів
    @Transient
    private List<Integer> keywordIds;

    public List<Integer> getKeywordIds() {
        if (keywords == null) return new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        for (Keyword keyword : keywords) {
            ids.add(keyword.getId());
        }
        return ids;
    }

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

    // Методи для встановлення значень за ID (для десеріалізації)
    public void setMaterialTypeId(Integer id) {
        if (id != null) {
            MaterialType type = new MaterialType();
            type.setId(id);
            this.materialType = type;
        }
    }

    public void setStatusId(Integer id) {
        if (id != null) {
            MaterialStatus status = new MaterialStatus();
            status.setId(id);
            this.status = status;
        }
    }

    public void setUsageRestrictionId(Integer id) {
        if (id != null) {
            UsageRestriction restriction = new UsageRestriction();
            restriction.setId(id);
            this.usageRestriction = restriction;
        }
    }

    public void setLicenceTypeId(Integer id) {
        if (id != null) {
            LicenceType licence = new LicenceType();
            licence.setId(id);
            this.licenceType = licence;
        }
    }

    public void setTargetAudienceId(Integer id) {
        if (id != null) {
            TargetAudience audience = new TargetAudience();
            audience.setId(id);
            this.targetAudience = audience;
        }
    }

    public void setLanguageId(Integer id) {
        if (id != null) {
            Language lang = new Language();
            lang.setId(id);
            this.language = lang;
        }
    }

    public void setTaskId(Integer id) {
        if (id != null) {
            Task task = new Task();
            task.setId(id);
            this.task = task;
        }
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
}