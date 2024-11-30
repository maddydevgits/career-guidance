package com.example.cgs.controller;

import com.example.cgs.entities.Message;
import com.example.cgs.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    /**
     * Store a message.
     *
     * @param message The message payload from the client.
     * @return The saved message object.
     */
    @PostMapping
    public ResponseEntity<String> saveMessage(@ModelAttribute Message message, RedirectAttributes redirectAttributes) {
        // Save the message
        messageRepository.save(message);

        // Add a success message
        redirectAttributes.addFlashAttribute("successMessage", "Message sent successfully!");

        // Redirect back to the form page
        return ResponseEntity.ok("message stored");
    }

    /**
     * Retrieve all messages.
     *
     * @return A list of all stored messages.
     */
    @GetMapping
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageRepository.findAll();
        return ResponseEntity.ok(messages);
    }
}