package ehei.iot.coldChain.entity;

import ehei.iot.coldChain.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String email;
    private String phone;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private boolean active;
}