package com.blog.cutom_blog.models;

import com.blog.cutom_blog.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@MappedSuperclass
public abstract class Audit implements Serializable {

    static final long serialVersionUID = 1l;

    @Id
    protected String id;

    protected LocalDateTime createdAt;

    @JsonIgnore
    protected LocalDateTime updatedAt;

    @PrePersist
    public void init() {
        setUpdatedAt(null);
        if(this.createdAt == null) {
            setCreatedAt(LocalDateTime.now());
        }
        if(StringUtils.isBlank(this.id)) {
            this.id = UUID.randomUUID().toString();
        }
    }

    public Audit() {

    }

    public Audit(final String id, final LocalDateTime createdAt, final LocalDateTime updatedAt) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.id = id;
    }

    @PreUpdate
    public void beforeUpdate(){
        setUpdatedAt(LocalDateTime.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        final Audit audit = (Audit) o;
        return id != null && Objects.equals(id, audit.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}