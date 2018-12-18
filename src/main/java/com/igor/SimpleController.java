package com.igor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class SimpleController {

  @RequestMapping("/api/test")
  @GetMapping
  public String hello() {
    System.out.println("hello() endpoint is called");
    return "Hello World";
  }
}
