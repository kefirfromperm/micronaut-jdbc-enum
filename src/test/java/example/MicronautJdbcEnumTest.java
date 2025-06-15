package example;

import example.domain.Issue;
import example.domain.Status;
import example.domain.Tag;
import example.repository.IssueRepository;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import jakarta.inject.Inject;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
class MicronautJdbcEnumTest {

    @Inject
    EmbeddedApplication<?> application;

    @Inject IssueRepository issueRepository;

    @Test
    void testItWorks() {
        Assertions.assertTrue(application.isRunning());
    }

    @Test
    void testIssue() {
        var saved = issueRepository.save(
                new Issue()
                        .setTitle("Test issue")
                        .setStatus(Status.OPEN)
                        .setTags(Set.of(Tag.QA, Tag.BUG))
        );

        var found = issueRepository.findById(saved.getId()).orElseThrow();

        assertEquals(Status.OPEN, found.getStatus());
        assertEquals(2, found.getTags().size());
        assertTrue(found.getTags().contains(Tag.QA));
        assertTrue(found.getTags().contains(Tag.BUG));
    }
}
