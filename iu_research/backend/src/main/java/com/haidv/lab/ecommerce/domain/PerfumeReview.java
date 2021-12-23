package com.haidv.lab.ecommerce.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "perfume_reviews")
public class PerfumeReview {
    private Long id;
    private Long perfume_id;
    private Long reviews_id;

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }
}
