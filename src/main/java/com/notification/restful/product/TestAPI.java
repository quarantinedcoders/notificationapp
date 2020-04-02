package com.notification.restful.product;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestAPI {

	@GetMapping
	public String test() {
		return "Hello World Hiren";
	}

}
