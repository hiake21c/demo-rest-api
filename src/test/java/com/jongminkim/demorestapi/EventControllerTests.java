package com.jongminkim.demorestapi;

import com.jongminkim.demorestapi.accounts.Account;
import com.jongminkim.demorestapi.accounts.AccountRepository;
import com.jongminkim.demorestapi.common.AppProperties;
import com.jongminkim.demorestapi.common.BaseControllerTest;
import com.jongminkim.demorestapi.common.TestDescription;
import com.jongminkim.demorestapi.events.Event;
import com.jongminkim.demorestapi.events.EventDto;
import com.jongminkim.demorestapi.events.EventRepository;
import com.jongminkim.demorestapi.events.EventStatus;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class EventControllerTests extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AppProperties appProperties;

    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("spring")
                .description("REST API Development with spring")
                .beginEventDateTime(LocalDateTime.of(2018, 11, 24, 13, 13))
                .endEventDateTime(LocalDateTime.of(2018,11, 25  ,13,13,13))
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,24,13,13))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,25,13,13))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역  D2 스타텁 팩토리")
                .build();

        mockMvc.perform(post("/api/events/")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON) //Hypertext application language
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8.toString()))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(Matchers.not(EventStatus.DRAFT)))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-event").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-event").description("link to query events"),
                                linkWithRel("update-event").description("link to update an existing event"),
                                linkWithRel("profile").description("link to profile an existing event")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content Type")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new Event"),
                                fieldWithPath("description").description("description of new Event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new Event"),
                                fieldWithPath("endEventDateTime").description("endEventDateTime of close of new Event"),
                                fieldWithPath("beginEnrollmentDateTime").description("enrollmentDateTime of begin of new Event"),
                                fieldWithPath("closeEnrollmentDateTime").description("enrollmentDateTime of close of new Event"),
                                fieldWithPath("location").description("location of new Event"),
                                fieldWithPath("basePrice").description("basePrice of new Event"),
                                fieldWithPath("maxPrice").description("maxPrice of new Event"),
                                fieldWithPath("limitOfEnrollment").description("limit of new Event")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("id").description("id of new Event"),
                                fieldWithPath("name").description("Name of new Event"),
                                fieldWithPath("description").description("description of new Event"),
                                fieldWithPath("beginEventDateTime").description("date time of begin of new Event"),
                                fieldWithPath("endEventDateTime").description("endEventDateTime of close of new Event"),
                                fieldWithPath("beginEnrollmentDateTime").description("enrollmentDateTime of begin of new Event"),
                                fieldWithPath("closeEnrollmentDateTime").description("enrollmentDateTime of close of new Event"),
                                fieldWithPath("location").description("location of new Event"),
                                fieldWithPath("basePrice").description("basePrice of new Event"),
                                fieldWithPath("maxPrice").description("maxPrice of new Event"),
                                fieldWithPath("limitOfEnrollment").description("limit of new Event"),
                                fieldWithPath("free").description("it tells if this event is free"),
                                fieldWithPath("offline").description("it tells if this event is offline"),
                                fieldWithPath("eventStatus").description("event status")
                        )

                ));
    }

    private String getBearerToken() throws Exception {
        return "Bearer " + getAccessToken();
    }

    private String getAccessToken() throws Exception {
        //Given

        ResultActions perform = this.mockMvc.perform(MockMvcRequestBuilders.post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
                .param("username", appProperties.getUserUserName())
                .param("password", appProperties.getUserPassword())
                .param("grant_type", "password"));

        String responseBody = perform.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser parser = new Jackson2JsonParser();
        return parser.parseMap(responseBody).get("access_token").toString();
    }


    @Test
    @TestDescription("입력 받을수 없는 값을 사용한 경우에  에러가 발생하는 테스트")
    public void createEvent_bed_request() throws Exception {
        Event event = Event.builder()
                .name("spring")
                .description("REST API Development with spring")
                .beginEventDateTime(LocalDateTime.of(2018, 12, 24, 13, 13))
                .endEventDateTime(LocalDateTime.of(2018,12, 25  ,13,13,13))
                .beginEnrollmentDateTime(LocalDateTime.of(2018,12,24,13,13))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,12,25,13,13))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역  D2 스타텁 팩토리")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(post("/api/events/")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON) //Hypertext application language
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())

        ;
    }

    /**
     *
     * @throws Exception
     */
    @Test
    @TestDescription("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("spring")
                .description("REST API Development with spring")
                .beginEventDateTime(LocalDateTime.of(2018, 12, 26, 13, 13))
                .endEventDateTime(LocalDateTime.of(2018,12, 24  ,13,13,13))
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,25,15,13))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,24,15,13))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역  D2 스타텁 팩토리")
                .build();

        mockMvc.perform(post("/api/events/")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON) //Hypertext application language
                .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content[0].objectName").exists())
//                .andExpect(jsonPath("$[0].field").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
//                .andExpect(jsonPath("$[0].rejectedValue").exists())

        ;
    }

    @Test
    @TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEvents() throws Exception {
        //Given
        IntStream.range(0, 30).forEach(i -> this.generateEvent(i));

        this.mockMvc.perform(get("/api/events")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "name,DESC")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]_links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"))

        ;

    }

    @Test
    @TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEventsWithAuthentication() throws Exception {
        //Given
        IntStream.range(0, 30).forEach(i -> this.generateEvent(i));

        this.mockMvc.perform(get("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .param("page", "1")
                .param("size", "10")
                .param("sort", "name,DESC")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]_links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.create-event").exists())
                .andDo(document("query-events"))

        ;

    }

    private Event generateEvent(int index) {
        Optional<Account> accountOptional = accountRepository.findByEmail(appProperties.getUserUserName());

        Event event = Event.builder()
                .name("spring")
                .description("REST API Development with spring")
                .beginEventDateTime(LocalDateTime.of(2018, 12, 24, 13, 13))
                .endEventDateTime(LocalDateTime.of(2018,12, 25  ,13,13,13))
                .beginEnrollmentDateTime(LocalDateTime.of(2018,12,24,13,13))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,12,25,13,13))
                .basePrice(100)
                .maxPrice(200)
                .manager(accountOptional.get())
                .limitOfEnrollment(100)
                .location("강남역  D2 스타텁 팩토리")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.DRAFT)
                .build();

        return this.eventRepository.save(event);
    }

    @Test
    @TestDescription("기존의 이벤트를 하나 조회하기")
    public void getEvent() throws Exception {
        //Given
        Event event = this.generateEvent(100);

        //when & then
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-an-event"))

                ;

    }

    @Test
    @TestDescription("없는 이벤트는 조회했을때 404 받기")
    public void getEvent404() throws Exception {
        //when & then
        this.mockMvc.perform(get("/api/events/123123"))
                .andExpect(status().isNotFound())
        ;
    }

    @Test
    @TestDescription("이벤트를 정상적으로 수정하기")
    public void updateEvent() throws Exception {
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        String eventName = "Updated Event";
        eventDto.setName(eventName);

        //When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .content(this.objectMapper.writeValueAsString(eventDto))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("name").value(eventName))
        .andExpect(jsonPath("_links.self").exists())
        .andDo(document("update-event"));
    }

    @Test
    @TestDescription("입력값이 잘못된 경우에 이벤트 수정 실패")
    public void updateEvent400_wrong() throws Exception {
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setBasePrice(10000);

        //When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .content(this.objectMapper.writeValueAsString(eventDto))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isBadRequest())

        ;
    }

    @Test
    @TestDescription("입력값이 비어있는 경우에 이벤트 수정 실패")
    public void updateEven400_Empty() throws Exception {

        Event event = this.generateEvent(200);
        EventDto eventDto = new EventDto();

        //When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .content(this.objectMapper.writeValueAsString(eventDto))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isBadRequest())

        ;
    }

    @Test
    @TestDescription("존재하지 않는 이벤트 수정 실패")
    public void updateEven400_NOTFOUND() throws Exception {
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);

        //When & Then
        this.mockMvc.perform(put("/api/events/12323")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .content(this.objectMapper.writeValueAsString(eventDto))
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isNotFound())

        ;
    }
}

