package com.custom.trade.controller;

import com.custom.trade.common.Box;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class Appcontroller {

    @GetMapping("/home")
    public String home(){
        return "ddd";
    }

    @PostMapping("/test1")
    public String test1 (){
        return "HID2";
    }



    @PostMapping("/test2")
    public String test2 (Box box){
        return "HID";
    }

}
