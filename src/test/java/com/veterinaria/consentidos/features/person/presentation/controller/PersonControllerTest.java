package com.veterinaria.consentidos.features.person.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.veterinaria.consentidos.features.person.application.command.CreatePersonCommand;
import com.veterinaria.consentidos.features.person.application.command.PersonDto;
import com.veterinaria.consentidos.features.person.application.command.UpdatePersonCommand;
import com.veterinaria.consentidos.features.person.application.usecase.CreatePersonUseCase;
import com.veterinaria.consentidos.features.person.application.usecase.DeletePersonUseCase;
import com.veterinaria.consentidos.features.person.application.usecase.GetPersonUseCase;
import com.veterinaria.consentidos.features.person.application.usecase.UpdatePersonUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

/**
 * Test class for PersonController.
 * Tests the REST API endpoints for person management.
 */
@WebMvcTest(PersonController.class)
@DisplayName("PersonController Tests")
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreatePersonUseCase createPersonUseCase;

    @MockBean
    private GetPersonUseCase getPersonUseCase;

    @MockBean
    private UpdatePersonUseCase updatePersonUseCase;

    @MockBean
    private DeletePersonUseCase deletePersonUseCase;

    private ObjectMapper objectMapper;
    private PersonDto testPersonDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        testPersonDto = new PersonDto(1L, "M", LocalDateTime.of(1990, 1, 15, 0, 0), "Juan", "Perez", "12345678", "CC", "Medellin");
    }

    @Test
    @DisplayName("POST /api/persons - Should create person successfully")
    void testCreatePerson_ValidCommand_ShouldReturnCreated() throws Exception {
        // Given
        CreatePersonCommand command = new CreatePersonCommand("M", LocalDateTime.of(1990, 1, 15, 0, 0), "Juan", "Perez", "12345678", "CC", "Medellin");
        when(createPersonUseCase.execute(any(CreatePersonCommand.class))).thenReturn(testPersonDto);

        // When & Then
        mockMvc.perform(post("/api/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Juan")))
                .andExpect(jsonPath("$.lastName", is("Perez")))
                .andExpect(jsonPath("$.document", is("12345678")));

        verify(createPersonUseCase).execute(any(CreatePersonCommand.class));
    }

    @Test
    @DisplayName("POST /api/persons - Should return bad request for duplicate document")
    void testCreatePerson_DuplicateDocument_ShouldReturnBadRequest() throws Exception {
        // Given
        CreatePersonCommand command = new CreatePersonCommand("M", LocalDateTime.of(1990, 1, 15, 0, 0), "Juan", "Perez", "12345678", "CC", "Medellin");
        when(createPersonUseCase.execute(any(CreatePersonCommand.class)))
                .thenThrow(new IllegalArgumentException("A person with document 12345678 already exists"));

        // When & Then
        mockMvc.perform(post("/api/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("A person with document 12345678 already exists")));

        verify(createPersonUseCase).execute(any(CreatePersonCommand.class));
    }

    @Test
    @DisplayName("GET /api/persons/{id} - Should return person when found")
    void testGetPersonById_PersonExists_ShouldReturnPerson() throws Exception {
        // Given
        when(getPersonUseCase.getById(1L)).thenReturn(testPersonDto);

        // When & Then
        mockMvc.perform(get("/api/persons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Juan")))
                .andExpect(jsonPath("$.lastName", is("Perez")));

        verify(getPersonUseCase).getById(1L);
    }

    @Test
    @DisplayName("GET /api/persons/{id} - Should return not found when person does not exist")
    void testGetPersonById_PersonNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        when(getPersonUseCase.getById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/persons/999"))
                .andExpect(status().isNotFound());

        verify(getPersonUseCase).getById(999L);
    }

    @Test
    @DisplayName("GET /api/persons/document/{document} - Should return person when found by document")
    void testGetPersonByDocument_PersonExists_ShouldReturnPerson() throws Exception {
        // Given
        when(getPersonUseCase.getByDocument("12345678")).thenReturn(testPersonDto);

        // When & Then
        mockMvc.perform(get("/api/persons/document/12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.document", is("12345678")));

        verify(getPersonUseCase).getByDocument("12345678");
    }

    @Test
    @DisplayName("GET /api/persons - Should return all persons")
    void testGetAllPersons_NoFilters_ShouldReturnAllPersons() throws Exception {
        // Given
        PersonDto person2 = new PersonDto(2L, "F", LocalDateTime.of(1985, 6, 20, 0, 0), "Maria", "Gonzalez", "87654321", "TI", "Bogota");
        List<PersonDto> persons = Arrays.asList(testPersonDto, person2);
        when(getPersonUseCase.getAll()).thenReturn(persons);

        // When & Then
        mockMvc.perform(get("/api/persons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is("Juan")))
                .andExpect(jsonPath("$[1].firstName", is("Maria")));

        verify(getPersonUseCase).getAll();
        verify(getPersonUseCase, never()).getByCity(any());
        verify(getPersonUseCase, never()).getByDocumentType(any());
    }

    @Test
    @DisplayName("GET /api/persons?city=Medellin - Should return persons filtered by city")
    void testGetAllPersons_FilterByCity_ShouldReturnFilteredPersons() throws Exception {
        // Given
        List<PersonDto> persons = Arrays.asList(testPersonDto);
        when(getPersonUseCase.getByCity("Medellin")).thenReturn(persons);

        // When & Then
        mockMvc.perform(get("/api/persons").param("city", "Medellin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].city", is("Medellin")));

        verify(getPersonUseCase).getByCity("Medellin");
        verify(getPersonUseCase, never()).getAll();
        verify(getPersonUseCase, never()).getByDocumentType(any());
    }

    @Test
    @DisplayName("GET /api/persons?documentType=CC - Should return persons filtered by document type")
    void testGetAllPersons_FilterByDocumentType_ShouldReturnFilteredPersons() throws Exception {
        // Given
        List<PersonDto> persons = Arrays.asList(testPersonDto);
        when(getPersonUseCase.getByDocumentType("CC")).thenReturn(persons);

        // When & Then
        mockMvc.perform(get("/api/persons").param("documentType", "CC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].documentType", is("CC")));

        verify(getPersonUseCase).getByDocumentType("CC");
        verify(getPersonUseCase, never()).getAll();
        verify(getPersonUseCase, never()).getByCity(any());
    }

    @Test
    @DisplayName("PUT /api/persons/{id} - Should update person successfully")
    void testUpdatePerson_ValidCommand_ShouldReturnUpdatedPerson() throws Exception {
        // Given
        UpdatePersonCommand command = new UpdatePersonCommand(1L, "F", "Juana", "Martinez", "Bogota", "87654321", "TI");
        PersonDto updatedPersonDto = new PersonDto(1L, "F", LocalDateTime.of(1990, 1, 15, 0, 0), "Juana", "Martinez", "87654321", "TI", "Bogota");
        when(updatePersonUseCase.execute(any(UpdatePersonCommand.class))).thenReturn(updatedPersonDto);

        // When & Then
        mockMvc.perform(put("/api/persons/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Juana")))
                .andExpect(jsonPath("$.lastName", is("Martinez")))
                .andExpect(jsonPath("$.city", is("Bogota")));

        verify(updatePersonUseCase).execute(any(UpdatePersonCommand.class));
    }

    @Test
    @DisplayName("PUT /api/persons/{id} - Should return bad request when person not found")
    void testUpdatePerson_PersonNotFound_ShouldReturnBadRequest() throws Exception {
        // Given
        UpdatePersonCommand command = new UpdatePersonCommand(999L, "F", "Test", "User", "City", "12345", "CC");
        when(updatePersonUseCase.execute(any(UpdatePersonCommand.class)))
                .thenThrow(new IllegalArgumentException("Person with ID 999 not found"));

        // When & Then
        mockMvc.perform(put("/api/persons/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Person with ID 999 not found")));

        verify(updatePersonUseCase).execute(any(UpdatePersonCommand.class));
    }

    @Test
    @DisplayName("DELETE /api/persons/{id} - Should delete person successfully")
    void testDeletePerson_PersonExists_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(deletePersonUseCase).execute(1L);

        // When & Then
        mockMvc.perform(delete("/api/persons/1"))
                .andExpect(status().isNoContent());

        verify(deletePersonUseCase).execute(1L);
    }

    @Test
    @DisplayName("DELETE /api/persons/{id} - Should return not found when person does not exist")
    void testDeletePerson_PersonNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        doThrow(new IllegalArgumentException("Person with ID 999 not found"))
                .when(deletePersonUseCase).execute(999L);

        // When & Then
        mockMvc.perform(delete("/api/persons/999"))
                .andExpect(status().isNotFound());

        verify(deletePersonUseCase).execute(999L);
    }

    @Test
    @DisplayName("Should handle unexpected exceptions with internal server error")
    void testCreatePerson_UnexpectedException_ShouldReturnInternalServerError() throws Exception {
        // Given
        CreatePersonCommand command = new CreatePersonCommand("M", LocalDateTime.of(1990, 1, 15, 0, 0), "Juan", "Perez", "12345678", "CC", "Medellin");
        when(createPersonUseCase.execute(any(CreatePersonCommand.class)))
                .thenThrow(new RuntimeException("Database connection error"));

        // When & Then
        mockMvc.perform(post("/api/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("An unexpected error occurred")));

        verify(createPersonUseCase).execute(any(CreatePersonCommand.class));
    }
}