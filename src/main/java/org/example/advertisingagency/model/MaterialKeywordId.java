package org.example.advertisingagency.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class MaterialKeywordId implements Serializable {
    @Serial
    private static final long serialVersionUID = 7106219427262170248L;
    @NotNull
    @Column(name = "MaterialID", nullable = false)
    private Integer materialID;

    @NotNull
    @Column(name = "KeywordID", nullable = false)
    private Integer keywordID;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MaterialKeywordId entity = (MaterialKeywordId) o;
        return Objects.equals(this.keywordID, entity.keywordID) &&
                Objects.equals(this.materialID, entity.materialID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keywordID, materialID);
    }

}