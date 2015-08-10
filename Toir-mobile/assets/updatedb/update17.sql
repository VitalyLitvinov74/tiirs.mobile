drop table if exists 'equipment_status';
create table 'equipment_status' ('_id' integer not null primary key autoincrement, 'uuid' text not null unique, 'title' text not null, 'type' integer);

drop table if exists 'equipment_new';
create table 'equipment_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'title' text not null, 'equipment_type_uuid' text not null, 'critical_type_uuid' text not null, 'start_date' integer not null default 0, 'latitude' real, 'longitude' real, 'tag_id' text not null unique, 'img' text, 'equipment_status_uuid' text not null);
insert into 'equipment_new' ('uuid', 'title', 'equipment_type_uuid', 'critical_type_uuid', 'start_date', 'tag_id', 'latitude', 'longitude','img', 'equipment_status_uuid') select uuid, title, equipment_type_uuid, critical_type_uuid, start_date, tag_id, latitude, longitude, '/data/img/img.png', '61c5007f-ae18-4c4e-bd57-737a20ef9ebc' from 'equipment';
drop table if exists 'equipment';
alter table 'equipment_new' rename to 'equipment';

