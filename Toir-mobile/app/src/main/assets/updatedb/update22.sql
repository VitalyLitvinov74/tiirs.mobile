drop table if exists 'equipment_operation_new';
create table 'equipment_operation_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'task_uuid' text not null, 'equipment_uuid' text not null, 'operation_type_uuid' text not null, 'operation_pattern_uuid' text not null, 'operation_status_uuid' text not null, 'operation_time' integer not null default 0, 'CreatedAt' integer not null, 'ChangedAt' integer not null);
insert into 'equipment_operation_new' ('uuid', 'task_uuid', 'equipment_uuid', 'operation_type_uuid', 'operation_pattern_uuid', 'operation_status_uuid', 'operation_time', 'CreatedAt', 'ChangedAt') select "uuid", "task_uuid", "equipment_uuid", "operation_type_uuid", "operation_pattern_uuid", "operation_status_uuid", "operation_time", 0, 0 from 'equipment_operation';
drop table if exists 'equipment_operation';
alter table 'equipment_operation_new' rename to 'equipment_operation';

drop table if exists 'operation_pattern_new';
create table 'operation_pattern_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'title' text not null, 'operation_type_uuid' text not null, 'CreatedAt' integer not null, 'ChangedAt' integer not null);
insert into 'operation_pattern_new' ('uuid', 'title', 'operation_type_uuid', 'CreatedAt', 'ChangedAt') select "uuid", "title", "operation_type_uuid", 0, 0 from 'operation_pattern';
drop table if exists 'operation_pattern';
alter table 'operation_pattern_new' rename to 'operation_pattern';

drop table if exists 'operation_pattern_step_new';
create table 'operation_pattern_step_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'operation_pattern_uuid' text not null, 'description' text not null, 'image' text default null, 'first_step' integer default 0, 'last_step' integer default 0, 'name'  text, 'CreatedAt' integer not null, 'ChangedAt' integer not null);
insert into 'operation_pattern_step_new' ('uuid', 'operation_pattern_uuid', 'description', 'image', 'first_step', 'last_step', 'name', 'CreatedAt', 'ChangedAt') select "uuid", "operation_pattern_uuid", "description", "image", "first_step", "last_step", "name", 0, 0 from 'operation_pattern_step';
drop table if exists 'operation_pattern_step';
alter table 'operation_pattern_step_new' rename to 'operation_pattern_step';

drop table if exists 'operation_pattern_step_result_new';
create table 'operation_pattern_step_result_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'operation_pattern_step_uuid' text not null, 'next_operation_pattern_step_uuid' text not null, 'title' text not null, 'measure_type_uuid' text default null, 'CreatedAt' integer not null, 'ChangedAt' integer not null);
insert into 'operation_pattern_step_result_new' ('uuid', 'operation_pattern_step_uuid', 'next_operation_pattern_step_uuid', 'title', 'measure_type_uuid', 'CreatedAt', 'ChangedAt') select "uuid", "operation_pattern_step_uuid", "next_operation_pattern_step_uuid", "title", "measure_type_uuid", 0, 0  from 'operation_pattern_step_result';
drop table if exists 'operation_pattern_step_result';
alter table 'operation_pattern_step_result_new' rename to 'operation_pattern_step_result';

