package ehei.iot.coldChain.service;

public interface CommentService {
    void addComment(Long ticketId, Long userId, String message);
}
