package ehei.iot.coldChain.service.escalation;

import ehei.iot.coldChain.entity.AppUser;
import ehei.iot.coldChain.entity.Incident;

import java.util.List;

public interface IncidentEscalationPolicy {
    List<AppUser> selectRecipients(Incident incident, int alertNumber);
}
