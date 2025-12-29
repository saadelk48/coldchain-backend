package ehei.iot.coldChain.service.impl;

import ehei.iot.coldChain.dto.ArchivedIncidentSummaryResponse;
import ehei.iot.coldChain.dto.IncidentDetailsResponse;
import ehei.iot.coldChain.dto.OperatorDto;
import ehei.iot.coldChain.entity.AppUser;
import ehei.iot.coldChain.entity.Incident;
import ehei.iot.coldChain.entity.IncidentComment;
import ehei.iot.coldChain.entity.IncidentOperatorAck;
import ehei.iot.coldChain.repository.AppUserRepository;
import ehei.iot.coldChain.repository.IncidentCommentRepository;
import ehei.iot.coldChain.repository.IncidentOperatorAckRepository;
import ehei.iot.coldChain.repository.IncidentRepository;
import ehei.iot.coldChain.service.AlertService;
import ehei.iot.coldChain.service.IncidentService;
import ehei.iot.coldChain.service.escalation.IncidentEscalationPolicy;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncidentServiceImpl implements IncidentService {

    private static final Logger log = LoggerFactory.getLogger(IncidentServiceImpl.class);
    private static final double THRESHOLD = 25.0;

    private final IncidentRepository incidentRepo;
    private final AppUserRepository userRepo;
    private final IncidentOperatorAckRepository ackRepo;
    private final IncidentCommentRepository commentRepo;
    private final AlertService alertService;
    private final IncidentEscalationPolicy escalationPolicy;

    @Override
    public void processTemperature(double temp) {
        Incident incident = incidentRepo.findByActiveTrue().orElse(null);

        if (temp >= THRESHOLD) {
            if (incident == null) {
                incident = Incident.builder()
                        .startTime(LocalDateTime.now())
                        .active(true)
                        .alertCount(0)
                        .maxTemperature(temp)
                        .build();
                incident = incidentRepo.save(incident);
            }

            if (temp > incident.getMaxTemperature()) {
                incident.setMaxTemperature(temp);
            }

            processEscalation(incident, temp);
            incidentRepo.save(incident);
        } else {
            closeIncidentIfExists(incident);
        }
    }

    private void processEscalation(Incident incident, double temp) {
        incident.setAlertCount(incident.getAlertCount() + 1);
        int alertNumber = incident.getAlertCount();

        List<AppUser> recipients = escalationPolicy.selectRecipients(incident, alertNumber);
        if (recipients.isEmpty()) {
            return;
        }

        String message = "ColdChain Alert\nTemperature reached " + temp + " °C";

        // 🔔 Broadcast alerts (fixed destinations)
        alertService.sendEmailAlert(temp);
        alertService.sendTelegramAlert(message);
        alertService.sendWhatsappAlert(message); // ✅ FIXED PHONE (CallMeBot)

        // 👤 Per-operator ACK creation (NO WhatsApp here)
        for (AppUser target : recipients) {
            if (target.getId() == null) {
                continue;
            }

            IncidentOperatorAck existingAck =
                    ackRepo.findByIncidentIdAndOperatorId(incident.getId(), target.getId());

            if (existingAck != null) {
                log.info("ACK already exists for {}", target.getFullName());
                continue;
            }

            log.info("Creating ACK for {}", target.getFullName());

            IncidentOperatorAck ack = IncidentOperatorAck.builder()
                    .incident(incident)
                    .operator(target)
                    .acknowledged(false)
                    .build();

            ackRepo.save(ack);
        }
    }


    private void closeIncidentIfExists(Incident incident) {
        if (incident == null) {
            return;
        }
        incident.setActive(false);
        incident.setEndTime(LocalDateTime.now());
        incidentRepo.save(incident);
    }

    @Override
    public void acknowledgeIncident(Long operatorId) {
        Incident incident = incidentRepo.findByActiveTrue().orElse(null);
        if (incident == null) {
            return;
        }

        AppUser operator = userRepo.findById(operatorId).orElse(null);
        if (operator == null) {
            return;
        }

        IncidentOperatorAck existingAck =
                ackRepo.findByIncidentIdAndOperatorId(incident.getId(), operatorId);

        if (existingAck != null) {
            existingAck.setAcknowledged(true);
            existingAck.setAckTime(LocalDateTime.now());
            ackRepo.save(existingAck);
            return;
        }

        IncidentOperatorAck newAck = IncidentOperatorAck.builder()
                .incident(incident)
                .operator(operator)
                .acknowledged(true)
                .ackTime(LocalDateTime.now())
                .build();

        ackRepo.save(newAck);
    }

    @Override
    public void addComment(Long operatorId, String comment) {
        Incident incident = incidentRepo.findByActiveTrue().orElse(null);
        if (incident == null) {
            return;
        }

        AppUser operator = userRepo.findById(operatorId).orElse(null);
        if (operator == null) {
            return;
        }

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
        if (incident == null) {
            return null;
        }

        IncidentDetailsResponse response = new IncidentDetailsResponse();
        response.setId(incident.getId());
        response.setActive(incident.isActive());
        response.setMaxTemperature(incident.getMaxTemperature());
        response.setStartTime(incident.getStartTime());
        response.setEndTime(incident.getEndTime());

        List<IncidentDetailsResponse.CommentDTO> commentList =
                incident.getComments().stream()
                        .map(c -> {
                            IncidentDetailsResponse.CommentDTO dto =
                                    new IncidentDetailsResponse.CommentDTO();

                            dto.setOperator(
                                    new OperatorDto(
                                            c.getOperator().getId(),
                                            c.getOperator().getFullName(),
                                            c.getOperator().getEmail(),
                                            c.getOperator().getRole()
                                    )
                            );

                            dto.setMessage(c.getMessage());
                            dto.setCreatedAt(c.getCreatedAt());
                            return dto;
                        })
                        .toList();

        List<IncidentDetailsResponse.AckDTO> ackList =
                incident.getAcknowledgments().stream()
                        .map(a -> {
                            IncidentDetailsResponse.AckDTO dto =
                                    new IncidentDetailsResponse.AckDTO();

                            dto.setOperator(
                                    new OperatorDto(
                                            a.getOperator().getId(),
                                            a.getOperator().getFullName(),
                                            a.getOperator().getEmail(),
                                            a.getOperator().getRole()
                                    )
                            );

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


    @Override
    public List<ArchivedIncidentSummaryResponse> getArchivedIncidents() {
        return incidentRepo.findByActiveFalseOrderByEndTimeDesc()
                .stream()
                .map(incident -> ArchivedIncidentSummaryResponse.builder()
                        .id(incident.getId())
                        .startTime(incident.getStartTime())
                        .endTime(incident.getEndTime())
                        .alertCount(incident.getAlertCount())
                        .maxTemperature(incident.getMaxTemperature())
                        .build()
                )
                .toList();
    }

    @Override
    public IncidentDetailsResponse getArchivedIncidentDetails(Long incidentId) {
        Incident incident = incidentRepo.findById(incidentId).orElse(null);

        if (incident == null || incident.isActive()) {
            return null;
        }

        return getIncidentDetails(incidentId);
    }

}
