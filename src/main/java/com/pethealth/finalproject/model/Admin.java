package com.pethealth.finalproject.model;

import com.pethealth.finalproject.security.models.Role;
import com.pethealth.finalproject.security.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Entity
@Data
@NoArgsConstructor
//@AllArgsConstructor
@DiscriminatorValue("ADMIN")
public class Admin extends User {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    public Admin(Long id, String name, String username, String password, Collection<Role> roles) {
        super(id, name, username, password, roles);
    }
}
