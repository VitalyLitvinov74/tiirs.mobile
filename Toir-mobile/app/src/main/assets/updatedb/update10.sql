drop table if exists 'users';
create table 'users' ('_id' integer not null primary key, 'uuid' text not null unique, 'name' text not null, 'login' text not null, 'pass' text not null, 'type' integer, 'tag_id' text not null unique, 'active' integer not null default 0, 'whois' text not null);
insert into 'users' ('uuid', 'name', 'login', 'pass', 'type', 'tag_id', 'active', 'whois') values('4462ed77-9bf0-4542-b127-f4ecefce49da', 'Иванов О.А.', 'olejek8@yandex.ru', 'admin', 3, '01234567', 1,"Ведущий инженер");
