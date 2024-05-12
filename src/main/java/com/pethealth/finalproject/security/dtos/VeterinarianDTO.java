package com.pethealth.finalproject.security.dtos;

import com.pethealth.finalproject.dtos.PetReadDTO;
import com.pethealth.finalproject.model.PetDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class VeterinarianDTO extends UserDTO {
    private String email;
    private List<PetReadDTO> treatedPets;
}
