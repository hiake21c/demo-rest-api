package com.jongminkim.demorestapi.events;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(JUnitParamsRunner.class)
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


    /**
     * @Parameters 이용해서 문자열로 넣어주거나
     * @Parameters(method = "parametersForTestFree") 명시적으로 이렇게 선언 하거나
     * @Parameters 만 붙이고 parametersFor{method이름}으로 Object[]를 return하는 private메소드를 만들어서 사용해도 된다
     */
    @Test
//    @Parameters({
//            "0 , 0, true",
//            "100, 0 , false",
//            "0, 100, false"
//    })
//    @Parameters(method = "paramsForTestFree")
    @Parameters
    public void testFree(int basePrice, int maxPrice, boolean isFree){

        //given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();

        //when
        event.update();

        //then
        assertThat(event.isFree()).isEqualTo(isFree);

    }

    private Object[] parametersForTestFree() {
        return new Object[] {
                new Object[] {0, 0, true},
                new Object[] {100, 0, false},
                new Object[] {0, 100, false},
                new Object[] {100, 200, false},
        };

    }

    @Test
    @Parameters
    public void testOffline(String location, boolean isOffline) {
        //given
        Event event = Event.builder()
                .location(location)
                .build();

        //when
        event.update();

        //then
        assertThat(event.isOffline()).isEqualTo(isOffline);

    }

    private Object[] parametersForTestOffline() {
        return new Object[] {
                new Object[] {"얼바인", true},
                new Object[] {null, false},
                new Object[] {"", false},
        };

    }
}