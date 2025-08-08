package com.belvinard.gestionstock.service.impl;

import com.belvinard.gestionstock.dto.ChangerMotDePasseUtilisateurDTO;
import com.belvinard.gestionstock.dto.RolesDTO;
import com.belvinard.gestionstock.dto.UtilisateurDTO;
import com.belvinard.gestionstock.models.Entreprise;
import com.belvinard.gestionstock.models.RoleType;
import com.belvinard.gestionstock.models.Roles;
import com.belvinard.gestionstock.models.Utilisateur;
import com.belvinard.gestionstock.repositories.EntrepriseRepository;
import com.belvinard.gestionstock.repositories.RolesRepository;
import com.belvinard.gestionstock.repositories.UtilisateurRepository;
import com.belvinard.gestionstock.service.UtilisateurService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final RolesRepository rolesRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UtilisateurDTO save(UtilisateurDTO dto, Long entrepriseId) {
        if (entrepriseId == null) {
            throw new IllegalArgumentException("L'id de l'entreprise ne doit pas être null");
        }

        // Récupération de l'entreprise
        Entreprise entreprise = entrepriseRepository.findById(entrepriseId)
                .orElseThrow(() -> new EntityNotFoundException("Aucune entreprise trouvée avec l'id " + entrepriseId));

        // Mapping DTO -> Entity
        Utilisateur utilisateur = modelMapper.map(dto, Utilisateur.class);
        utilisateur.setEntreprise(entreprise);

        // Encoder le mot de passe avant la sauvegarde
        utilisateur.setMoteDePasse(passwordEncoder.encode(dto.getMoteDePasse()));

        // Sauvegarde de l'utilisateur
        Utilisateur savedUser = utilisateurRepository.save(utilisateur);

        // Assignation du rôle par défaut USER_BASE (peu importe ce que l'utilisateur a
        // demandé)
        assignRole(savedUser.getId(), RoleType.USER_BASE);

        // Récupération de l'utilisateur avec son rôle assigné
        Utilisateur userWithRole = utilisateurRepository.findById(savedUser.getId())
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé après sauvegarde"));

        // Mapping Entity -> DTO avec le rôle
        UtilisateurDTO utilisateurDTO = modelMapper.map(userWithRole, UtilisateurDTO.class);
        utilisateurDTO.setEntrepriseId(entrepriseId); // pour que le champ soit bien renseigné

        // S'assurer que le rôle est bien mappé
        if (userWithRole.getRole() != null) {
            RolesDTO roleDTO = new RolesDTO();
            roleDTO.setId(userWithRole.getRole().getId());
            roleDTO.setRoleName(userWithRole.getRole().getRoleName());
            roleDTO.setRoleType(userWithRole.getRole().getRoleType());
            utilisateurDTO.setRoles(Collections.singletonList(roleDTO));
        }

        return utilisateurDTO;
    }

    @Override
    public UtilisateurDTO findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'identifiant est obligatoire");
        }

        Utilisateur user = utilisateurRepository.findById(id.longValue())
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        // Mapping de base
        UtilisateurDTO dto = modelMapper.map(user, UtilisateurDTO.class);

        // Mapping manuel du rôle
        if (user.getRole() != null) {
            RolesDTO roleDTO = new RolesDTO();
            roleDTO.setId(user.getRole().getId());
            roleDTO.setRoleName(user.getRole().getRoleName());
            roleDTO.setRoleType(user.getRole().getRoleType());
            dto.setRoles(Collections.singletonList(roleDTO));
        }

        // S'assurer que l'entrepriseId est bien mappé
        if (user.getEntreprise() != null) {
            dto.setEntrepriseId(user.getEntreprise().getId());
        }

        return dto;
    }

    @Override
    public List<UtilisateurDTO> findAll() {
        return utilisateurRepository.findAll().stream()
                .map(this::mapUserToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Méthode helper pour mapper un Utilisateur vers UtilisateurDTO avec le rôle
     */
    private UtilisateurDTO mapUserToDTO(Utilisateur user) {
        // Mapping de base
        UtilisateurDTO dto = modelMapper.map(user, UtilisateurDTO.class);

        // Mapping manuel du rôle
        if (user.getRole() != null) {
            RolesDTO roleDTO = new RolesDTO();
            roleDTO.setId(user.getRole().getId());
            roleDTO.setRoleName(user.getRole().getRoleName());
            roleDTO.setRoleType(user.getRole().getRoleType());
            dto.setRoles(Collections.singletonList(roleDTO));
        }

        // S'assurer que l'entrepriseId est bien mappé
        if (user.getEntreprise() != null) {
            dto.setEntrepriseId(user.getEntreprise().getId());
        }

        return dto;
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'identifiant est obligatoire");
        }

        if (!utilisateurRepository.existsById(id.longValue())) {
            throw new EntityNotFoundException("Utilisateur non trouvé");
        }

        utilisateurRepository.deleteById(id.longValue());
    }

    @Override
    public UtilisateurDTO findByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("L'email est obligatoire");
        }

        return utilisateurRepository.findByEmail(email)
                .map(this::mapUserToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Aucun utilisateur avec cet email"));
    }

    @Override
    public UtilisateurDTO changerMotDePasse(ChangerMotDePasseUtilisateurDTO dto) {
        if (dto == null || dto.getId() == null || dto.getMotDePasseActuel() == null
                || dto.getNouveauMotDePasse() == null) {
            throw new IllegalArgumentException("Les informations de changement de mot de passe sont incomplètes");
        }

        Utilisateur utilisateur = utilisateurRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        // Vérifier l'ancien mot de passe avec l'encodeur
        if (!passwordEncoder.matches(dto.getMotDePasseActuel(), utilisateur.getMoteDePasse())) {
            throw new IllegalStateException("Ancien mot de passe incorrect");
        }

        // Encoder le nouveau mot de passe
        utilisateur.setMoteDePasse(passwordEncoder.encode(dto.getNouveauMotDePasse()));
        utilisateur = utilisateurRepository.save(utilisateur);

        return mapUserToDTO(utilisateur);
    }

    @Override
    public UtilisateurDTO findByIdLonge(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'identifiant est obligatoire");
        }

        return utilisateurRepository.findById(id)
                .map(this::mapUserToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
    }

    // === NOUVELLES MÉTHODES ===

    @Override
    public UtilisateurDTO assignRole(Long userId, RoleType roleType) {
        if (userId == null || roleType == null) {
            throw new IllegalArgumentException("L'ID utilisateur et le type de rôle sont obligatoires");
        }

        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        // Chercher ou créer le rôle
        List<Roles> existingRoles = rolesRepository.findByRoleType(roleType);
        Roles role;
        if (!existingRoles.isEmpty()) {
            role = existingRoles.get(0); // Prendre le premier rôle trouvé
        } else {
            // Créer un nouveau rôle
            role = new Roles();
            role.setRoleType(roleType);
            role.setRoleName("ROLE_" + roleType.name());
            role = rolesRepository.save(role);
        }

        // Assigner le rôle à l'utilisateur
        utilisateur.setRole(role);
        Utilisateur savedUser = utilisateurRepository.save(utilisateur);

        // Mapper vers DTO avec le rôle
        UtilisateurDTO dto = modelMapper.map(savedUser, UtilisateurDTO.class);

        // Mapper manuellement le rôle
        if (savedUser.getRole() != null) {
            RolesDTO roleDTO = new RolesDTO();
            roleDTO.setId(savedUser.getRole().getId());
            roleDTO.setRoleName(savedUser.getRole().getRoleName());
            roleDTO.setRoleType(savedUser.getRole().getRoleType());
            dto.setRoles(Collections.singletonList(roleDTO));
        }

        return dto;
    }

    @Override
    public UtilisateurDTO removeRole(Long userId, RoleType roleType) {
        if (userId == null || roleType == null) {
            throw new IllegalArgumentException("L'ID utilisateur et le type de rôle sont obligatoires");
        }

        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        // Vérifier que l'utilisateur a bien ce rôle
        if (utilisateur.getRole() != null && utilisateur.getRole().getRoleType() == roleType) {
            utilisateur.setRole(null); // Supprimer le rôle
            utilisateurRepository.save(utilisateur);
        } else {
            throw new EntityNotFoundException("L'utilisateur ne possède pas ce rôle");
        }

        return mapUserToDTO(utilisateur);
    }

    @Override
    public List<UtilisateurDTO> findByRole(RoleType roleType) {
        if (roleType == null) {
            throw new IllegalArgumentException("Le type de rôle est obligatoire");
        }

        return utilisateurRepository.findByRoleType(roleType).stream()
                .map(this::mapUserToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UtilisateurDTO activateUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("L'ID utilisateur est obligatoire");
        }

        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        utilisateur.setActif(true); // ou le nom de votre méthode setter
        utilisateur = utilisateurRepository.save(utilisateur);

        return mapUserToDTO(utilisateur);
    }

    @Override
    public UtilisateurDTO deactivateUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("L'ID utilisateur est obligatoire");
        }

        Utilisateur utilisateur = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        utilisateur.setActif(false); // ou le nom de votre méthode setter
        utilisateur = utilisateurRepository.save(utilisateur);

        return mapUserToDTO(utilisateur);
    }

    @Override
    public List<UtilisateurDTO> findByEntreprise(Long entrepriseId) {
        if (entrepriseId == null) {
            throw new IllegalArgumentException("L'ID de l'entreprise est obligatoire");
        }

        return utilisateurRepository.findByEntrepriseId(entrepriseId).stream()
                .map(this::mapUserToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UtilisateurDTO> findActiveUsers() {
        return utilisateurRepository.findByActifTrue().stream()
                .map(this::mapUserToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UtilisateurDTO> findInactiveUsers() {
        return utilisateurRepository.findByActifFalse().stream()
                .map(this::mapUserToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UtilisateurDTO> findActiveUsersByEntreprise(Long entrepriseId) {
        if (entrepriseId == null) {
            throw new IllegalArgumentException("L'ID de l'entreprise est obligatoire");
        }

        return utilisateurRepository.findByEntrepriseIdAndActifTrue(entrepriseId).stream()
                .map(this::mapUserToDTO)
                .collect(Collectors.toList());
    }
}