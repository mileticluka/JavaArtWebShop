package hr.algebra.javawebproject.controllers;

import hr.algebra.javawebproject.model.Log;
import hr.algebra.javawebproject.model.dataModel.Artist;
import hr.algebra.javawebproject.repository.ILogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/log")
public class LogController {

    private final ILogRepository logRepository;

    public LogController(ILogRepository logRepository)
    {
        this.logRepository = logRepository;
    }

    @GetMapping("/get")
    public String getAll(Model model) {

        List<Log> logs = logRepository.findAll();

        model.addAttribute("logs",logs );
        return "log/index";
    }
}
