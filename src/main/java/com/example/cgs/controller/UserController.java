package com.example.cgs.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.cgs.DTO.LoginDTO;
import com.example.cgs.DTO.RegisterDTO;
import com.example.cgs.DTO.playlistDTO;
import com.example.cgs.entities.Courses;
import com.example.cgs.entities.JobPosting;
import com.example.cgs.entities.Playlist;
import com.example.cgs.entities.Profile;
import com.example.cgs.entities.Users;
import com.example.cgs.repositories.CoursesRepository;
import com.example.cgs.repositories.JobPostingRepository;
import com.example.cgs.repositories.PlaylistRepository;
import com.example.cgs.repositories.ProfilesRepository;
import com.example.cgs.repositories.UsersRepository;

import jakarta.servlet.http.HttpSession;


@Controller
public class UserController {

	@Autowired
	UsersRepository usersRepository;

	@Autowired
	ProfilesRepository profileRepository;

	@Autowired
	CoursesRepository coursesRepository;

	@Autowired
	PlaylistRepository playlistRepository;
	
	@Autowired
	JobPostingRepository jobPostingRepository;

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/courses")
	public String courses(Model model, HttpSession session) {
		String email = (String) session.getAttribute("userEmail");
		String name = (String) session.getAttribute("name");

		model.addAttribute("name", name);
		model.addAttribute("email", email);
		return "courses";
	}

	@GetMapping("/deletecourse/{id}")
	public String deleteCourse(@PathVariable("id") Long id, Model model) {
		// Find the course by its ID
		Courses course = coursesRepository.findById(id).orElse(null);

		if (course != null) {
			// Delete the course from the database
			coursesRepository.delete(course);
			model.addAttribute("message", "Course deleted successfully");
		} else {
			model.addAttribute("message", "Course not found");
		}

		// Redirect to the course list page or dashboard
		return "redirect:/courses"; // Or another page where the user can see the updated course list
	}

	@GetMapping("/dashboard")
	public String dashboard(HttpSession session,Model model) {
		String name=(String) session.getAttribute("name");
		String email=(String) session.getAttribute("userEmail");
		model.addAttribute("name", name);
		model.addAttribute("email", email);
		return "dashboard";
	}

	@GetMapping("/jobrecommendation")
	public String jobrecommend(Model model,HttpSession session) {
		model.addAttribute("userType",session.getAttribute("userType"));
		System.out.print(session.getAttribute("userType"));
		String name=(String) session.getAttribute("name");
		String email=(String) session.getAttribute("userEmail");
		model.addAttribute("name", name);
		model.addAttribute("email", email);
		return "jobrecommendation";
	}
    // Delete job endpoint
    @PostMapping("/deletejob/{id}")
    public String deleteJob(@PathVariable("id") Long jobId, HttpSession session) {
        
        // Check if the user is an admin
        String userType = (String) session.getAttribute("userType");

        if (userType == null || !userType.equals("admin")) {
            // If the user is not an admin, redirect them to login page
            return "redirect:/login";
        }

        // Perform the delete operation by calling the repository's deleteById method
        try {
            jobPostingRepository.deleteById(jobId);
        } catch (Exception e) {
            // If there was an error deleting the job, log it and redirect to an error page
            return "redirect:/error"; // You can customize the error page
        }

        // Redirect to job listings or any other page after successful deletion
        return "redirect:/jobrecommendation"; // Adjust this URL to your requirement
    }

	@GetMapping("/playlist")
	public String playlist() {
		return "playlist";
	}

	@GetMapping("/skills")
	public String skills(HttpSession session, Model model) {
		String name=(String) session.getAttribute("name");
		String email=(String) session.getAttribute("userEmail");

		model.addAttribute("name", name);
		model.addAttribute("email", email);
		return "skills";
	}

	@GetMapping("/addcourse")
	public String addcourse() {
		return "addCourse";
	}

	@GetMapping("/courseadd")
	public String courseadd(){
		return "courseadd";
	}

	@GetMapping("/coursesview")
	public String coursesview(){
		return "coursesview";
	}

	@GetMapping("/update")
	public String update(HttpSession session, Model model) {
		Profile prf = profileRepository.findByEmail((String) session.getAttribute("userEmail"));

		// Print values of `prf` to the console
		if (prf != null) {
			System.out.println("Profile Details:");
			System.out.println("Name: " + prf.getName());
			System.out.println("Email: " + prf.getEmail());
			System.out.println("Interests: " + prf.getInterests());
			System.out.println("Role: " + prf.getRole());
			System.out.println("Certification: " + prf.getCertification());
			System.out.println("Achievements: " + prf.getAchievements());
		} else {
			System.out.println("No profile found for email: " + session.getAttribute("userEmail"));
		}

		model.addAttribute("profile", prf);
		return "update";
	}

	@GetMapping("/feedback")
	public String feedback(HttpSession session,Model model) {
		String name=(String) session.getAttribute("name");
		String email=(String) session.getAttribute("userEmail");

		model.addAttribute("name", name);
		model.addAttribute("email", email);
		return "feedback";
	}

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody RegisterDTO registerDTO) {

		System.out.print(registerDTO.toString());
		// Check if the email already exists
		if (usersRepository.existsByEmail(registerDTO.getEmail())) {
			return ResponseEntity.badRequest().body("{\"message\":\"Email is already in use\"}");
		}

		// Save the user to the database
		Users user = new Users();
		user.setName(registerDTO.getName());
		user.setEmail(registerDTO.getEmail());
		user.setPassword(registerDTO.getPassword()); // For production, hash the password!
		user.setDob(registerDTO.getDob());

		usersRepository.save(user);

		Profile prf = new Profile();
		prf.setEmail(user.getEmail());
		prf.setName(user.getName());

		profileRepository.save(prf);

		// Return JSON response
		return ResponseEntity.ok("{\"message\":\"User registered successfully\"}");
	}

	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@RequestBody LoginDTO loginDTO, HttpSession session) {


		if (loginDTO.getEmail().equals("admin@gmail.com") && loginDTO.getPassword().equals("1234567890")) {
			System.out.print("hello");
			session.setAttribute("userType", "admin");
			return ResponseEntity.status(HttpStatus.OK).body("{\"message\":\"admin\"}");
		}

		// Fetch the user by email
		Users user = usersRepository.findByEmail(loginDTO.getEmail());

		// Check if the user exists
		if (user == null) {
			// Return 404 (Not Found) if user is not found
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\":\"User not found\"}");
		}

		// Check if the password matches (for production, hash the password and compare
		// it securely)
		if (!user.getPassword().equals(loginDTO.getPassword())) {
			// Return 401 (Unauthorized) if password is incorrect
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\":\"Invalid password\"}");
		}

		// Store the user's email in the session
		session.setAttribute("name", user.getName());
		session.setAttribute("userEmail", user.getEmail());
		session.setAttribute("userType", "student");

		// If login is successful, return a success message with 200 OK status
		return ResponseEntity.status(HttpStatus.OK).body("{\"message\":\"Login successful\"}");
	}

	@GetMapping("/adminDashboard")
	public String adminDash() {
		return "adminDashboard";
	}

	@PostMapping("/addcourse")
	public String addcour(Courses c, Model model) {
		coursesRepository.save(c);
		return "redirect:/courses";
	}

	@GetMapping("/viewplaylist/{id}")
	public String viewPlaylist(@PathVariable("id") Long id, Model model,HttpSession session) {
		// Retrieve the course by ID from the repository
		session.setAttribute("courseid", id);
		Optional<Courses> courseOptional = coursesRepository.findById(id);
		List<Playlist> playlist=playlistRepository.findAllByCourseid(id);

		if (courseOptional.isPresent()) {
			// If course is found, add it to the model
			model.addAttribute("course", courseOptional.get());
			model.addAttribute("playlists",playlist);
			model.addAttribute("userType",session.getAttribute("userType"));
			model.addAttribute("playlistSize",playlist.size() + " videos");
			String name=(String) session.getAttribute("name");
			String email=(String) session.getAttribute("userEmail");
			model.addAttribute("name", name);
			model.addAttribute("email", email);
			return "playlist"; // Return the playlist.html page with the course details
		} else {
			// If course is not found, redirect or show an error page
			model.addAttribute("error", "Course not found.");
			return "error"; // Or a custom error page
		}
	}
	
	@GetMapping("/deleteplaylist/{id}")
	public String deletePlaylist(@PathVariable("id") Long id ,HttpSession session) {
		
		Long cid=(Long)session.getAttribute("courseid");
	    // Your delete logic here, for example:
	    playlistRepository.deleteById(id);
	
	    
	    // Redirect to another page (e.g., playlist list) after deletion
	    return "redirect:/viewplaylist/"+cid;
	}


	@GetMapping("/addvideo/{id}")
	public String addvideo(@PathVariable("id") String id, HttpSession session) {
		session.setAttribute("playlistid", id);
		return "addPlaylist";
	}
	
	@GetMapping("/watchvideo/{id}")
	public String watchvideo(@PathVariable Long id, Model model) {
	    Optional<Playlist> play = playlistRepository.findById(id);
	    
	    if (play.isPresent()) {
	        Playlist playlist = play.get();
	        model.addAttribute("videoUrl", playlist.getVideoUrl());  // Assuming videoUrl is a field
	    } else {
	        model.addAttribute("error", "Playlist not found.");
	    }
	    
	    return "watchvideo";
	}



	@PostMapping("/addtoplaylist")
	public String addToPlaylist(playlistDTO playlist, HttpSession session) {
		// Check if userType in the session is equal to "admin"
		String userType = (String) session.getAttribute("userType");

		if (userType == null || !userType.equals("admin")) {
			// If userType is not "admin", redirect to login page
			return "redirect:/login"; // Redirect to login page if the user is not an admin
		}

		// Retrieve the playlist id as a String and then convert it to Long
		String playlistIdString = (String) session.getAttribute("playlistid");

		if (playlistIdString == null) {
			// Handle case where playlistId is not found in session
			return "redirect:/error"; // Redirect to error page if no playlist ID exists
		}

		Long playlistId = null;
		try {
			playlistId = Long.parseLong(playlistIdString); // Convert String to Long
		} catch (NumberFormatException e) {
			// Handle the case where the string cannot be parsed into a Long
			return "redirect:/error"; // Redirect to error page or handle gracefully
		}

		// Create a new Playlist object
		Playlist play = new Playlist();

		// Set the courseId, title, and coverImage for the Playlist object
		play.setCourseid(playlistId);
		play.setCoverImage(playlist.getCoverImage());
		play.setTitle(playlist.getTitle());
		play.setVideoUrl(playlist.getVideoUrl());

		// Save the new Playlist object to the repository
		playlistRepository.save(play);

		// Redirect to the playlist page with the specific id
		return "redirect:/viewplaylist/" + playlistId; // Corrected the redirect to include the playlist id
	}

	@GetMapping("/addjob")
	public String addjob() {
		return "addjob";
	}
	
	@PostMapping("/addjob")
	public String addjob(JobPosting job,Model model) {
		jobPostingRepository.save(job);
		
		List<JobPosting> jobs= jobPostingRepository.findAll();
		model.addAttribute("jobPostings",jobs);
		return "adminjob";
	}
	
	@GetMapping("/logout")
	public String logoutPage(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}

	@GetMapping("/messagesview")
	public String messagesviewPage(HttpSession session) {
		return "messagesview";
	}

	@GetMapping("/viewjobs")
	public String viewjobsPage(HttpSession session){
		return "viewjobs";
		
	}
	
}
