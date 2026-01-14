package com.agrowmart.repository;



import com.agrowmart.entity.WomenProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

public interface WomenProductRepository extends JpaRepository<WomenProduct, Long> {
 List<WomenProduct> findBySellerId(Long sellerId);
 List<WomenProduct> findByCategory(String category);
 List<WomenProduct> findByIsAvailableTrue();
//Add this to WomenProductRepository

List<WomenProduct> findAllByOrderByCreatedAtDesc();

@Query("SELECT wp FROM WomenProduct wp JOIN wp.seller u " +
	       "WHERE u.onlineStatus = 'ONLINE' AND u.profileCompleted = 'YES'")
	List<WomenProduct> findAllFromOnlineSellers();

}