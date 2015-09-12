drop table if exists 'measure_value_new';
create table 'measure_value_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'equipment_operation_uuid' text not null, 'operation_pattern_step_result_uuid' text not null, 'date' integer not null, 'value' text not null, 'attempt_send_date' integer default null, 'attempt_count' integer not null default 0, 'updated' integer not null default 0, CreatedAt integer default 0, ChangedAt integer default 0);
insert into 'measure_value_new' ('uuid', 'equipment_operation_uuid', 'operation_pattern_step_result_uuid', 'date', 'value', 'attempt_send_date', 'attempt_count', 'updated', 'CreatedAt', 'ChangedAt') select "uuid", "equipment_operation_uuid", "operation_pattern_step_result", "date", "value", "attempt_send_date", "attempt_count", "updated", "CreatedAt", "ChangedAt" from 'measure_value';
drop table if exists 'measure_value';
alter table 'measure_value_new' rename to 'measure_value';

drop table if exists 'operation_pattern_step_new';
create table 'operation_pattern_step_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'operation_pattern_uuid' text not null, 'description' text not null, 'image' text default null, 'first_step' integer default 0, 'last_step' integer default 0, 'title' text, 'CreatedAt' integer not null, 'ChangedAt' integer not null);
insert into 'operation_pattern_step_new' ('uuid', 'operation_pattern_uuid', 'description', 'image', 'first_step', 'last_step', 'title', 'CreatedAt', 'ChangedAt') select "uuid", "operation_pattern_uuid", "description", "image", "first_step", "last_step", "name", "CreatedAt", "ChangedAt" from 'operation_pattern_step';
drop table if exists 'operation_pattern_step';
alter table 'operation_pattern_step_new' rename to 'operation_pattern_step';

