package com.jongminkim.demorestapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jongminkim.demorestapi.events.Event;
import com.jongminkim.demorestapi.events.EventRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@RunWith(SpringRunner.class)
@WebMvcTest // 웹과 관련된것만 slicing test라고 부름(구역을 나눠서 테스트하는 거라 조금더 빠름)
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean //WevMvcTest로 테스트 중이기 때문에 Repositroy들은 bean등록이 되지 않아 MockBean으로 만들어줌
    EventRepository eventRepository;

    @Test
    public void createEvent() throws Exception {
        Event event = Event.builder()
                .name("spring")
                .description("REST API Development with spring")
                .beginEventDateTime(LocalDateTime.of(2018, 11, 20, 13, 13))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,25,15,13))
                .endEventDateTime(LocalDateTime.of(2018,12, 26 ,13,13,13))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역  D2 스타텁 팩토리")
                .build();

        event.setId(10);
        Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON) //Hypertext application language
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists());
    }
}
