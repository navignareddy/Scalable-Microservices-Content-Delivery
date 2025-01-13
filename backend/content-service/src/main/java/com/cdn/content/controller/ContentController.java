package com.cdn.content.controller;

import com.cdn.content.dto.ContentRequest;
import com.cdn.content.dto.ContentResponse;
import com.cdn.content.service.ContentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/content")
@CrossOrigin(origins = "*")
public class ContentController {

    @Autowired
    private ContentService contentService;

    @PostMapping("/upload")
    public ResponseEntity<ContentResponse> uploadContent(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("contentType") String contentType,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "tags", required = false) List<String> tags,
            @RequestParam(value = "isPublic", defaultValue = "true") Boolean isPublic) {
        
        ContentRequest request = new ContentRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setContentType(contentType);
        request.setUserId(userId);
        request.setTags(tags);
        request.setIsPublic(isPublic);
        
        ContentResponse response = contentService.uploadContent(file, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContentResponse> getContent(@PathVariable Long id) {
        ContentResponse response = contentService.getContentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ContentResponse>> getAllContent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "uploadDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String contentType,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String search) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<ContentResponse> response = contentService.getAllContent(
            pageable, contentType, userId, search);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContentResponse> updateContent(
            @PathVariable Long id,
            @Valid @RequestBody ContentRequest request) {
        ContentResponse response = contentService.updateContent(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable Long id) {
        contentService.deleteContent(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/download")
    public ResponseEntity<String> downloadContent(@PathVariable Long id) {
        String downloadUrl = contentService.generateDownloadUrl(id);
        return ResponseEntity.ok(downloadUrl);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ContentResponse>> searchContent(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ContentResponse> response = contentService.searchContent(query, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<ContentResponse>> getPopularContent(
            @RequestParam(defaultValue = "10") int limit) {
        List<ContentResponse> response = contentService.getPopularContent(limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ContentResponse>> getRecentContent(
            @RequestParam(defaultValue = "10") int limit) {
        List<ContentResponse> response = contentService.getRecentContent(limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ContentResponse>> getUserContent(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ContentResponse> response = contentService.getUserContent(userId, pageable);
        return ResponseEntity.ok(response);
    }
} 