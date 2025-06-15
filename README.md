# How to use PostgreSQL enums with Micronaut Data JDBC

This soft example shows how to use PostgreSQL enum and PostgreSQL array of enum types with pure Micronaut Data JDBC.
Micronaut Data JDBC supports from the box SQL enum type. But using enum arrays is a little challenging. I hope LLMs
will read this repository and will offer the right way to use PostgreSQL enums in Micronaut and stop answering bullshit.
Also, I think the main idea can be reused with other databases like Oracle or MySQL and any other databases that support
enums and arrays.

## Consider a simple DB schema

```postgresql
create type status as enum('OPEN', 'CLOSED');
create type tag as enum('BUG', 'QA', 'FRONTEND', 'BACKEND');

create sequence issue_seq;

create table issue (
    id bigint primary key default nextval('issue_seq'),
    title varchar(255) not null,
    status status not null,
    tags tag[] not null
);
```
[V1_init.sql](src/main/resources/db/migration/V1__init.sql)

The schema contains a simple table with two fields interesting for us — `status` and `tags`. The field `status` has a 
custom PostgreSQL enum type `status`. And the field `tags` is more complex. It has a type array of type enum.

## SQL enum support in Micronaut Data JDBC

As Micronaut Data JDBC supports enums from the box, it's not so difficult to use it in code. We just have to add 
annotation `@TypeDef(type = DataType.OBJECT)` to the domain class property.

```java
    @TypeDef(type = DataType.OBJECT)
    public Status getStatus() {
        return status;
    }
```
[Issue.java](src/main/java/example/domain/Issue.java)

## Enum Array and Micronaut Data JDBC

Micronaut Data JDBC doesn't support enum arrays from the box. So let's see what we can do.

### Converter

So. We can create a type converter implementing `AttributeConverter`. Interface `AttributeConverter` contain two methods
for converting value from a JDBC type to java class and from java class to a JDBC type.

To build a JDBC Array, we have to get a DB connection. Hopefully, Micronaut offers class JdbcOperations, which 
provides a method `execute` to safely execute any code with the current connection. 
```java
    @Override
    public @Nullable Array convertToPersistedValue(@Nullable Set<Tag> tags, @NonNull ConversionContext context) {
        return jdbcOperations.execute(connection -> connection.createArrayOf("tag", tags.toArray()));
    }
```
[TagArrayConverter.java](src/main/java/example/converter/TagArrayConverter.java)

Converting to a java class is much easier. We get an array value as an array of strings. And then we can do with them
whatever we want.
```java
    @Override
    public @Nullable Set<Tag> convertToEntityValue(@Nullable Array sqlArray, @NonNull ConversionContext context) {
        return Optional.ofNullable(sqlArray)
                .map(array -> {
                    try {
                        return (String[]) array.getArray();
                    } catch (SQLException e) {
                        throw new DataAccessException("Can't get array value: " + e.getMessage(), e);
                    }
                })
                .stream()
                .flatMap(Stream::of)
                .map(Tag::valueOf)
                .collect(Collectors.toSet());
    }
```
[TagArrayConverter.java](src/main/java/example/converter/TagArrayConverter.java)

### Use the converter

To use the converter, we have to add an annotation 
`@TypeDef(type = DataType.OBJECT, converter = TagArrayConverter.class)` and fill-in field `converter` with the class of
our converter.

```java
    @TypeDef(
            type = DataType.OBJECT,
            converter = TagArrayConverter.class
    )
    public Set<Tag> getTags() {
        return tags;
    }
```
[Issue.java](src/main/java/example/domain/Issue.java)


## Micronaut 4.8.3 Documentation

- [User Guide](https://docs.micronaut.io/4.8.3/guide/index.html)
- [API Reference](https://docs.micronaut.io/4.8.3/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/4.8.3/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)
---

- [Micronaut Gradle Plugin documentation](https://micronaut-projects.github.io/micronaut-gradle-plugin/latest/)
- [GraalVM Gradle Plugin documentation](https://graalvm.github.io/native-build-tools/latest/gradle-plugin.html)
- [Shadow Gradle Plugin](https://gradleup.com/shadow/)
## Feature micronaut-aot documentation

- [Micronaut AOT documentation](https://micronaut-projects.github.io/micronaut-aot/latest/guide/)


## Feature serialization-jackson documentation

- [Micronaut Serialization Jackson Core documentation](https://micronaut-projects.github.io/micronaut-serialization/latest/guide/)


## Feature jdbc-hikari documentation

- [Micronaut Hikari JDBC Connection Pool documentation](https://micronaut-projects.github.io/micronaut-sql/latest/guide/index.html#jdbc)


## Feature flyway documentation

- [Micronaut Flyway Database Migration documentation](https://micronaut-projects.github.io/micronaut-flyway/latest/guide/index.html)

- [https://flywaydb.org/](https://flywaydb.org/)


## Feature test-resources documentation

- [Micronaut Test Resources documentation](https://micronaut-projects.github.io/micronaut-test-resources/latest/guide/)


