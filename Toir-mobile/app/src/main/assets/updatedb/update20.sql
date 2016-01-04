drop table if exists 'critical_type_new';
create table 'critical_type_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'type' integer not null, CreatedAt integer default 0, ChangedAt integer default 0);
insert into 'critical_type_new' ('uuid', 'type') select uuid, type from 'critical_type';
drop table if exists 'critical_type';
alter table 'critical_type_new' rename to 'critical_type';

drop table if exists 'documentation_type_new';
create table 'documentation_type_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'title' text not null, CreatedAt integer default 0, ChangedAt integer default 0);
insert into 'documentation_type_new' ('uuid', 'title') select uuid, title from 'documentation_type';
drop table if exists 'documentation_type';
alter table 'documentation_type_new' rename to 'documentation_type';

drop table if exists 'equipment_type_new';
create table 'equipment_type_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'title' text not null, CreatedAt integer default 0, ChangedAt integer default 0);
insert into 'equipment_type_new' ('uuid', 'title') select uuid, title from 'equipment_type';
drop table if exists 'equipment_type';
alter table 'equipment_type_new' rename to 'equipment_type';

drop table if exists 'equipment_status_new';
create table 'equipment_status_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'title' text not null, 'type' integer, CreatedAt integer default 0, ChangedAt integer default 0);
insert into 'equipment_status_new' ('uuid', 'title', 'type') select uuid, title, type from 'equipment_status';
drop table if exists 'equipment_status';
alter table 'equipment_status_new' rename to 'equipment_status';

drop table if exists 'measure_type_new';
create table 'measure_type_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'title' text not null, CreatedAt integer default 0, ChangedAt integer default 0);
insert into 'measure_type_new' ('uuid', 'title') select uuid, title from 'measure_type';
drop table if exists 'measure_type';
alter table 'measure_type_new' rename to 'measure_type';

drop table if exists 'task_status_new';
create table 'task_status_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'title' text not null, CreatedAt integer default 0, ChangedAt integer default 0);
insert into 'task_status_new' ('uuid', 'title') select uuid, title from 'task_status';
drop table if exists 'task_status';
alter table 'task_status_new' rename to 'task_status';

drop table if exists 'operation_status_new';
create table 'operation_status_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'title' text not null, CreatedAt integer default 0, ChangedAt integer default 0);
insert into 'operation_status_new' ('uuid', 'title') select uuid, title from 'operation_status';
drop table if exists 'operation_status';
alter table 'operation_status_new' rename to 'operation_status';

drop table if exists 'operation_type_new';
create table 'operation_type_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'title' text not null, CreatedAt integer default 0, ChangedAt integer default 0);
insert into 'operation_type_new' ('uuid', 'title') select uuid, title from 'operation_type';
drop table if exists 'operation_type';
alter table 'operation_type_new' rename to 'operation_type';

drop table if exists 'operation_result_new';
create table 'operation_result_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'operation_type_uuid' text not null, 'title' text not null, CreatedAt integer default 0, ChangedAt integer default 0);
insert into 'operation_result_new' ('uuid', 'title', 'operation_type_uuid') select uuid, title, operation_type_uuid  from 'operation_result';
drop table if exists 'operation_result';
alter table 'operation_result_new' rename to 'operation_result';

drop table if exists 'equipment_documentation_new';
create table 'equipment_documentation_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'equipment_uuid' text not null, 'documentation_type_uuid' text not null, 'title' text not null, 'path' text not null, CreatedAt integer default 0, ChangedAt integer default 0);
insert into 'equipment_documentation_new' ('uuid', 'equipment_uuid', 'documentation_type_uuid', 'title', 'path') select uuid, equipment_uuid, documentation_type_uuid, title, path from 'equipment_documentation';
drop table if exists 'equipment_documentation';
alter table 'equipment_documentation_new' rename to 'equipment_documentation';

drop table if exists 'equipment_new';
create table 'equipment_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'title' text not null, 'equipment_type_uuid' text not null, 'critical_type_uuid' text not null, 'start_date' integer not null default 0, 'latitude' real, 'longitude' real, 'tag_id' text not null unique, 'img' text, 'equipment_status_uuid' text not null, 'inventory_number' text not null, 'location' text not null, CreatedAt integer default 0, ChangedAt integer default 0);
insert into 'equipment_new' ('uuid', 'title', 'equipment_type_uuid', 'critical_type_uuid', 'start_date', 'tag_id', 'latitude', 'longitude', 'img', 'equipment_status_uuid', 'inventory_number', 'location') select uuid, title, equipment_type_uuid, critical_type_uuid, start_date, tag_id, latitude, longitude, img, equipment_status_uuid, inventory_number, location from 'equipment';
drop table if exists 'equipment';
alter table 'equipment_new' rename to 'equipment';

