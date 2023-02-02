package com.contact_manager.controller;

import com.contact_manager.dao.ContactRepository;
import com.contact_manager.dao.UserRepository;
import com.contact_manager.entities.Contact;
import com.contact_manager.entities.User;
import com.contact_manager.helper.Message;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

//    Creating method for all
    @ModelAttribute
    public void addCommonData(Model model, Principal principal){
        String userName = principal.getName();
        System.out.println("USERNAME "+userName);

//        get the user using username(Email)
        User user = userRepository.getUserByUserName(userName);
        System.out.println("User " +user);
        model.addAttribute("user ", user);
    }


    @RequestMapping("/index")
//    principal is uses to take userId from login to dashboard page

    public String dashboard(Model model, Principal principal){
        model.addAttribute("title", "User Private DashBoard");
        return "normal/user_dashboard";
    }

    //Making form handler
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model){
        model.addAttribute("title","Add Contact");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact_form";
    }

    //processing add contact form
    @PostMapping("/process-contact")
    public String processContact(
            @ModelAttribute Contact contact,
            @RequestParam("profileImage") MultipartFile file,
            Principal principal,
            HttpSession session){

        try {
            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);


//            processing and uploading file
            if(file.isEmpty()){
                System.out.println("File is Empty");
                contact.setImage("contactlogo.png");
            }
            else{
//                Upload the file to folder and update the name to contact
                contact.setImage(file.getOriginalFilename());

//                making path for the uploaded image
               File saveFile =  new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator+file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Image Uploaded Successfully");
            }


//        passing contact to user
            contact.setUser(user);

//        At first list will come and in that list contact will be added
            user.getContacts().add(contact);

//            Saves the file
            this.userRepository.save(user);

            System.out.println("DATA " + contact);
            System.out.println("Added to Database");

//            message success
            session.setAttribute("message", new Message("Your Contact is Added Successfully...Add more", "success" ));



        }catch (Exception e){
            System.out.println("ERROR " +e.getMessage());
//            for detail Exception
            e.printStackTrace();

            //error message
            session.setAttribute("message", new Message("Something went Wrong...Please fill all the form", "danger" ));
        }
        return "normal/add_contact_form";
    }

//    Show Contacts Controller
    @GetMapping("/show-contacts")
    public String showContacts(Model m, Principal principal){
        m.addAttribute("title","User View Contacts");
        //Sending all contacts in this page

        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

        List<Contact> contacts =  this.contactRepository.findContactsByUser(user.getId());

        m.addAttribute("contacts", contacts);
        return "normal/show_contacts";
    }

//    Showing particular contact details
    @RequestMapping("/{cId}/contact")
    public String showContactDetail(@PathVariable("cId") Integer cId, Model model){

        System.out.println("CID " + cId);

        Optional<Contact> contactOptional = this.contactRepository.findById(cId);
        Contact contact = contactOptional.get();

        model.addAttribute("contact", contact);

        return "normal/contact_details";
    }

}
