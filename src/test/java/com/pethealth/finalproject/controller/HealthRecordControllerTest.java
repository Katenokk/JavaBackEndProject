package com.pethealth.finalproject.controller;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pethealth.finalproject.dtos.HealthRecordDTO;
import com.pethealth.finalproject.model.*;
import com.pethealth.finalproject.repository.EventRepository;
import com.pethealth.finalproject.repository.HealthRecordRepository;
import com.pethealth.finalproject.repository.PetRepository;
import com.pethealth.finalproject.repository.WeightRepository;
import com.pethealth.finalproject.security.repositories.UserRepository;
import com.pethealth.finalproject.service.PetService;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@Import(TestEntityManager.class)
class HealthRecordControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private PetService petService;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HealthRecordRepository healthRecordRepository;

    @Autowired
    private WeightRepository weightRepository;

    @Autowired
    private EventRepository eventRepository;
    private Cat catto;
    private Dog newDog;
    private Owner owner;

    private Veterinarian oriol;
    private Weight weight1;
    private Weight weight2;
    private HealthRecord healthRecord1;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper.registerModule(new JavaTimeModule());
        owner = new Owner("New Owner", "new-owner", "1234", new ArrayList<>(), "owner@mail.com");
        oriol = new Veterinarian("Oriol", "oriol", "1234", new ArrayList<>(), "owner@mail.com");
        userRepository.save(owner);
        LocalDate dateOfBirth = LocalDate.of(200, 1, 1);
        Date dateOfBirthOld = Date.from(dateOfBirth.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        catto = new Cat("Catto", dateOfBirthOld, false, List.of(CatDiseases.IBD), CatBreeds.BENGAL, owner, oriol);
        userRepository.save(oriol);
        healthRecord1 = new HealthRecord(catto);
//        LocalDate localNow = LocalDate.now();
//        Date now = Date.from(localNow.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        LocalDate localDate1 = LocalDate.of(2024, 1, 5);
        LocalDate localDate2 = LocalDate.of(2024, 1, 20);
        Date date1 = Date.from(localDate1.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        Date date2 = Date.from(localDate2.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        weight1 = new Weight( date1, 10.5, healthRecord1);
        weight2= new Weight( date2, 11.5, healthRecord1);
        healthRecord1.addWeight(weight1);
        healthRecord1.addWeight(weight2);
        catto.setHealthRecord(healthRecord1);
//        healthRecordRepository.save(healthRecord1);
        petRepository.saveAndFlush(catto);

    }

    @AfterEach
    void tearDown() {
        weightRepository.deleteAll();
        petRepository.deleteAll();
        userRepository.deleteAll();
    }
    @Test
    @Transactional
    @WithMockUser(username = "new-owner", authorities = {"ROLE_USER"})
    void testAddWeightToPet() throws Exception {
        catto.getHealthRecord().getWeights().clear();
        weightRepository.deleteAll();
        TestTransaction.flagForCommit(); //para solo tener que comprobar un weight
        Long petId = catto.getId();
        LocalDate localNow = LocalDate.now();
        Date date = Date.from(localNow.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        double weightInKg = 10.0;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = formatter.format(date);

        MvcResult result = mockMvc.perform(post("/health-records/weights/" + petId)
                        .param("date", formattedDate)
                        .param("weightInKg", String.valueOf(weightInKg)))

                .andExpect(status().isOk())
                .andReturn();
        TestTransaction.flagForCommit();
        TestTransaction.end();

        TestTransaction.start();

        Pet pet = petRepository.findById(petId).orElse(null);
        assertNotNull(pet);
        assertNotNull(pet.getHealthRecord());
        assertFalse(pet.getHealthRecord().getWeights().isEmpty());
        assertEquals(weightInKg, pet.getHealthRecord().getWeights().get(0).getWeight());
    }

    @Test
    @WithMockUser(username = "new-owner", authorities = {"ROLE_USER"})
    void testGetPetHealthRecord()  throws Exception {
        Long petId = catto.getId();
        MvcResult result = mockMvc.perform(get("/health-records/" + petId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        HealthRecordDTO healthRecordDTO = objectMapper.readValue(content, HealthRecordDTO.class);

        assertNotNull(healthRecordDTO);
        assertEquals(healthRecord1.getId(), healthRecordDTO.getId());
        assertEquals(healthRecord1.getWeights().size(), healthRecordDTO.getWeights().size());
    }




    @Test
    @WithMockUser(username = "oriol", authorities = {"ROLE_VET"})
    void testFindWeightsBetweenDates_Vet() throws Exception {
        Long petId = catto.getId();
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        Date start = Date.from(startDate.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        Date end = Date.from(endDate.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));

        mockMvc.perform(get("/health-records/weights/" + petId)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].day", is(notNullValue())))
                .andExpect(jsonPath("$[0].weight", is(notNullValue())));

        // Convert the response to a list of Weights
//        String content = result.getResponse().getContentAsString();
//        JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, Weight.class);
//        List<Weight> returnedWeights = objectMapper.readValue(content, type);


//        assertNotNull(returnedWeights);
//        assertFalse(returnedWeights.isEmpty());
//        for (Weight weight : returnedWeights) {
//            assertTrue(weight.getDay().after(start) && weight.getDay().before(end));
//        }
    }

    @Test
    @WithMockUser(username = "new-owner", authorities = {"ROLE_USER"})
    void testFindWeightsBetweenDates_Owner() throws Exception {
        Long petId = catto.getId();
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        Date start = Date.from(startDate.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));
        Date end = Date.from(endDate.atStartOfDay().toInstant(java.time.ZoneOffset.UTC));

        mockMvc.perform(get("/health-records/weights/" + petId)
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[0].day", is(notNullValue())))
                .andExpect(jsonPath("$[0].weight", is(notNullValue())));

    }

    @Test
    @WithMockUser(username = "new-owner", authorities = {"ROLE_USER"})
    void testRemoveWeightFromPet_Valid_Owner() throws Exception {
        Long petId = catto.getId();
        Long weightId = weight1.getId();

        mockMvc.perform(delete("/health-records/weights/" + weightId + "/" + petId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertFalse(weightRepository.existsById(weightId));
        Optional<Pet> pet = petRepository.findByIdAndFetchWeightsEagerly(petId);
        assertTrue(pet.isPresent());

        pet.get().getHealthRecord().getWeights().size();
        assertTrue(pet.get().getHealthRecord().getWeights().stream().noneMatch(weight -> weight.getId().equals(weightId)));
    }

    @Test
    @WithMockUser(username = "oriol", authorities = {"ROLE_VET"})
    void testRemoveWeightFromPet_Invalid_Vet() throws Exception {
        Long petId = catto.getId();
        Long weightId = weight1.getId();

        mockMvc.perform(delete("/health-records/weights/" + weightId + "/" + petId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        assertTrue(weightRepository.existsById(weightId));
        Optional<Pet> pet = petRepository.findByIdAndFetchWeightsEagerly(petId);
        assertTrue(pet.isPresent());

        pet.get().getHealthRecord().getWeights().size();
        assertTrue(pet.get().getHealthRecord().getWeights().stream().anyMatch(weight -> weight.getId().equals(weightId)));
    }

    @Test
//    @Transactional
    void testAddEventToPet_Valid() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2024);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 30);
        Date date1 = calendar.getTime();

        Vomit vomit1 = new Vomit(date1, "Vomit", true, false);

        String body = objectMapper.writeValueAsString(vomit1);
        MvcResult mvcResult = mockMvc.perform(post("/health-records/events/" + catto.getId())
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

//        String content = mvcResult.getResponse().getContentAsString();

        Pet fromRepoCat = petRepository.findByIdAndFetchEventsEagerly(catto.getId()).get();
        HealthRecord fromRepoHealthRecord = fromRepoCat.getHealthRecord();
        Vomit savedVomit = (Vomit) fromRepoHealthRecord.getEvents().get(0);
        //problemas con el formato de las fechas
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedVomit1Date = formatter.format(vomit1.getDate());
        String formattedSavedEventDate = formatter.format(savedVomit.getDate());

        assertEquals(vomit1.getComment(), savedVomit.getComment());
        assertEquals(formattedVomit1Date, formattedSavedEventDate);
        assertEquals(vomit1.isHasFood(), savedVomit.isHasFood());
        assertEquals(vomit1.isHasHairball(), savedVomit.isHasHairball());
    }

    @Test
    void testFindEventsByPet() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2024);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 30);
        Date date2 = calendar.getTime();

        Vomit vomit2 = new Vomit(date2, "Vomit", true, false);
        Fever fever1 = new Fever(date2, "Fever", 39.5);
        eventRepository.save(vomit2);
        eventRepository.save(fever1);
        catto.getHealthRecord().addEvent(vomit2);
        catto.getHealthRecord().addEvent(fever1);
        petRepository.save(catto);

        MvcResult result = mockMvc.perform(get("/health-records/events/" + catto.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Pet fromRepoCat = petRepository.findByIdAndFetchEventsEagerly(catto.getId()).get();
        HealthRecord fromRepoHealthRecord = fromRepoCat.getHealthRecord();
        List<Event> savedEvents = fromRepoHealthRecord.getEvents();

        assertFalse(savedEvents.isEmpty());
        assertTrue(savedEvents.contains(vomit2));
        assertTrue(savedEvents.contains(fever1));
    }

    @Test
    void testUpdateEvent_Valid() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2024);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 30);
        Date date3 = calendar.getTime();

        Vomit vomit3 = new Vomit(date3, "Vomit", true, false);
        eventRepository.save(vomit3);
        catto.getHealthRecord().addEvent(vomit3);
        petRepository.save(catto);

        Vomit newVomit = new Vomit(date3, "updated", false, true);

        String body = objectMapper.writeValueAsString(newVomit);

        mockMvc.perform(put("/health-records/events/" + catto.getId())
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        Optional<Event> fromRepoVomit = eventRepository.findById(vomit3.getId());

        assertTrue(fromRepoVomit.isPresent());
        assertEquals("updated", fromRepoVomit.get().getComment());
        assertEquals(newVomit.isHasHairball(), ((Vomit) fromRepoVomit.get()).isHasHairball());
        assertEquals(newVomit.isHasFood(), ((Vomit) fromRepoVomit.get()).isHasFood());
    }

    @Test
    void testDeleteEvent_Valid() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2024);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 30);
        Date date4 = calendar.getTime();

        Vomit vomit4 = new Vomit(date4, "Vomit", true, false);
        eventRepository.save(vomit4);
        catto.getHealthRecord().addEvent(vomit4);
        petRepository.save(catto);

        //assert that vomit4 was added to pet
        Cat fromRepoCat = (Cat) petRepository.findByIdAndFetchEventsEagerly(catto.getId()).get();
        HealthRecord fromRepoHealthRecord = fromRepoCat.getHealthRecord();
        assertTrue(fromRepoHealthRecord.getEvents().contains(vomit4));

        mockMvc.perform(delete("/health-records/events/" + vomit4.getId())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        Optional<Event> fromRepoEvent = eventRepository.findById(vomit4.getId());
        assertFalse(fromRepoEvent.isPresent());
    }

}