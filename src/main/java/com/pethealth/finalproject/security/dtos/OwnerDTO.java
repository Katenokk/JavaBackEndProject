package com.pethealth.finalproject.security.dtos;

import com.pethealth.finalproject.dtos.PetReadDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OwnerDTO extends UserDTO {

    private String email;
    private List<PetReadDTO> ownedPets;
}
