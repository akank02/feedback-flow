package ticket.system.feedbackFlow.dto;

import ticket.system.feedbackFlow.model.User;

public class UserMapper {

    public static UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getIsActive()
        );
    }

    public static AuthResponse toAuthResponse(User user, String token) {
        return new AuthResponse(
                token,
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}