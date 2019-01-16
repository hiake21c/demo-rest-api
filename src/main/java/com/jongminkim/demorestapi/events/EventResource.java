package com.jongminkim.demorestapi.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.jongminkim.demorestapi.EventController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

//public class EventResource extends ResourceSupport {
//
//    @JsonUnwrapped
//    private Event event;
//
//    public EventResource(Event event) {
//        this.event = event;
//    }
//
//    public Event getEvent() {
//        return event;
//    }
//}

/**
 * @JsonUnwrapped를 사용하고 싶지 않다면 Resource<T>를 상속 받아서 사용하면 된다.
 * 상속 받은 클래스에 이미 @JsonUnwrapped가 붙어 있다.
 */
public class EventResource extends Resource<Event> {

    public EventResource(Event event, Link... links) {
        super(event, links);
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    }
}
