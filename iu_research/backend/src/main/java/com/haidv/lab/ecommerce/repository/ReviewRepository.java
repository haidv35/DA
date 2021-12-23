package com.haidv.lab.ecommerce.repository;

import com.haidv.lab.ecommerce.domain.Review;
import com.haidv.lab.ecommerce.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
}
