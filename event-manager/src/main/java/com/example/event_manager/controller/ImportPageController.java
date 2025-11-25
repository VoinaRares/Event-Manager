package com.example.event_manager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ImportPageController {

    @GetMapping("/import-form")
    public String showImportForm() {
        return "import_form";
    }

}
