package com.example.cgs.controller;

import com.example.cgs.entities.Playlist;
import com.example.cgs.repositories.PlaylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlist")
public class PlaylistController {

    @Autowired
    private PlaylistRepository playlistRepository;

    /**
     * Fetch all videos (playlist) for a specific course.
     *
     * @param courseId The ID of the course.
     * @return A list of videos for the course.
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Playlist>> getPlaylistByCourse(@PathVariable Long courseId) {
        List<Playlist> playlist = playlistRepository.findAllByCourseid(courseId);
        return ResponseEntity.ok(playlist);
    }

    /**
     * Add a new video to the playlist for a course.
     *
     * @param playlist The playlist (video) details.
     * @return The saved playlist object.
     */
    @PostMapping
    public ResponseEntity<Playlist> addVideoToPlaylist(@RequestBody Playlist playlist) {
        Playlist savedPlaylist = playlistRepository.save(playlist);
        return ResponseEntity.ok(savedPlaylist);
    }

    /**
     * Delete a video from the playlist by ID.
     *
     * @param id The ID of the playlist (video).
     * @return A response indicating success or failure.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVideoFromPlaylist(@PathVariable Long id) {
        return playlistRepository.findById(id)
                .map(video -> {
                    playlistRepository.delete(video);
                    return ResponseEntity.ok("Deleted the Playlist");
                })
                .orElse(ResponseEntity.notFound().build());
    }
}