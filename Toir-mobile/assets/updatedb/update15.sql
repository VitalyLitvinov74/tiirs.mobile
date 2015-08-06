drop table if exists 'token_new';
create table 'token_new' ('token_type' text not null, 'access_token' text not null, 'expires_in' integer not null, 'username' text not null unique, '.issued' text not null, '.expires' text not null);
insert into 'token_new' ('token_type', 'access_token', 'expires_in', 'username', '.issued', '.expires') select 'bearer' as 'token_type', 'access_token', 'expires_in', 'username', '.issued', '.expires' from 'token';

drop table if exists 'token';
alter table 'token_new' rename to 'token';

drop table if exists 'equipment_new';
create table 'equipment_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'title' text not null, 'equipment_type_uuid' text not null, 'critical_type_uuid' text not null, 'start_date' integer not null default 0, 'latitude' real, 'longitude' real, 'tag_id' text not null unique);
insert into 'equipment_new' ('uuid', 'title', 'equipment_type_uuid', 'critical_type_uuid', 'start_date', 'tag_id', 'latitude', 'longitude') select 'uuid', 'title', 'equipment_type_uuid', 'critical_type_uuid', 'start_date', 'tag_id', 0, 0 from 'equipment';
drop table if exists 'equipment';
alter table 'equipment_new' rename to 'equipment';

