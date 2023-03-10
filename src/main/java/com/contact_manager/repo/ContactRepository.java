package com.contact_manager.repo;

import com.contact_manager.entities.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
//    Implementing method for pagination

    @Query(value = "select * from Contact c where c.user_id =?1",nativeQuery = true)
    public List<Contact> findContactsByUser(Integer userId);


}
