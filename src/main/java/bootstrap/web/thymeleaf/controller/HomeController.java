package bootstrap.web.thymeleaf.controller;

import bootstrap.web.thymeleaf.dto.PostDto;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

public class HomeController {
    @GetMapping("/admin/index")
    public String posts(Model model){

        model.addAttribute("index", "home");
        return "/admin/index";
    }
}
