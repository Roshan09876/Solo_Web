package com.contact_manager.controller;

import com.contact_manager.repo.UserRepository;
import com.contact_manager.entities.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
public class HomeController {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    //Mapping for HomePage
    @RequestMapping("/")
    public String home(Model model){
        model.addAttribute("title", "Home - Contact Manager");
        return "home";
    }

    //Mapping for AboutPage
    @RequestMapping("/about")
    public String about(Model model){
        model.addAttribute("title","About - Contact Manager");
        return "about";
    }
    //Mapping for SignupPage
    @RequestMapping("/signup")
    public String signup(Model model){
        model.addAttribute("title","Register - Contact Manager");
        model.addAttribute("user", new User());
        return "signup";
    }

    //handler for registering user
//    HttpSession session   -- Use to give message for errors
    @PostMapping(value = "/do_register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult result1,
                               @RequestParam(value = "agreement",
                                       defaultValue = "false")boolean agreement, Model model, HttpSession session){


        try {
 //        This print only shows in console
            if(!agreement){
                System.out.println("You have not agreed the terms and conditions");
                throw new Exception("You have not agreed the terms and conditions");
            }

            if(result1.hasErrors()){
                System.out.println("ERROR " +result1.toString());
                model.addAttribute("user", user);
                model.addAttribute("result1", "Name field is required \n min 2 and max 20 characters are only allowed !!");
                return "signup";
            }

            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            System.out.println("AGREEMENT " +agreement);
            System.out.println("ROSHAN " +user);

//This saves in database
            User result = this.userRepository.save(user);

//        If all the form is correct so giving success message and make all fields empty
            model.addAttribute("user", new User());


            //Sir method to give error message
            model.addAttribute("message", "Successfully Register !!");
            return "signup";

//            Catch is to throw exception/errors
        }catch (Exception e){

            e.printStackTrace();
//Sir method to give error message
            model.addAttribute("errormessage", "You have not mentioned the terms and conditions  !!");
           //            Message is from helper package so do not get confuse
            return "signup";

        }


    }

    //handler for login
    @RequestMapping("/signin")
    public String login(Model model){
        model.addAttribute("title","SignIn Contact Manager");
        return "login";
    }
}
































































//THIS IS ONLY TO TEST WHETHER THIS PROJECT IS WORKING OR NOT

// @Autowired
//    private UserRepository userRepository;
//
//    @GetMapping("/test")
//    @ResponseBody
//    public String test(){
//        User user = new User();
//        user.setName("Roshan Kumar");
//        user.setEmail("kumar@gmail.com");
//
//        Contact contact = new Contact();
//        user.getContacts().add(contact);
//
//        userRepository.save(user);
//        return "working";
//    }