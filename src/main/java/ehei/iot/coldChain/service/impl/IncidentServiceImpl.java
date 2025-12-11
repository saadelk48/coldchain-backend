package ehei.iot.coldChain.service.impl;

import ehei.iot.coldChain.dto.IncidentDetailsResponse;
import ehei.iot.coldChain.entity.*;
import ehei.iot.coldChain.enums.UserRole;
import ehei.iot.coldChain.repository.*;
import ehei.iot.coldChain.service.AlertService;
import ehei.iot.coldChain.service.IncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository incidentRepo;
    private final AppUserRepository userRepo;
    private final IncidentOperatorAckRepository ackRepo;
    private final IncidentCommentRepository commentRepo;
    private final AlertService alertService;

    private static final int ALERTS_PER_OPERATOR = 3;
    private static final double THRESHOLD = 25.0;

    @Override
    public void processTemperature(double temp) {

        // 1️⃣ CHECK IF INCIDENT OPEN
        Incident incident = incidentRepo.findByActiveTrue().orElse(null);

        if (temp >= THRESHOLD) {

            // Start new incident if none exists
            if (incident == null) {
                incident = Incident.builder()
                        .startTime(LocalDateTime.now())
                        .active(true)
                        .alertCount(0)
                        .maxTemperature(temp)
                        .build();

                incidentRepo.save(incident);
            }

            // Update MAX temperature
            if (temp > incident.getMaxTemperature()) {
                incident.setMaxTemperature(temp);
            }

            // ALERT PROCESSING
            processEscalation(incident, temp);

            incidentRepo.save(incident);
        }
        else {
            // TEMPERATURE NORMAL -> AUTO CLOSE INCIDENT
            closeIncidentIfExists(incident);
        }
    }

    private void processEscalation(Incident incident, double temp) {

        incident.setAlertCount(incident.getAlertCount() + 1);

        int alertNumber = incident.getAlertCount();

        List<AppUser> operators = userRepo.findByRoleAndActive(UserRole.OPERATOR, true);
        if (operators.isEmpty()) return;

        // Determine highest operator index allowed
        int operatorIndex = (alertNumber - 1) / ALERTS_PER_OPERATOR;
        if (operatorIndex >= operators.size()) {
            operatorIndex = operators.size() - 1;
        }

        for (int i = 0; i <= operatorIndex; i++) {

            AppUser target = operators.get(i);

            // 🔍 Check if ACK already created for this operator in this incident
            IncidentOperatorAck existingAck =
                    ackRepo.findByIncidentIdAndOperatorId(incident.getId(), target.getId());

            if (existingAck == null) {
                // 👉 FIRST TIME → create ACK + send alert
                System.out.println("📣 Creating ACK & sending alert to " + target.getFullName());

                String message = "⚠️ ColdChain Alert! Temperature reached " + temp + "°C.";
                alertService.sendWhatsappAlert(target.getPhone(), message);

                IncidentOperatorAck ack = IncidentOperatorAck.builder()
                        .incident(incident)
                        .operator(target)
                        .acknowledged(false)
                        .build();

                ackRepo.save(ack);

            } else {
                // 👉 Already exists = DO NOTHING (NO DUPLICATE, NO SPAM)
                System.out.println("⏭ ACK already exists for " + target.getFullName());
            }
        }
    }




    private void closeIncidentIfExists(Incident incident) {
        if (incident != null) {
            incident.setActive(false);
            incident.setEndTime(LocalDateTime.now());
            incidentRepo.save(incident);
        }
    }

    @Override
    public void acknowledgeIncident(Long operatorId) {

        // 1. Get the current active incident
        Incident incident = incidentRepo.findByActiveTrue().orElse(null);
        if (incident == null) return;

        // 2. Get the operator
        AppUser operator = userRepo.findById(operatorId).orElse(null);
        if (operator == null) return;

        // 3. Find existing ACK entry (MUST create this repo method)
        IncidentOperatorAck existingAck =
                ackRepo.findByIncidentIdAndOperatorId(incident.getId(), operatorId);

        if (existingAck != null) {
            // ⚠️ UPDATE the existing ack
            existingAck.setAcknowledged(true);
            existingAck.setAckTime(LocalDateTime.now());
            ackRepo.save(existingAck);
        } else {
            // ⚠️ Otherwise create new only if not exists
            IncidentOperatorAck newAck = IncidentOperatorAck.builder()
                    .incident(incident)
                    .operator(operator)
                    .acknowledged(true)
                    .ackTime(LocalDateTime.now())
                    .build();

            ackRepo.save(newAck);
        }
    }


    @Override
    public void addComment(Long operatorId, String comment) {

        Incident incident = incidentRepo.findByActiveTrue().orElse(null);
        if (incident == null) return;

        AppUser operator = userRepo.findById(operatorId).orElse(null);
        if (operator == null) return;

        IncidentComment c = IncidentComment.builder()
                .incident(incident)
                .operator(operator)
                .message(comment)
                .createdAt(LocalDateTime.now())
                .build();


        commentRepo.save(c);
    }

    @Override
    public IncidentDetailsResponse getIncidentDetails(Long id) {

        Incident incident = incidentRepo.findById(id).orElse(null);
        if (incident == null) return null;

        IncidentDetailsResponse response = new IncidentDetailsResponse();
        response.setId(incident.getId());
        response.setActive(incident.isActive());
        response.setMaxTemperature(incident.getMaxTemperature());
        response.setStartTime(incident.getStartTime());
        response.setEndTime(incident.getEndTime());

        // 🔥 Build Comments List
        List<IncidentDetailsResponse.CommentDTO> commentList =
                incident.getComments()
                        .stream()
                        .map(c -> {
                            IncidentDetailsResponse.CommentDTO dto = new IncidentDetailsResponse.CommentDTO();
                            dto.setOperator(c.getOperator().getFullName());
                            dto.setMessage(c.getMessage());
                            dto.setCreatedAt(c.getCreatedAt());
                            return dto;
                        })
                        .toList();

        // 🔥 Build ACK List
        List<IncidentDetailsResponse.AckDTO> ackList =
                incident.getAcknowledgments()
                        .stream()
                        .map(a -> {
                            IncidentDetailsResponse.AckDTO dto = new IncidentDetailsResponse.AckDTO();
                            dto.setOperator(a.getOperator().getFullName());
                            dto.setAcknowledged(a.isAcknowledged());
                            dto.setAckTime(a.getAckTime());
                            return dto;
                        })
                        .toList();

        response.setComments(commentList);
        response.setAcknowledgments(ackList);

        return response;
    }


    @Override
    public Incident getActiveIncident() {
        return incidentRepo.findByActiveTrue().orElse(null);
    }
}
