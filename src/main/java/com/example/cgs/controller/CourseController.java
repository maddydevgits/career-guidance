package com.example.cgs.controller;

import com.example.cgs.entities.Courses;
import com.example.cgs.repositories.CoursesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CoursesRepository coursesRepository;

    /**
     * Fetch all courses.
     *
     * @return List of all courses.
     */
    @GetMapping
    public ResponseEntity<List<Courses>> getAllCourses() {
        List<Courses> courses = coursesRepository.findAll();
        return ResponseEntity.ok(courses);
    }

    /**
     * Fetch a specific course by ID.
     *
     * @param id The ID of the course.
     * @return The course details if found, or a 404 response.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Courses> getCourseById(@PathVariable Long id) {
        return coursesRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Add a new course with its associated skills.
     *
     * @param course The course details with its skills.
     * @return The saved course.
     */
    @PostMapping
    public ResponseEntity<Courses> addCourse(@RequestBody Courses course) {
        Courses savedCourse = coursesRepository.save(course);
        return ResponseEntity.ok(savedCourse);
    }

    /**
     * Update an existing course by ID.
     *
     * @param id     The ID of the course to update.
     * @param course The updated course details.
     * @return The updated course or a 404 response if not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Courses> updateCourse(@PathVariable Long id, @RequestBody Courses course) {
        return coursesRepository.findById(id)
                .map(existingCourse -> {
                    existingCourse.setName(course.getName());
                    existingCourse.setImageUrl(course.getImageUrl());
                    existingCourse.setCoverImage(course.getCoverImage());
                    existingCourse.setTrainerDesignation(course.getTrainerDesignation());
                    existingCourse.setCourseTitle(course.getCourseTitle());
                    existingCourse.setDescription(course.getDescription());
                    existingCourse.setSkills(course.getSkills());
                    Courses updatedCourse = coursesRepository.save(existingCourse);
                    return ResponseEntity.ok(updatedCourse);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a course by ID.
     *
     * @param id The ID of the course to delete.
     * @return A 204 response if deleted successfully, or 404 if not found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable Long id) {
        return coursesRepository.findById(id)
                .map(course -> {
                    coursesRepository.delete(course);
                    return ResponseEntity.ok("deleted the course"); // Explicitly set type to Void
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
