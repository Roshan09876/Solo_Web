package com.contact_manager.controller;

import com.contact_manager.dao.ContactRepository;
import com.contact_manager.dao.UserRepository;
import com.contact_manager.entities.Contact;
import com.contact_manager.entities.User;
import com.contact_manager.helper.Message;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
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

//    To check the old Becrypt Password for changing password
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

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
//            session.setAttribute("message", new Message("Something went Wrong...Please fill all the form", "danger" ));
        }
        return "normal/add_contact_form";
    }

//    Show Contacts Controller
    @GetMapping("/show-contacts")
    public String showContacts(Model m,
                               Principal principal){
//        m.addAttribute("title","User View Contacts");
        //Sending all contacts in this page

        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

        List<Contact> contacts =  this.contactRepository.findContactsByUser(user.getId());

        m.addAttribute("contacts", contacts);
        return "normal/show_contacts";
    }

//    Showing particular contact details
    @RequestMapping("/{cId}/contact")
    public String showContactDetail(@PathVariable("cId") Integer cId,
                                    Model model,
                                    Principal principal){

        System.out.println("CID " + cId);

        Optional<Contact> contactOptional = this.contactRepository.findById(cId);
        Contact contact = contactOptional.get();

//        To show the all the contacts individually to login User
        String userName = principal.getName();
        User user = this.userRepository.getUserByUserName(userName);

        if(user.getId() == contact.getUser().getId()) {

            model.addAttribute("contact", contact);
        }
            return "normal/contact_details";
    }

    //Delete Contact Handler
    @GetMapping("/delete/{cid}")
    public String deleteContact(@PathVariable("cid") Integer cId,
                                Model model,
                                Principal principal,
                                HttpSession session){

        Optional<Contact> contactOptional = this.contactRepository.findById(cId);
//        Contact contact = contactOptional.get();
        System.out.println("CID " +cId);

        Contact contact = this.contactRepository.findById(cId).get();

        User user = this.userRepository.getUserByUserName(principal.getName());

        user.getContacts().remove(contact);
        this.userRepository.save(user);


        //check...
//        System.out.println("Contact "+ contact.getcId());


//        this.contactRepository.delete(contact);


        System.out.println("DELETED");
        session.setAttribute("contact_delete", new Message("You Contact is deleted Successfully", "success"));

        return "redirect:/user/show-contacts";
    }

//    Update Contact Form
    @PostMapping("/update-contact/{cid}")
    public String updateForm(@PathVariable("cid") Integer cid,
                             Model m){

        m.addAttribute("title", "Update Contact");

        Contact contact = this.contactRepository.findById(cid).get();
        m.addAttribute("contact", contact);

        return "normal/update_form";
    }
//    Update contact handler database
    @RequestMapping(value = "/process-update", method = RequestMethod.POST)
    public String updateHandler(@ModelAttribute Contact contact,
                                @RequestParam("profileImage") MultipartFile file,
                                Model m, HttpSession session,
                                Principal principal){

        try{
//            At first fetching Old Contact Details
            Contact oldcontactDetail = this.contactRepository.findById(contact.getcId()).get();

            if(!file.isEmpty()){

//                Delete old Photo
                File deleteFile =  new ClassPathResource("static/img").getFile();
                File file1 = new File(deleteFile, oldcontactDetail.getImage());
                file1.delete();






//                Update new Photo
                File saveFile =  new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator+file.getOriginalFilename());

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                contact.setImage(file.getOriginalFilename());

            }else{
//                If file is empty then inserting old contactDetails in new one
                contact.setImage(oldcontactDetail.getImage());
            }

            User user = this.userRepository.getUserByUserName(principal.getName());

            contact.setUser(user);

            this.contactRepository.save(contact);

            session.setAttribute("contact_update", new Message("Your Contact is Updated" , "success"));



        }catch (Exception e){
            e.printStackTrace();
        }

        System.out.println("Contact Name " + contact.getName());
        System.out.println("Contact Name " + contact.getcId());
        return "redirect:/user/"+contact.getcId()+"/contact";
    }


//    Profile Handler
    @GetMapping("/profile")
    public String yourProfile(Model model, Principal principal){
    

        User existing = userRepository.getUserByUserName(principal.getName());
        User user1 = new User();
        user1.setName(existing.getName());
        user1.setEmail(existing.getEmail());
        user1.setRole(existing.getRole());
        user1.setAbout(existing.getAbout());
        user1.setContacts(existing.getContacts());
        System.out.println(existing);

        model.addAttribute("user", user1);
        return "normal/profile";
    }


    //Open Settings Page
    @GetMapping("/settings")
    public String openSettings(Model model){

        model.addAttribute("title", "Your Settings");
        return "normal/settings";
    }

//    Change Password Handler
    @PostMapping("/change-password")
    public String changepassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 Principal principal,
                                 HttpSession session){

        System.out.println("OLD PASSWORD " + oldPassword);
        System.out.println("NEW PASSWORD " + newPassword);

        String userName = principal.getName();
        User currentUser = this.userRepository.getUserByUserName(userName);
        System.out.println(currentUser.getPassword());

        if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())){
//            Then Change Password
            currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
            this.userRepository.save(currentUser);

            session.setAttribute("password", new Message("Your Password is Successfully Changed..", "success"));

        }else{
//            Error
            session.setAttribute("error", new Message("Please enter your Correct Old Password...", "danger"));
            return "redirect:/user/settings";
        }

        return "redirect:/user/index";
    }
}
