package com.example.cgs.controller;

import com.example.cgs.entities.Courses;
import com.example.cgs.entities.Skill;
import com.example.cgs.entities.UserProfile;
import com.example.cgs.repositories.CoursesRepository;
import com.example.cgs.repositories.UserProfileRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class RecommendationController {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private CoursesRepository coursesRepository;

    /**
     * Recommends courses based on the user's skills.
     *
     * @param session The HTTP session to retrieve the user's email.
     * @return A list of recommended courses sorted by match score.
     */
    @GetMapping("/api/recommend-courses")
    public ResponseEntity<List<Courses>> recommendCourses(HttpSession session) {
        // Retrieve the userEmail from the session
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        // Fetch the user's profile using the email
        UserProfile user = userProfileRepository.findByEmail(userEmail);

        List<Skill> userSkills = user.getSkills();

        // Fetch all courses
        List<Courses> courses = coursesRepository.findAll();

        // Calculate match scores for each course
        Map<Courses, Integer> courseMatchScores = new HashMap<>();
        for (Courses course : courses) {
            int matchScore = calculateMatchScore(userSkills, course.getSkills());
            courseMatchScores.put(course, matchScore);
        }

        // Filter courses with a minimum match score of 1
        List<Courses> recommendedCourses = courseMatchScores.entrySet().stream()
                .filter(entry -> entry.getValue() > 0) // Only include courses with a match score > 0
                .sorted(Map.Entry.<Courses, Integer>comparingByValue().reversed()) // Sort by match score descending
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return ResponseEntity.ok(recommendedCourses);
    }

    /**
     * Calculates the match score between the user's skills and the course's required skills.
     *
     * @param userSkills   The list of skills the user possesses.
     * @param courseSkills The list of skills required for the course.
     * @return The match score (number of matching skills).
     */
    private int calculateMatchScore(List<Skill> userSkills, List<Skill> courseSkills) {
        int matchScore = 0;

        for (Skill courseSkill : courseSkills) {
            for (Skill userSkill : userSkills) {
                if (courseSkill.getSkill().equalsIgnoreCase(userSkill.getSkill()) &&
                        isSkillLevelMatching(userSkill.getLevel(), courseSkill.getLevel())) {
                    matchScore++;
                    break; // No need to check further if the skill matches
                }
            }
        }

        return matchScore;
    }

    /**
     * Compares the user's skill level with the course's required skill level.
     *
     * @param userLevel   The user's skill level.
     * @param courseLevel The course's required skill level.
     * @return True if the user's skill level meets or exceeds the course's requirement, false otherwise.
     */
    private boolean isSkillLevelMatching(String userLevel, String courseLevel) {
        List<String> levels = Arrays.asList("Beginner", "Intermediate", "Advanced");

        // Ensure valid levels
        if (!levels.contains(userLevel) || !levels.contains(courseLevel)) {
            return false;
        }

        // User's level index must be >= course's level index
        return levels.indexOf(userLevel) >= levels.indexOf(courseLevel);
    }
}