package ehei.iot.coldChain.dto;

public record RegisterDeviceRequest(
        Long userId,
        String fcmToken
) {}
