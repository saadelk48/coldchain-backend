package ehei.iot.coldChain.repository;

import ehei.iot.coldChain.entity.Ticket;
import ehei.iot.coldChain.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByStatus(TicketStatus status);
}
