package ehei.iot.coldChain.controller;

import ehei.iot.coldChain.dto.RegisterDeviceRequest;
import ehei.iot.coldChain.entity.AppUser;
import ehei.iot.coldChain.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final AppUserRepository userRepo;

    @PostMapping("/register")
    public void register(@RequestBody RegisterDeviceRequest req) {

        AppUser user = userRepo.findById(req.userId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFcmToken(req.fcmToken());
        userRepo.save(user);
    }
}
