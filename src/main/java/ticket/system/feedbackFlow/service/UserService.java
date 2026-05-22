package ticket.system.feedbackFlow.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ticket.system.feedbackFlow.model.User;
import ticket.system.feedbackFlow.repository.UserRepository;

import ticket.system.feedbackFlow.enums.Role;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public User registerUser(String name, String email, String password){
        if(userRepository.existsByEmail(email)){
            throw new RuntimeException("User already exists");
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
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        if (!user.getIsActive()) {
            throw new RuntimeException("Account is deactivated");
        }

        return user;
    }
}
