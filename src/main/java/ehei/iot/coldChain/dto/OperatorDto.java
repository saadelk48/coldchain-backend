package ehei.iot.coldChain.dto;

import ehei.iot.coldChain.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperatorDto {
    private Long id;
    private String fullName;
    private String email;
    private String role;

    // Constructor that accepts UserRole enum
    public OperatorDto(Long id, String fullName, String email, UserRole role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role != null ? role.name() : null;
    }
}