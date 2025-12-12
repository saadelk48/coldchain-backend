package ehei.iot.coldChain.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import ehei.iot.coldChain.entity.AppUser;
import ehei.iot.coldChain.repository.AppUserRepository;
import ehei.iot.coldChain.service.PushService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class PushTestController {

    private final AppUserRepository userRepo;
    private final PushService pushService;

    @PostMapping("/push/{userId}")
    public String sendTestPush(@PathVariable Long userId) throws FirebaseMessagingException {

        AppUser user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getFcmToken() == null) {
            return "User has no FCM token";
        }

        pushService.sendCriticalAlert(
                user.getFcmToken(),
                "ColdChain TEST",
                "This is a test alert from backend",
                0L
        );

        return "Push sent";
    }
}
