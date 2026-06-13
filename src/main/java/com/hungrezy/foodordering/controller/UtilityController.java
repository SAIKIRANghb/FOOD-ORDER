package com.hungrezy.foodordering.controller;

import com.hungrezy.foodordering.dto.ApiResponse;
import com.hungrezy.foodordering.dto.request.AnnouncementRequest;
import com.hungrezy.foodordering.dto.request.ContactMessageRequest;
import com.hungrezy.foodordering.entity.AdminCredentialShare;
import com.hungrezy.foodordering.entity.Announcement;
import com.hungrezy.foodordering.entity.ContactMessage;
import com.hungrezy.foodordering.dto.response.UserResponse;
import com.hungrezy.foodordering.entity.enums.Role;
import com.hungrezy.foodordering.entity.User;
import com.hungrezy.foodordering.entity.enums.UserStatus;
import com.hungrezy.foodordering.exception.ResourceNotFoundException;
import com.hungrezy.foodordering.mapper.DtoMapper;
import com.hungrezy.foodordering.repository.AdminCredentialShareRepository;
import com.hungrezy.foodordering.repository.AnnouncementRepository;
import com.hungrezy.foodordering.repository.ContactMessageRepository;
import com.hungrezy.foodordering.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class UtilityController {
    private final ContactMessageRepository contactMessageRepository;
    private final AnnouncementRepository announcementRepository;
    private final AdminCredentialShareRepository adminCredentialShareRepository;
    private final UserRepository userRepository;
    private final DtoMapper mapper;

    public UtilityController(ContactMessageRepository contactMessageRepository, AnnouncementRepository announcementRepository,
                             AdminCredentialShareRepository adminCredentialShareRepository,
                             UserRepository userRepository, DtoMapper mapper) {
        this.contactMessageRepository = contactMessageRepository;
        this.announcementRepository = announcementRepository;
        this.adminCredentialShareRepository = adminCredentialShareRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @PostMapping("/api/contact")
    ApiResponse<ContactMessage> contact(@Valid @RequestBody ContactMessageRequest request) {
        ContactMessage message = new ContactMessage();
        message.setName(request.getName());
        message.setEmail(request.getEmail());
        message.setSubject(request.getSubject());
        message.setMessage(request.getMessage());
        return ApiResponse.ok("Message received", contactMessageRepository.save(message));
    }

    @PostMapping("/api/contact/")
    ApiResponse<ContactMessage> contactWithSlash(@Valid @RequestBody ContactMessageRequest request) {
        return contact(request);
    }

    @GetMapping({"/api/contact", "/api/contact/"})
    ApiResponse<List<ContactMessage>> contacts() {
        return ApiResponse.ok("Contacts fetched", contactMessageRepository.findAll());
    }

    @DeleteMapping("/api/contact/{id}")
    ResponseEntity<Void> deleteContact(@PathVariable UUID id) {
        contactMessageRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/announcements")
    ApiResponse<List<Announcement>> announcements() {
        return ApiResponse.ok("Announcements fetched", announcementRepository.findByActiveTrue());
    }

    @GetMapping("/api/admin/announcements")
    ApiResponse<List<Announcement>> adminAnnouncements() {
        List<Announcement> active = announcementRepository.findByActiveTrue();
        return ApiResponse.ok("Announcements fetched", active.isEmpty() ? announcementRepository.findAll() : active);
    }

    @PostMapping("/api/admin/announce")
    ApiResponse<Announcement> announce(@RequestBody AnnouncementRequest request) {
        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle());
        announcement.setMessage(request.getMessage());
        announcement.setActive(true);
        return ApiResponse.ok("Announcement created", announcementRepository.save(announcement));
    }

    @GetMapping("/api/admin/all")
    ApiResponse<List<UserResponse>> admins() {
        List<UserResponse> admins = userRepository.findAll().stream()
                .filter(user -> user.getRole() == Role.ADMIN)
                .map(mapper::toUserResponse)
                .toList();
        return ApiResponse.ok("Admins fetched", admins);
    }

    @PostMapping("/api/admin/{id}/status")
    @Transactional
    ApiResponse<UserResponse> updateAdminStatus(@PathVariable UUID id, @RequestBody Map<String, Boolean> body) {
        User admin = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        Boolean active = body == null ? null : body.get("active");
        if (active != null) {
            admin.setStatus(active ? UserStatus.ACTIVE : UserStatus.SUSPENDED);
        }
        return ApiResponse.ok("Admin status updated", mapper.toUserResponse(admin));
    }

    @PostMapping("/api/admin/{id}/share-credentials")
    @Transactional
    ApiResponse<Map<String, Boolean>> shareCredentials(@PathVariable UUID id) {
        User admin = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Admin not found"));
        AdminCredentialShare share = new AdminCredentialShare();
        share.setAdmin(admin);
        share.setSharedAt(Instant.now());
        adminCredentialShareRepository.save(share);
        return ApiResponse.ok("Credentials shared", Map.of("shared", true));
    }
}
