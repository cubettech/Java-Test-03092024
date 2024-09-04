
mport org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;

@WebFluxTest(controllers = EventController.class)
class EventControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private EventRepository eventRepository;

    @Test
    void testGetAllEvents() {

        Event event1 = new Event(LocalDateTime.now().minusHours(2), "Event 1", Severity.CRITICAL);
        Event event2 = new Event(LocalDateTime.now().minusHours(1), "Event 2", Severity.MAJOR);
        Event event3 = new Event(LocalDateTime.now().minusMinutes(30), "Event 3", Severity.MINOR);
        Event event4 = new Event(LocalDateTime.now(), "Event 4", Severity.OK);


        when(eventRepository.findAll()).thenReturn(Stream.of(event4, event1, event3, event2));


        webTestClient.get().uri("/events")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Event.class)
                .hasSize(4)
                .contains(event1, event2, event3, event4)
                .satisfies(events -> {
                    assertSortedByHighestSeverity(events);
                });
    }

    private void assertSortedByHighestSeverity(Iterable<Event> events) {
        Event prevEvent = null;
        for (Event event : events) {
            if (prevEvent != null) {
                assertTrue(prevEvent.getSeverity().compareTo(event.getSeverity()) >= 0);
                assertTrue(prevEvent.getTimestamp().compareTo(event.getTimestamp()) >= 0);
            }
            prevEvent = event;
        }
    }
}