package org.example.advertisingagency.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "Offices")
public class Office {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OfficeID", nullable = false)
    private Integer id;

    @Size(max = 200)
    @Nationalized
    @Column(name = "Street", length = 200)
    private String street;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "CityID", nullable = false)
    private City cityID;

    @NotNull
    @Column(name = "CreateDatetime", nullable = false)
    private Instant createDatetime;

    @Column(name = "UpdateDatetime")
    private Instant updateDatetime;

}