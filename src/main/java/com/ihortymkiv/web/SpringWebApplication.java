package com.ihortymkiv.web;

import com.ihortymkiv.chemistry.Compound;
import com.ihortymkiv.rihn.Rihn;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@SpringBootApplication
@Controller
public class SpringWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringWebApplication.class, args);
    }

    @RequestMapping("/")
    String index(@RequestParam(name= "hydrocarbon", required=false) String hydrocarbon, Model model) {
        if (hydrocarbon != null) {
            model.addAttribute("hydrocarbon", hydrocarbon);
            try {
                Compound compound = Rihn.getCompound(hydrocarbon.toLowerCase());
                model.addAttribute("compoundJSON", CompoundJSONGenerator.generate(compound).toString());
            } catch (Exception e) {
                model.addAttribute("error", e.getMessage());
            }
        }
        return "index";
    }

}
