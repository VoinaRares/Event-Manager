package com.example.event_manager.controller;

import com.example.event_manager.model.User;
import com.example.event_manager.model.Role;
import com.example.event_manager.model.AuditLog;
import com.example.event_manager.repository.UserRepository;
import com.example.event_manager.repository.AuditLogRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserRepository repo;
    private final AuditLogRepository logRepo;

    public AdminController(UserRepository repo, AuditLogRepository logRepo) {
        this.repo = repo; this.logRepo = logRepo;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("userCount", repo.count());
        model.addAttribute("logs", logRepo.findAll());
        return "admin-dashboard";
    }

    @GetMapping("/users")
    public String users(@RequestParam(required=false) String q, Model model) {
        List<User> users = (q != null && !q.isEmpty()) ? repo.findByUsernameContainingIgnoreCase(q) : repo.findAll();
        model.addAttribute("users", users);
        model.addAttribute("roles", Role.values());
        return "admin-users";
    }

    @PostMapping("/users/promote")
    public String promote(@RequestParam Long id, @RequestParam Role role, Authentication auth) {
        User user = repo.findById(id).orElse(null);
        if (user != null) {
            user.setRole(role);
            repo.save(user);
            logRepo.save(new AuditLog(auth.getName(), "promoted user", user.getUsername() + " to " + role, LocalDateTime.now()));
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/demote")
    public String demote(@RequestParam Long id, Authentication auth) {
        User user = repo.findById(id).orElse(null);
        if (user != null) {
            user.setRole(Role.USER);
            repo.save(user);
            logRepo.save(new AuditLog(auth.getName(), "demoted user", user.getUsername(), LocalDateTime.now()));
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete")
    public String delete(@RequestParam Long id, Authentication auth) {
        User user = repo.findById(id).orElse(null);
        if (user != null){
            repo.deleteById(id);
            logRepo.save(new AuditLog(auth.getName(), "deleted user", user.getUsername(), LocalDateTime.now()));
        }
        return "redirect:/admin/users";
    }
}
