drop table if exists 'task_journal';
create table 'task_journal' ('_id' integer not null primary key, 'uuid' text not null unique, 'users_uuid' text not null, 'create_date' integer not null, 'modify_date' integer not null, 'close_date' integer not null default 0, 'task_status_uuid' text not null, 'attempt_send_date' integer not null default 0, 'attempt_count' integer not null default 0, 'updated' integer not null default 0, user_uuid text not null, change_date integer not null default 0, operation text not null);
drop table if exists 'equipment_operation_journal';
create table 'equipment_operation_journal' ('_id' integer not null primary key, 'uuid' text not null unique, 'task_uuid' text not null, 'equipment_uuid' text not null, 'operation_type_uuid' text not null, 'operation_pattern_uuid' text not null, 'operation_status_uuid' text not null, 'user_uuid' text not null, 'change_date' integer not null default 0, 'operation' text not null);

drop table if exists 'operation_pattern_step_new';
create table 'operation_pattern_step_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'operation_pattern_uuid' text not null, 'description' text not null, 'image' text default null, 'first_step' integer default 0, 'last_step' integer default 0, 'name'  text)
insert into 'operation_pattern_step_new' ('uuid', 'operation_pattern_uuid', 'description', 'image', 'first_step', 'last_step') select "uuid", "operation_pattern_uuid", "description", "image", "first_step", "last_step" from 'operation_pattern_step';
drop table if exists 'operation_pattern_step';
alter table 'operation_pattern_step_new' rename to 'operation_pattern_step';
