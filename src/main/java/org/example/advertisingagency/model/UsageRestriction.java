package org.example.advertisingagency.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "UsageRestrictions")
public class UsageRestriction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UsageRestrictionsID", nullable = false)
    private Integer id;

    @Nationalized
    @Lob
    @Column(name = "Description")
    private String description;

    @NotNull
    @Column(name = "CreateDatetime", nullable = false)
    private Instant createDatetime;

    @Column(name = "UpdateDatetime")
    private Instant updateDatetime;

}