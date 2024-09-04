import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    @GetMapping
    public Flux<Event> getAllEvents() {
        return Flux.fromStream(eventRepository.findAll())
                .collectSortedList(this::compareEvents)
                .flatMapMany(Flux::fromIterable);
    }

    private int compareEvents(Event e1, Event e2) {
        int severityCompare = e2.getSeverity().compareTo(e1.getSeverity());
        if (severityCompare != 0) {
            return severityCompare;
        } else {
            return e2.getTimestamp().compareTo(e1.getTimestamp());
        }
    }
}

class Event {
    private LocalDateTime timestamp;
    private String eventText;
    private Severity severity;

    public Event(LocalDateTime timestamp, String eventText, Severity severity) {
        this.timestamp = timestamp;
        this.eventText = eventText;
        this.severity = severity;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getEventText() {
        return eventText;
    }

    public Severity getSeverity() {
        return severity;
    }

    @Override
    public String toString() {
        return "Event{" +
                "timestamp=" + timestamp +
                ", eventText='" + eventText + '\'' +
                ", severity=" + severity +
                '}';
    }
}

enum Severity {
    CRITICAL, MAJOR, MINOR, OK
}

interface EventRepository {
    Stream<Event> findAll();
}