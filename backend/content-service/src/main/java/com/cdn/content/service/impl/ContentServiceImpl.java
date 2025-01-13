package com.cdn.content.service.impl;

import com.cdn.content.dto.ContentRequest;
import com.cdn.content.dto.ContentResponse;
import com.cdn.content.entity.Content;
import com.cdn.content.repository.ContentRepository;
import com.cdn.content.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentRepository contentRepository;

    @Override
    public ContentResponse uploadContent(MultipartFile file, ContentRequest request) {
        Content content = new Content();
        content.setTitle(request.getTitle());
        content.setDescription(request.getDescription());
        content.setContentType(request.getContentType());
        content.setUserId(request.getUserId());
        content.setIsPublic(request.getIsPublic());
        content.setTags(request.getTags());
        content.setMetadata(request.getMetadata());
        
        // Handle file upload (simplified - in real scenario would use S3 or local storage)
        if (file != null && !file.isEmpty()) {
            content.setFilePath("/uploads/" + file.getOriginalFilename());
            content.setFileSize(file.getSize());
            content.setMimeType(file.getContentType());
        }
        
        content.setUploadDate(LocalDateTime.now());
        content.setLastModified(LocalDateTime.now());
        
        Content saved = contentRepository.save(content);
        return convertToResponse(saved);
    }

    @Override
    @Cacheable(value = "content", key = "#id")
    public ContentResponse getContentById(Long id) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Content not found with id: " + id));
        
        // Increment download count
        content.incrementDownloadCount();
        contentRepository.save(content);
        
        return convertToResponse(content);
    }

    @Override
    public Page<ContentResponse> getAllContent(Pageable pageable, String contentType, 
                                             Long userId, String search) {
        Page<Content> contentPage;
        
        if (search != null && !search.isEmpty()) {
            contentPage = contentRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                    search, search, pageable);
        } else if (contentType != null && userId != null) {
            contentPage = contentRepository.findByContentTypeAndUserId(contentType, userId, pageable);
        } else if (contentType != null) {
            contentPage = contentRepository.findByContentType(contentType, pageable);
        } else if (userId != null) {
            contentPage = contentRepository.findByUserId(userId, pageable);
        } else {
            contentPage = contentRepository.findByIsPublicTrue(pageable);
        }
        
        List<ContentResponse> responses = contentPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return new PageImpl<>(responses, pageable, contentPage.getTotalElements());
    }

    @Override
    public ContentResponse updateContent(Long id, ContentRequest request) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Content not found with id: " + id));
        
        content.setTitle(request.getTitle());
        content.setDescription(request.getDescription());
        content.setContentType(request.getContentType());
        content.setIsPublic(request.getIsPublic());
        content.setTags(request.getTags());
        content.setMetadata(request.getMetadata());
        content.setLastModified(LocalDateTime.now());
        
        Content updated = contentRepository.save(content);
        return convertToResponse(updated);
    }

    @Override
    public void deleteContent(Long id) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Content not found with id: " + id));
        contentRepository.delete(content);
    }

    @Override
    public String generateDownloadUrl(Long id) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Content not found with id: " + id));
        
        // In real scenario, generate signed URL for S3 or return direct path
        return "/api/v1/content/" + id + "/download";
    }

    @Override
    public Page<ContentResponse> searchContent(String query, Pageable pageable) {
        Page<Content> contentPage = contentRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                query, query, pageable);
        
        List<ContentResponse> responses = contentPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return new PageImpl<>(responses, pageable, contentPage.getTotalElements());
    }

    @Override
    public List<ContentResponse> getPopularContent(int limit) {
        List<Content> popularContent = contentRepository.findTopByOrderByDownloadCountDesc()
                .stream().limit(limit).collect(Collectors.toList());
        
        return popularContent.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ContentResponse> getRecentContent(int limit) {
        List<Content> recentContent = contentRepository.findTopByOrderByUploadDateDesc()
                .stream().limit(limit).collect(Collectors.toList());
        
        return recentContent.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ContentResponse> getUserContent(Long userId, Pageable pageable) {
        Page<Content> contentPage = contentRepository.findByUserId(userId, pageable);
        
        List<ContentResponse> responses = contentPage.getContent().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return new PageImpl<>(responses, pageable, contentPage.getTotalElements());
    }

    private ContentResponse convertToResponse(Content content) {
        ContentResponse response = new ContentResponse();
        response.setId(content.getId());
        response.setTitle(content.getTitle());
        response.setDescription(content.getDescription());
        response.setContentType(content.getContentType());
        response.setFilePath(content.getFilePath());
        response.setFileSize(content.getFileSize());
        response.setMimeType(content.getMimeType());
        response.setUploadDate(content.getUploadDate());
        response.setLastModified(content.getLastModified());
        response.setUserId(content.getUserId());
        response.setDownloadCount(content.getDownloadCount());
        response.setIsPublic(content.getIsPublic());
        response.setTags(content.getTags());
        response.setMetadata(content.getMetadata());
        response.setDownloadUrl("/api/v1/content/" + content.getId() + "/download");
        return response;
    }
} 