package com.fyen.restfull.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fyen.restfull.entity.Contact;
import com.fyen.restfull.entity.User;
import com.fyen.restfull.model.ContactResponse;
import com.fyen.restfull.model.CreateContactRequest;
import com.fyen.restfull.model.UpdateContactRequest;
import com.fyen.restfull.model.WebResponse;
import com.fyen.restfull.repository.ContactRepository;
import com.fyen.restfull.repository.UserRepository;
import com.fyen.restfull.security.BCrypt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ContactControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private UserRepository userRepository;

  @Autowired private ContactRepository contactRepository;

  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    contactRepository.deleteAll();
    userRepository.deleteAll();

    User user = new User();
    user.setUsername("test");
    user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
    user.setName("Test");
    user.setToken("test");
    user.setTokenExpiredAt(System.currentTimeMillis() + 10000000L);
    userRepository.save(user);
  }

  @Test
  void createContactBadRequest() throws Exception {
    CreateContactRequest request = new CreateContactRequest();
    request.setFirstName("");
    request.setEmail("salah");

    mockMvc
        .perform(
            post("/api/contacts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("X-API-TOKEN", "test"))
        .andExpectAll(status().isBadRequest())
        .andDo(
            result -> {
              WebResponse<String> response =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(),
                      new TypeReference<WebResponse<String>>() {});

              assertNotNull(response.getErrors());
            });
  }

  @Test
  void createContactSuccess() throws Exception {
    CreateContactRequest request = new CreateContactRequest();
    request.setFirstName("Ferry");
    request.setLastName("Gunawan");
    request.setEmail("ferrygun45@gmail.com");
    request.setPhone("081297507252");

    mockMvc
        .perform(
            post("/api/contacts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("X-API-TOKEN", "test"))
        .andExpectAll(status().isOk())
        .andDo(
            result -> {
              WebResponse<ContactResponse> response =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});

              assertNull(response.getErrors());
              assertEquals("Ferry", response.getData().getFirstName());
              assertEquals("Gunawan", response.getData().getLastName());
              assertEquals("ferrygun45@gmail.com", response.getData().getEmail());
              assertEquals("081297507252", response.getData().getPhone());

              assertTrue(contactRepository.existsById(response.getData().getId()));
            });
  }

  @Test
  void getContactNotFound() throws Exception {
    mockMvc
        .perform(
            get("/api/contacts/123123123")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test"))
        .andExpectAll(status().isNotFound())
        .andDo(
            result -> {
              WebResponse<String> response =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(),
                      new TypeReference<WebResponse<String>>() {});

              assertNotNull(response.getErrors());
            });
  }

  @Test
  void getContactSuccess() throws Exception {
    User user = userRepository.findById("test").orElseThrow();

    Contact contact = new Contact();
    contact.setId(UUID.randomUUID().toString());
    contact.setUser(user);
    contact.setFirstName("Ferry");
    contact.setLastName("Gunawan");
    contact.setEmail("ferrygun45@gmail.com");
    contact.setPhone("081297507252");
    contactRepository.save(contact);

    mockMvc
        .perform(
            get("/api/contacts/" + contact.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test"))
        .andExpectAll(status().isOk())
        .andDo(
            result -> {
              WebResponse<ContactResponse> response =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});

              assertNull(response.getErrors());

              assertEquals(contact.getId(), response.getData().getId());
              assertEquals(contact.getFirstName(), response.getData().getFirstName());
              assertEquals(contact.getLastName(), response.getData().getLastName());
              assertEquals(contact.getEmail(), response.getData().getEmail());
              assertEquals(contact.getPhone(), response.getData().getPhone());
            });
  }

  @Test
  void updateContactBadRequest() throws Exception {
    UpdateContactRequest request = new UpdateContactRequest();
    request.setFirstName("");
    request.setEmail("salah");

    mockMvc
        .perform(
            put("/api/contacts/1234")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("X-API" + "-TOKEN", "test"))
        .andExpectAll(status().isBadRequest())
        .andDo(
            result -> {
              WebResponse<String> response =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(),
                      new TypeReference<WebResponse<String>>() {});

              assertNotNull(response.getErrors());
            });
  }

  @Test
  void updateContactSuccess() throws Exception {
    User user = userRepository.findById("test").orElseThrow();

    Contact contact = new Contact();
    contact.setId(UUID.randomUUID().toString());
    contact.setUser(user);
    contact.setFirstName("Ferry");
    contact.setLastName("Gunawan");
    contact.setEmail("ferrygun45@gmail.com");
    contact.setPhone("081297507252");
    contactRepository.save(contact);

    CreateContactRequest request = new CreateContactRequest();
    request.setFirstName("Peter");
    request.setLastName("Parker");
    request.setEmail("peterparker@gmail.com");
    request.setPhone("081208130814");

    mockMvc
        .perform(
            put("/api/contacts/" + contact.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("X-API-TOKEN", "test"))
        .andExpectAll(status().isOk())
        .andDo(
            result -> {
              WebResponse<ContactResponse> response =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});

              assertNull(response.getErrors());
              assertEquals(request.getFirstName(), response.getData().getFirstName());
              assertEquals(request.getLastName(), response.getData().getLastName());
              assertEquals(request.getEmail(), response.getData().getEmail());
              assertEquals(request.getPhone(), response.getData().getPhone());

              assertTrue(contactRepository.existsById(response.getData().getId()));
            });
  }

  @Test
  void deleteContactNotFound() throws Exception {
    mockMvc
        .perform(
            delete("/api/contacts/123123123")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test"))
        .andExpectAll(status().isNotFound())
        .andDo(
            result -> {
              WebResponse<String> response =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(),
                      new TypeReference<WebResponse<String>>() {});

              assertNotNull(response.getErrors());
            });
  }

  @Test
  void deleteContactSuccess() throws Exception {
    User user = userRepository.findById("test").orElseThrow();

    Contact contact = new Contact();
    contact.setId(UUID.randomUUID().toString());
    contact.setUser(user);
    contact.setFirstName("Ferry");
    contact.setLastName("Gunawan");
    contact.setEmail("ferrygun45@gmail.com");
    contact.setPhone("081297507252");
    contactRepository.save(contact);

    mockMvc
        .perform(
            delete("/api/contacts/" + contact.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test"))
        .andExpectAll(status().isOk())
        .andDo(
            result -> {
              WebResponse<String> response =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});

              assertNull(response.getErrors());
              assertEquals("OK", response.getData());
            });
  }

  @Test
  void searchNotFound() throws Exception {
    mockMvc
        .perform(
            get("/api/contacts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test"))
        .andExpectAll(status().isOk())
        .andDo(
            result -> {
              WebResponse<List<ContactResponse>> response =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});

              assertNull(response.getErrors());
              assertEquals(0, response.getData().size());
              assertEquals(0, response.getPaging().getTotalPage());
              assertEquals(0, response.getPaging().getCurrentPage());
              assertEquals(10, response.getPaging().getSize());
            });
  }

  @Test
  void searchSuccess() throws Exception {
    User user = userRepository.findById("test").orElseThrow();

    for (int i = 0; i < 100; i++) {
      Contact contact = new Contact();
      contact.setId(UUID.randomUUID().toString());
      contact.setUser(user);
      contact.setFirstName("Ferry" + i);
      contact.setLastName("Gunawan");
      contact.setEmail("ferrygun45@gmail.com");
      contact.setPhone("081297507252");
      contactRepository.save(contact);
    }

    // Mencari menggunakan First Name
    mockMvc
        .perform(
            get("/api/contacts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
                .queryParam("name", "Ferry") // First Name
            )
        .andExpectAll(status().isOk())
        .andDo(
            result -> {
              WebResponse<List<ContactResponse>> response =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});

              assertNull(response.getErrors());
              assertEquals(10, response.getData().size());
              assertEquals(10, response.getPaging().getTotalPage());
              assertEquals(0, response.getPaging().getCurrentPage());
              assertEquals(10, response.getPaging().getSize());
            });

    // Mencari menggunakan Last Name
    mockMvc
        .perform(
            get("/api/contacts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
                .queryParam("name", "Gunawan") // Last Name
            )
        .andExpectAll(status().isOk())
        .andDo(
            result -> {
              WebResponse<List<ContactResponse>> response =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});

              assertNull(response.getErrors());
              assertEquals(10, response.getData().size());
              assertEquals(10, response.getPaging().getTotalPage());
              assertEquals(0, response.getPaging().getCurrentPage());
              assertEquals(10, response.getPaging().getSize());
            });

    // Mencari menggunakan Email
    mockMvc
        .perform(
            get("/api/contacts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
                .queryParam("email", "gmail.com") // Email
            )
        .andExpectAll(status().isOk())
        .andDo(
            result -> {
              WebResponse<List<ContactResponse>> response =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});

              assertNull(response.getErrors());
              assertEquals(10, response.getData().size());
              assertEquals(10, response.getPaging().getTotalPage());
              assertEquals(0, response.getPaging().getCurrentPage());
              assertEquals(10, response.getPaging().getSize());
            });

    // Mencari menggunakan Phone
    mockMvc
        .perform(
            get("/api/contacts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
                .queryParam("phone", "0812") // Phone
            )
        .andExpectAll(status().isOk())
        .andDo(
            result -> {
              WebResponse<List<ContactResponse>> response =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});

              assertNull(response.getErrors());
              assertEquals(10, response.getData().size());
              assertEquals(10, response.getPaging().getTotalPage());
              assertEquals(0, response.getPaging().getCurrentPage());
              assertEquals(10, response.getPaging().getSize());
            });

    // Mencari menggunakan Phone & Page diatas 100
    mockMvc
        .perform(
            get("/api/contacts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
                .queryParam("phone", "0812") // Phone
                .queryParam("page", "1000") // Page
            )
        .andExpectAll(status().isOk())
        .andDo(
            result -> {
              WebResponse<List<ContactResponse>> response =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});

              assertNull(response.getErrors());
              assertEquals(0, response.getData().size());
              assertEquals(10, response.getPaging().getTotalPage());
              assertEquals(1000, response.getPaging().getCurrentPage());
              assertEquals(10, response.getPaging().getSize());
            });
  }
}
