package com.cdn.content.repository;

import com.cdn.content.entity.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    
    Page<Content> findByIsPublicTrue(Pageable pageable);
    
    Page<Content> findByUserId(Long userId, Pageable pageable);
    
    Page<Content> findByContentType(String contentType, Pageable pageable);
    
    Page<Content> findByContentTypeAndUserId(String contentType, Long userId, Pageable pageable);
    
    Page<Content> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String title, String description, Pageable pageable);
    
    List<Content> findTopByOrderByDownloadCountDesc();
    
    List<Content> findTopByOrderByUploadDateDesc();
    
    @Query("SELECT c FROM Content c WHERE c.isPublic = true AND " +
           "(LOWER(c.title) LIKE LOWER(CONCAT('%', ?1, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', ?1, '%')))")
    Page<Content> searchPublicContent(String query, Pageable pageable);
} 