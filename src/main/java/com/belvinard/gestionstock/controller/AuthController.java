package com.belvinard.gestionstock.controller;

import com.belvinard.gestionstock.dto.AdresseDTO;
import com.belvinard.gestionstock.dto.EntrepriseDTO;
import com.belvinard.gestionstock.dto.RolesDTO;
import com.belvinard.gestionstock.models.Roles;
import com.belvinard.gestionstock.models.Utilisateur;
import com.belvinard.gestionstock.repositories.RolesRepository;
import com.belvinard.gestionstock.repositories.UtilisateurRepository;
import com.belvinard.gestionstock.security.jwt.JwtUtils;
import com.belvinard.gestionstock.security.request.LoginRequest;
import com.belvinard.gestionstock.security.request.SignupRequest;
import com.belvinard.gestionstock.security.response.LoginResponse;
import com.belvinard.gestionstock.security.response.MessageResponse;
import com.belvinard.gestionstock.security.response.UserInfoResponse;
import com.belvinard.gestionstock.service.UtilisateurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtils jwtUtils;

    private final AuthenticationManager authenticationManager;

    private final UtilisateurRepository utilisateurRepository;

    private final RolesRepository roleRepository;

    private final PasswordEncoder encoder;

    private final UtilisateurService utilisateurService;

    @PostMapping("/public/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails.getUsername());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        LoginResponse response = new LoginResponse(userDetails.getUsername(), roles, jwtToken);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/public/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (utilisateurRepository.existsByUserName(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (utilisateurRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        Utilisateur user = new Utilisateur();
        user.setUserName(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setMoteDePasse(encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Roles role;

        if (strRoles == null || strRoles.isEmpty()) {
            role = roleRepository.findByRoleName("ROLE_SALES_REP")
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        } else {
            String roleStr = strRoles.iterator().next();
            role = roleRepository.findByRoleName("ROLE_" + roleStr.toUpperCase())
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        }

        user.setRole(role);

        utilisateurRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
        Utilisateur user = utilisateurRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse();
        response.setNom(user.getNom());
        response.setPrenom(user.getPrenom());
        response.setEmail(user.getEmail());
        response.setDateDeNaissance(user.getDateDeNaissance());
        if (user.getAdresse() != null) {
            AdresseDTO adresseDTO = new AdresseDTO();
            adresseDTO.setAdresse1(user.getAdresse().getAdresse1());
            adresseDTO.setAdresse2(user.getAdresse().getAdresse2());
            adresseDTO.setVille(user.getAdresse().getVille());
            adresseDTO.setCodePostale(user.getAdresse().getCodePostale());
            adresseDTO.setPays(user.getAdresse().getPays());
            response.setAdresse(adresseDTO);
        }
        response.setPhoto(user.getPhoto());
        if (user.getEntreprise() != null) {
            EntrepriseDTO entrepriseDTO = new EntrepriseDTO();
            entrepriseDTO.setId(user.getEntreprise().getId());
            entrepriseDTO.setNom(user.getEntreprise().getNom());
            entrepriseDTO.setDescription(user.getEntreprise().getDescription());
            entrepriseDTO.setCodeFiscal(user.getEntreprise().getCodeFiscal());
            entrepriseDTO.setPhoto(user.getEntreprise().getPhoto());
            entrepriseDTO.setEmail(user.getEntreprise().getEmail());
            entrepriseDTO.setNumTel(user.getEntreprise().getNumTel());
            entrepriseDTO.setSteWeb(user.getEntreprise().getSteWeb());
            response.setEntreprise(entrepriseDTO);
        }
        if (user.getRole() != null) {
            RolesDTO roleDTO = new RolesDTO();
            roleDTO.setId(user.getRole().getId());
            roleDTO.setRoleName(user.getRole().getRoleName());
            roleDTO.setRoleType(user.getRole().getRoleType());
            response.setRoles(Collections.singletonList(roleDTO));
        }
        response.setActif(user.getActif());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/username")
    public String currentUserName(@AuthenticationPrincipal UserDetails userDetails) {
        return (userDetails != null) ? userDetails.getUsername() : "";
    }
}
