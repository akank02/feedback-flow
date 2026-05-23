package ticket.system.feedbackFlow.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ticket.system.feedbackFlow.enums.Role;
import ticket.system.feedbackFlow.exception.ResourceNotFoundException;
import ticket.system.feedbackFlow.model.User;
import ticket.system.feedbackFlow.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public User registerUser(String name, String email, String password){
        if(userRepository.existsByEmail(email)){
            throw new IllegalStateException("Email already registered: " + email);
        }else{
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPasswordHash(passwordEncoder.encode(password));
            user.setRole(Role.USER);
            user.setIsActive(true);

            return userRepository.save(user);
        }

    }
    public User loginUser(String email, String password){
         User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalStateException("Invalid credentials");
        }

        if (!user.getIsActive()) {
            throw new IllegalStateException("Account is deactivated");
        }

        return user;
    }
}
