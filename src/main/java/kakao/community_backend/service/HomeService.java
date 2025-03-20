package kakao.community_backend.service;

import org.springframework.stereotype.Service;

@Service
public class HomeService {
    public String getWelcomeMessage() {
        return "Welcome to our Community Service!";
    }
}
