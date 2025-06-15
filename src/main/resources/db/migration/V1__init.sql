create type status as enum('OPEN', 'CLOSED');
create type tag as enum('BUG', 'QA', 'FRONTEND', 'BACKEND');

create sequence issue_seq;

create table issue (
    id bigint primary key default nextval('issue_seq'),
    title varchar(255) not null,
    status status not null,
    tags tag[] not null
);