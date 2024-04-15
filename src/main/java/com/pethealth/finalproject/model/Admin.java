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
@DiscriminatorValue("ADMIN")
public class Admin extends User {
    public Admin(String name, String username, String password, Collection<Role> roles) {
        super(name, username, password, roles);
    }
}
