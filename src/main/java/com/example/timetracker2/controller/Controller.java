package com.example.timetracker2.controller;

import com.example.timetracker2.exception.EmptyFileException;
import com.example.timetracker2.exception.FileProcessingException;
import com.example.timetracker2.service.CsvService;
import com.example.timetracker2.service.EmployeeCollaborationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@org.springframework.stereotype.Controller
@AllArgsConstructor
public class Controller {

    private final CsvService csvService;
    private final EmployeeCollaborationService employeeCollaborationService;

    @GetMapping("/")
    public String showHomepage(Model model) {
        return "homepage";
    }


    @PostMapping("/")
    public String csvUpload(@RequestParam("file") MultipartFile file,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        try {
            csvService.readCsvAndSaveData(file.getInputStream(), file.getOriginalFilename());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "homepage";
    }

    @PostMapping("show-pair")
    public String showEmployeePairMostWorkedTogether(Model model,
                                                     RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("employeePair",
                    employeeCollaborationService.findEmployeePairWithMostDaysWorkedTogether());
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to find employee pair: " + e.getMessage());
            return "redirect:/";
        }
        return "show-pair";
    }

}
