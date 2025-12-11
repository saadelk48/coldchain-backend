package ehei.iot.coldChain.repository;

import ehei.iot.coldChain.entity.AppUser;
import ehei.iot.coldChain.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    List<AppUser> findByRoleAndActive(UserRole role, boolean active);
}
