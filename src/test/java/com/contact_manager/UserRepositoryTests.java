package com.contact_manager;


import com.contact_manager.dao.UserRepository;
import com.contact_manager.entities.User;
//import org.junit.jupiter.api.Assertions;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;


    @Test
    public void saveUserTest(){

        User user = new User();
        user.setName("Roshan");
        user.setEmail("khadkaroshan715@gmail.com");
        user.setRole("user");
        user.setImageUrl("test.jpg");
        user.setAbout("My name is Roshan Kumar Khadka");


//        User user = User.builder().build();

        userRepository.save(user);
        Assertions.assertThat(user.getId()).isGreaterThan(0);
    }
}
