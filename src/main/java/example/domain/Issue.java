package example.domain;

import example.converter.TagArrayConverter;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.TypeDef;
import io.micronaut.data.model.DataType;

import java.util.Set;

@Introspected
@MappedEntity
public class Issue {
    @Id @GeneratedValue(GeneratedValue.Type.SEQUENCE) private Long id;
    private String title;
    private Status status;
    private Set<Tag> tags;

    public Long getId() {
        return id;
    }

    public Issue setId(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Issue setTitle(String title) {
        this.title = title;
        return this;
    }

    @TypeDef(type = DataType.OBJECT)
    public Status getStatus() {
        return status;
    }

    public Issue setStatus(Status status) {
        this.status = status;
        return this;
    }

    @TypeDef(
            type = DataType.OBJECT,
            converter = TagArrayConverter.class
    )
    public Set<Tag> getTags() {
        return tags;
    }

    public Issue setTags(Set<Tag> tags) {
        this.tags = tags;
        return this;
    }
}
