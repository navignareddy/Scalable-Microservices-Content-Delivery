package com.cdn.content.service;

import com.cdn.content.dto.ContentRequest;
import com.cdn.content.dto.ContentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ContentService {
    
    ContentResponse uploadContent(MultipartFile file, ContentRequest request);
    
    ContentResponse getContentById(Long id);
    
    Page<ContentResponse> getAllContent(Pageable pageable, String contentType, Long userId, String search);
    
    ContentResponse updateContent(Long id, ContentRequest request);
    
    void deleteContent(Long id);
    
    String generateDownloadUrl(Long id);
    
    Page<ContentResponse> searchContent(String query, Pageable pageable);
    
    List<ContentResponse> getPopularContent(int limit);
    
    List<ContentResponse> getRecentContent(int limit);
    
    Page<ContentResponse> getUserContent(Long userId, Pageable pageable);
} 