package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.model.UserProgress;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserProgressRepository;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final UserRepository userRepository;
    private final UserProgressRepository userProgressRepository;

    public ProgressController(UserRepository userRepository, UserProgressRepository userProgressRepository) {
        this.userRepository = userRepository;
        this.userProgressRepository = userProgressRepository;
    }

    @PostMapping
    public UserProgress saveProgress(@RequestBody Map<String, Object> payload) {
        String username = (String) payload.get("username");
        Long lessonId = ((Number) payload.get("lessonId")).longValue();
        boolean completed = (Boolean) payload.get("completed");

        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Check if progress already exists
        List<UserProgress> existingProgress = userProgressRepository.findByUserId(user.getId());
        UserProgress progress = existingProgress.stream()
                .filter(p -> p.getLessonId().equals(lessonId))
                .findFirst()
                .orElse(null);

        if (progress == null) {
            progress = new UserProgress();
            progress.setUser(user);
            progress.setLessonId(lessonId);
        }

        progress.setCompleted(completed);
        progress.setCompletionDate(LocalDateTime.now());
        progress.setPointsEarned(10); // default points

        // Also update user points if completed and not already awarded
        if (completed) {
            // Check if they were already awarded points for this lesson
            boolean alreadyCompleted = existingProgress.stream()
                    .anyMatch(p -> p.getLessonId().equals(lessonId) && p.isCompleted());
            if (!alreadyCompleted) {
                user.setTotalPoints(user.getTotalPoints() + 10);
                userRepository.save(user);
            }
        }

        return userProgressRepository.save(progress);
    }

    @GetMapping("/{username}")
    public List<UserProgress> getProgress(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return Collections.emptyList();
        }
        return userProgressRepository.findByUserId(user.getId());
    }
}
