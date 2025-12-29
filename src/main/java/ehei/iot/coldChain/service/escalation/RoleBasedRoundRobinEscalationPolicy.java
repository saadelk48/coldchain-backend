package ehei.iot.coldChain.service.escalation;

import ehei.iot.coldChain.entity.AppUser;
import ehei.iot.coldChain.entity.Incident;
import ehei.iot.coldChain.enums.UserRole;
import ehei.iot.coldChain.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleBasedRoundRobinEscalationPolicy implements IncidentEscalationPolicy {

    private static final int ALERTS_PER_STAGE = 3;
    private static final List<UserRole> ESCALATION_ORDER = List.of(
            UserRole.OPERATOR,
            UserRole.SUPERVISOR,
            UserRole.DIRECTOR
    );

    private final AppUserRepository userRepository;

    @Override
    public List<AppUser> selectRecipients(Incident incident, int alertNumber) {
        if (alertNumber <= 0) {
            return List.of();
        }

        int stage = Math.min((alertNumber - 1) / ALERTS_PER_STAGE, ESCALATION_ORDER.size() - 1);
        long incidentSeed = incident.getId() == null ? 0 : incident.getId();
        int group = (alertNumber - 1) / ALERTS_PER_STAGE;

        List<AppUser> recipients = new ArrayList<>();
        for (int i = 0; i <= stage; i++) {
            UserRole role = ESCALATION_ORDER.get(i);
            List<AppUser> users = userRepository.findByRoleAndActiveOrderByIdAsc(role, true);
            if (users.isEmpty()) {
                continue;
            }
            int index = Math.floorMod((int) (incidentSeed + group + i), users.size());
            recipients.add(users.get(index));
        }
        return recipients;
    }
}
