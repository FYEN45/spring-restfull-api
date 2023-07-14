package com.fyen.restfull.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fyen.restfull.entity.Address;
import com.fyen.restfull.entity.Contact;
import com.fyen.restfull.entity.User;
import com.fyen.restfull.model.AddressResponse;
import com.fyen.restfull.model.CreateAddressRequest;
import com.fyen.restfull.model.UpdateAddressRequest;
import com.fyen.restfull.model.WebResponse;
import com.fyen.restfull.repository.AddressRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AddressControllerTest {

  @Autowired private UserRepository userRepository;

  @Autowired private ContactRepository contactRepository;

  @Autowired private AddressRepository addressRepository;

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    addressRepository.deleteAll();
    contactRepository.deleteAll();
    userRepository.deleteAll();

    User user = new User();
    user.setUsername("test");
    user.setPassword(BCrypt.hashpw("test", BCrypt.gensalt()));
    user.setName("Test");
    user.setToken("test");
    user.setTokenExpiredAt(System.currentTimeMillis() + 10000000L);
    userRepository.save(user);

    Contact contact = new Contact();
    contact.setId("test");
    contact.setUser(user);
    contact.setFirstName("Ferry");
    contact.setLastName("Gunawan");
    contact.setEmail("ferrygun45@gmail.com");
    contact.setPhone("081297507252");
    contactRepository.save(contact);
  }

  @Test
  void createAddressBadRequest() throws Exception {
    CreateAddressRequest request = new CreateAddressRequest();
    request.setCountry("");

    mockMvc
        .perform(
            post("/api/contacts/test/addresses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
                .content(objectMapper.writeValueAsString(request)))
        .andExpectAll(status().isBadRequest())
        .andDo(
            result -> {
              WebResponse<String> response =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});

              assertNotNull(response.getErrors());
            });
  }

  @Test
  void createAddressSuccess() throws Exception {
    CreateAddressRequest request = new CreateAddressRequest();
    request.setStreet("Jalan");
    request.setCity("Kota");
    request.setProvince("Provinsi");
    request.setCountry("Indonesia");
    request.setPostalCode("12345");

    mockMvc
        .perform(
            post("/api/contacts/test/addresses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
                .content(objectMapper.writeValueAsString(request)))
        .andExpectAll(status().isOk())
        .andDo(
            result -> {
              WebResponse<AddressResponse> response =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});

              assertNull(response.getErrors());
              assertEquals(request.getStreet(), response.getData().getStreet());
              assertEquals(request.getCity(), response.getData().getCity());
              assertEquals(request.getProvince(), response.getData().getProvince());
              assertEquals(request.getCountry(), response.getData().getCountry());
              assertEquals(request.getPostalCode(), response.getData().getPostalCode());

              assertTrue(addressRepository.existsById(response.getData().getId()));
            });
  }

  @Test
  void getAddressNotFound() throws Exception {
    mockMvc
        .perform(
            get("/api/contacts/test/addresses/test")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test"))
        .andExpectAll(status().isNotFound())
        .andDo(
            result -> {
              WebResponse<String> response =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});

              assertNotNull(response.getErrors());
            });
  }

  @Test
  void getAddressSuccess() throws Exception {

    Contact contact = contactRepository.findById("test").orElseThrow();

    Address address = new Address();
    address.setId("test");
    address.setContact(contact);
    address.setStreet("Jalan");
    address.setCity("Kota");
    address.setProvince("Provinsi");
    address.setCountry("Indonesia");
    address.setPostalCode("12345");
    addressRepository.save(address);

    mockMvc
        .perform(
            get("/api/contacts/test/addresses/test")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test"))
        .andExpectAll(status().isOk())
        .andDo(
            result -> {
              WebResponse<AddressResponse> response =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});

              assertNull(response.getErrors());
              assertEquals(address.getStreet(), response.getData().getStreet());
              assertEquals(address.getCity(), response.getData().getCity());
              assertEquals(address.getProvince(), response.getData().getProvince());
              assertEquals(address.getCountry(), response.getData().getCountry());
              assertEquals(address.getPostalCode(), response.getData().getPostalCode());

              assertTrue(addressRepository.existsById(response.getData().getId()));
            });
  }

  @Test
  void updateAddressBadRequest() throws Exception {
    UpdateAddressRequest request = new UpdateAddressRequest();
    request.setCountry("");

    mockMvc
        .perform(
            put("/api/contacts/test/addresses/test")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
                .content(objectMapper.writeValueAsString(request)))
        .andExpectAll(status().isBadRequest())
        .andDo(
            result -> {
              WebResponse<String> response =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});

              assertNotNull(response.getErrors());
            });
  }

  @Test
  void updateRequestSuccess() throws Exception {
    Contact contact = contactRepository.findById("test").orElseThrow();

    Address address = new Address();
    address.setId("test");
    address.setContact(contact);
    address.setStreet("Lama");
    address.setCity("Lama");
    address.setProvince("Lama");
    address.setCountry("Lama");
    address.setPostalCode("54321");
    addressRepository.save(address);

    UpdateAddressRequest request = new UpdateAddressRequest();
    request.setStreet("Jalan");
    request.setCity("Kota");
    request.setProvince("Provinsi");
    request.setCountry("Indonesia");
    request.setPostalCode("12345");

    mockMvc
        .perform(
            put("/api/contacts/test/addresses/test")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test")
                .content(objectMapper.writeValueAsString(request)))
        .andExpectAll(status().isOk())
        .andDo(
            result -> {
              WebResponse<AddressResponse> response =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});

              assertNull(response.getErrors());
              assertEquals(request.getStreet(), response.getData().getStreet());
              assertEquals(request.getCity(), response.getData().getCity());
              assertEquals(request.getProvince(), response.getData().getProvince());
              assertEquals(request.getCountry(), response.getData().getCountry());
              assertEquals(request.getPostalCode(), response.getData().getPostalCode());

              assertTrue(addressRepository.existsById(response.getData().getId()));
            });
  }

  @Test
  void deleteAddressNotFound() throws Exception {
    mockMvc
        .perform(
            delete("/api/contacts/test/addresses/test")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test"))
        .andExpectAll(status().isNotFound())
        .andDo(
            result -> {
              WebResponse<String> response =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});

              assertNotNull(response.getErrors());
            });
  }

  @Test
  void deleteAddressSuccess() throws Exception {

    Contact contact = contactRepository.findById("test").orElseThrow();

    Address address = new Address();
    address.setId("test");
    address.setContact(contact);
    address.setStreet("Jalan");
    address.setCity("Kota");
    address.setProvince("Provinsi");
    address.setCountry("Indonesia");
    address.setPostalCode("12345");
    addressRepository.save(address);

    mockMvc
        .perform(
            delete("/api/contacts/test/addresses/test")
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

              assertFalse(addressRepository.existsById("test"));
            });
  }

  @Test
  void listAddressNotFound() throws Exception {
    mockMvc
        .perform(
            get("/api/contacts/salah/addresses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test"))
        .andExpectAll(status().isNotFound())
        .andDo(
            result -> {
              WebResponse<String> response =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});

              assertNotNull(response.getErrors());
            });
  }

  @Test
  void listAddressSuccess() throws Exception {

    Contact contact = contactRepository.findById("test").orElseThrow();

    for (int i = 0; i < 5; i++) {
      Address address = new Address();
      address.setId("test" + i);
      address.setContact(contact);
      address.setStreet("Jalan");
      address.setCity("Kota");
      address.setProvince("Provinsi");
      address.setCountry("Indonesia");
      address.setPostalCode("12345");
      addressRepository.save(address);
    }

    mockMvc
        .perform(
            get("/api/contacts/test/addresses")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "test"))
        .andExpectAll(status().isOk())
        .andDo(
            result -> {
              WebResponse<List<AddressResponse>> responses =
                  objectMapper.readValue(
                      result.getResponse().getContentAsString(), new TypeReference<>() {});

              assertNull(responses.getErrors());
              assertEquals(5, responses.getData().size());
            });
  }
}
