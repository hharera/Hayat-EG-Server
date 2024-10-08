package com.harera.hayat.controller.announcement;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.harera.hayat.model.announcement.AnnouncementResponse;
import com.harera.hayat.service.announcement.AnnouncementService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/announcements")
@Tag(name = "Announcement")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @Operation(summary = "List", description = "List announcements",
                    tags = "Announcement",
                    responses = @ApiResponse(responseCode = "200",
                                    description = "success|Ok"))
    @GetMapping
    public ResponseEntity<List<AnnouncementResponse>> list() {
        return ResponseEntity.ok(announcementService.list());
    }

    @Operation(summary = "Get", description = "Get an announcement",
                    tags = "Announcement",
                    responses = @ApiResponse(responseCode = "200",
                                    description = "success|Ok"))
    @GetMapping("/{id}")
    public ResponseEntity<AnnouncementResponse> get(@PathVariable("id") Long id) {
        return ResponseEntity.ok(announcementService.get(id));
    }
}
