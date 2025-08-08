package com.zain.backend.controller;

import com.zain.backend.dto.BugDTO;
import com.zain.backend.service.BugService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bugs")
public class BugController {
    private final BugService bugService;
    public BugController(BugService bugService) { this.bugService = bugService; }

    @PostMapping
    public ResponseEntity<BugDTO> create(@RequestBody BugDTO dto) {
        return ResponseEntity.ok(bugService.createBug(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BugDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(bugService.getBugById(id));
    }

    @GetMapping
    public ResponseEntity<List<BugDTO>> list() {
        return ResponseEntity.ok(bugService.getAllBugs());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<BugDTO> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(bugService.updateStatus(id, status));
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<BugDTO> assign(@PathVariable Long id, @RequestParam Long userId) {
        return ResponseEntity.ok(bugService.assignBug(id, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bugService.deleteBug(id);
        return ResponseEntity.noContent().build();
    }
}
