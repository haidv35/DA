package com.haidv.lab.ecommerce.repository;

import com.haidv.lab.ecommerce.domain.PerfumeReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PerfumeReviewRepository  extends JpaRepository<PerfumeReview, Long> {

    @Query(value = "SELECT review.id, reviews_id, perfume_id, author FROM perfume_reviews LEFT JOIN review ON perfume_reviews.reviews_id = review.id WHERE author=:author AND perfume_id=:perfume_id",
            nativeQuery = true)
    List<PerfumeReview> queryBy(@Param("perfume_id") Long perfume_id, @Param("author") String author);

}
