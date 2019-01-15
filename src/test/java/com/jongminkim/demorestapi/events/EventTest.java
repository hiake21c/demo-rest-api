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
}