package com.jongminkim.demorestapi.events;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class EventTest {

    @Test
    public void builder() {
        Event event = Event.builder()
                .name("Spring REST API")
                .description("REST API TEST")
                .build();

        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean() {
        Event event = new Event();
        String event1 = "Event";
        event.setName(event1);
        String desc = "Spring";
        event.setDescription(desc);

        assertThat(event.getName()).isEqualTo(event1);
        assertThat(event.getDescription()).isEqualTo(desc);
    }

    @Test
    public void testFree(){

        //given
        Event event = Event.builder()
                .basePrice(0)
                .maxPrice(0)
                .build();

        //when
        event.update();

        //then
        assertThat(event.isFree()).isTrue();

        //given
        event = Event.builder()
                .basePrice(100)
                .maxPrice(0)
                .build();

        //when
        event.update();

        //then
        assertThat(event.isFree()).isFalse();

        //given
        event = Event.builder()
                .basePrice(0)
                .maxPrice(100)
                .build();

        //when
        event.update();

        //then
        assertThat(event.isFree()).isFalse();
    }

    @Test
    public void testOffline() {
        //given
        Event event = Event.builder()
                .location("카카오 게임즈 얼바인")
                .build();

        //when
        event.update();

        //then
        assertThat(event.isOffline()).isTrue();


        //givine
        event = Event.builder()
                .build();

        //when
        event.update();

        //then
        assertThat(event.isOffline()).isFalse();


    }
}