package ehei.iot.coldChain.service;

import ehei.iot.coldChain.dto.auth.LoginRequest;
import ehei.iot.coldChain.dto.auth.LoginResponse;
import ehei.iot.coldChain.dto.auth.RegisterRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    void register(RegisterRequest request);
}
