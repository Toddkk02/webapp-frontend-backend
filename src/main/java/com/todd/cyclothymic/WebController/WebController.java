package com.todd.cyclothymic.WebController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class WebController {
	@GetMapping("/")
	public String home() {
		return "index.html";
	}
	@GetMapping("/admin")
	public String admin() {
		return "admin.html";
	}
}
