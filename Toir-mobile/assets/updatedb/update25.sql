drop table if exists 'equipment_new';
create table 'equipment_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'title' text not null, 'equipment_type_uuid' text not null, 'critical_type_uuid' text not null, 'start_date' integer not null default 0, 'latitude' real, 'longitude' real, 'tag_id' text not null unique, 'image' text, 'equipment_status_uuid' text not null, 'inventory_number' text not null, 'location' text not null, CreatedAt integer default 0, ChangedAt integer default 0);
insert into 'equipment_new' ('uuid', 'title', 'equipment_type_uuid', 'critical_type_uuid', 'start_date', 'latitude', 'longitude', 'tag_id', 'image', 'equipment_status_uuid', 'inventory_number', 'location', 'CreatedAt', 'ChangedAt') select "uuid", "title", "equipment_type_uuid", "critical_type_uuid", "start_date", "latitude", "longitude", "tag_id", "img", "equipment_status_uuid", "inventory_number", "location", "CreatedAt", "ChangedAt" from 'equipment';
drop table if exists 'equipment';
alter table 'equipment_new' rename to 'equipment';

