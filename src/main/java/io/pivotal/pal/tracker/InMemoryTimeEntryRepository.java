package io.pivotal.pal.tracker;

import org.springframework.http.ResponseEntity;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;


public class InMemoryTimeEntryRepository implements TimeEntryRepository{

    private List<TimeEntry> m_Data = new ArrayList<>();

    public TimeEntry create(TimeEntry timeEntry) {
        long max = m_Data.stream().mapToLong(c -> c.getId()).max().orElse(0l);
        timeEntry.setId(max+1);
        m_Data.add(timeEntry);
        return timeEntry;
    }

    public TimeEntry find(long id) {
        return m_Data.stream().filter( c -> (id == c.getId())).findAny().orElse(null);
    }

    public List<TimeEntry> list() {
        return m_Data;
    }

    public TimeEntry update(long id, TimeEntry timeEntry) {
        TimeEntry existing = m_Data.stream().filter(c->c.getId() == id).findAny().get();
        if (existing != null) {
            existing.setDate(timeEntry.getDate());
            existing.setHours(timeEntry.getHours());
            existing.setProjectId(timeEntry.getProjectId());
            existing.setUserId(timeEntry.getUserId());
        }
        return existing;
    }

    public TimeEntry delete(long id) {
        TimeEntry entryToRemove = m_Data.stream().filter( c -> (id == c.getId())).findAny().get();
        if (entryToRemove != null) {
            m_Data.remove(entryToRemove);
        }
        return entryToRemove;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryTimeEntryRepository that = (InMemoryTimeEntryRepository) o;
        return Objects.equals(m_Data, that.m_Data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_Data);
    }
}
