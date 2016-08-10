drop table if exists 'users_new';
create table 'users_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'name' text not null, 'login' text not null, 'pass' text not null, 'type' integer, 'tag_id' text not null unique, 'active' integer not null default 0, 'whois' text not null, 'image' text);
insert into 'users_new' ('uuid', 'name', 'login', 'pass', 'type', 'tag_id', 'active', 'whois') select uuid, name, login, pass, type, tag_id, active, whois from 'users';
drop table if exists 'users';
alter table 'users_new' rename to 'users';

