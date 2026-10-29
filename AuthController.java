package com.freelancehub.controller;
import com.freelancehub.model.User;
import com.freelancehub.repo.UserRepository;
import jakarta.validation.constraints.*;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/auth")
public class AuthController{
 private final UserRepository users; private final BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
 public AuthController(UserRepository u){users=u;}
 record Register(@NotBlank String name,@Email @NotBlank String email,@Size(min=6) String password,User.Role role){}
 record Login(@Email @NotBlank String email,@NotBlank String password){}
 private Map<String,Object> safe(User u){return Map.of("id",u.getId(),"name",u.getName(),"email",u.getEmail(),"role",u.getRole());}
 @PostMapping("/register") public ResponseEntity<?> register(@RequestBody Register r){
  if(r.name()==null||r.name().isBlank()||r.email()==null||!r.email().contains("@")||r.password()==null||r.password().length()<6||r.role()==null)
   return ResponseEntity.badRequest().body(Map.of("error","Enter a name, valid email, password (6+ characters), and role"));
  if(r.role()==User.Role.ADMIN)return ResponseEntity.status(403).body(Map.of("error","Admin accounts cannot be registered publicly"));
  if(users.findByEmail(r.email().trim().toLowerCase()).isPresent())return ResponseEntity.status(409).body(Map.of("error","Email already registered"));
  User u=new User();u.setName(r.name().trim());u.setEmail(r.email().trim().toLowerCase());u.setPasswordHash(encoder.encode(r.password()));u.setRole(r.role());
  return ResponseEntity.status(201).body(safe(users.save(u)));
 }
 @PostMapping("/login") public ResponseEntity<?> login(@RequestBody Login r){
  if(r.email()==null||r.password()==null)return ResponseEntity.badRequest().body(Map.of("error","Email and password are required"));
  return users.findByEmail(r.email().trim().toLowerCase()).filter(u->encoder.matches(r.password(),u.getPasswordHash()))
   .<ResponseEntity<?>>map(u->ResponseEntity.ok(safe(u)))
   .orElseGet(()->ResponseEntity.status(401).body(Map.of("error","Invalid email or password")));
 }
}
