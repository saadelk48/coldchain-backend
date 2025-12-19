package ehei.iot.coldChain.repository;

import ehei.iot.coldChain.entity.AppUser;
import ehei.iot.coldChain.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByPhone(String phone);

    Optional<AppUser> findByEmail(String email);
    List<AppUser> findByRoleAndActive(UserRole role, boolean active);

    List<AppUser> findByRoleAndActiveOrderByIdAsc(UserRole role, boolean active);
}
