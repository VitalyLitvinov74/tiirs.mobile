drop table if exists 'equipment_operation_new';
create table 'equipment_operation_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'task_uuid' text not null, 'equipment_uuid' text not null, 'operation_type_uuid' text not null, 'operation_pattern_uuid' text not null, 'operation_status_uuid' text not null, 'operation_time' integer not null default 0);
insert into 'equipment_operation_new' ('uuid', 'task_uuid', 'equipment_uuid', 'operation_type_uuid', 'operation_pattern_uuid', 'operation_status_uuid', 'operation_time') select "uuid", "task_uuid", "equipment_uuid", "operation_type_uuid", "operation_pattern_uuid", "operation_status_uuid", 0 from 'equipment_operation';
drop table if exists 'equipment_operation';
alter table 'equipment_operation_new' rename to 'equipment_operation';

drop table if exists 'operation_pattern_new';
create table 'operation_pattern_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'title' text not null, 'operation_type_uuid' text not null);
insert into 'operation_pattern_new' ('uuid', 'title', 'operation_type_uuid') select "uuid", "title", '' from 'operation_pattern';
drop table if exists 'operation_pattern';
alter table 'operation_pattern_new' rename to 'operation_pattern';

drop table if exists 'equipment_new';
create table 'equipment_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'title' text not null, 'equipment_type_uuid' text not null, 'critical_type_uuid' text not null, 'start_date' integer not null default 0, 'latitude' real, 'longitude' real, 'tag_id' text not null unique, 'img' text, 'equipment_status_uuid' text not null, 'inventory_number' text not null, 'location' text not null);
insert into 'equipment_new' ('uuid', 'title', 'equipment_type_uuid', 'critical_type_uuid', 'start_date', 'tag_id', 'latitude', 'longitude','img', 'equipment_status_uuid', 'inventory_number', 'location') select uuid, title, equipment_type_uuid, critical_type_uuid, start_date, tag_id, latitude, longitude, '/data/img/img.png', '61c5007f-ae18-4c4e-bd57-737a20ef9ebc', '', 'не указано' from 'equipment';
drop table if exists 'equipment';
alter table 'equipment_new' rename to 'equipment';

drop table if exists 'task_new';
create table 'task_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'users_uuid' text not null, 'create_date' integer not null, 'modify_date' integer not null, 'close_date' integer not null default 0, 'task_status_uuid' text not null, 'attempt_send_date' integer not null default 0, 'attempt_count' integer not null default 0, 'updated' integer not null default 0, 'task_name' text);
insert into 'task_new' ('uuid', 'users_uuid', 'create_date', 'modify_date', 'close_date', 'task_status_uuid', 'attempt_send_date', 'attempt_count', 'updated', 'task_name') select "uuid", "users_uuid", "create_date", "modify_date", "close_date", "task_status_uuid", "attempt_send_date", "attempt_count", "successefull_send", '' from 'task';
drop table if exists 'task';
alter table 'task_new' rename to 'task';
