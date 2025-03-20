package kakao.community_backend.controller;

import kakao.community_backend.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    private final HomeService homeService;

    @Autowired
    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping("/api/hello")
    public String hello() {
        return "Hello! Welcome to Kakao Community Service!";
    }
}
