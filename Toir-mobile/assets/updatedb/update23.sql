drop table if exists 'equipment_operation_result_new';
create table 'equipment_operation_result_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'equipment_operation_uuid' text not null, 'start_date' integer not null, 'end_date' integer default null, 'operation_result_uuid' text not null default '00000000-0000-0000-0000-000000000000', 'type' integer not null default 0,  'attempt_send_date' integer default null, 'attempt_count' integer not null default 0, 'updated' integer not null default 0,CreatedAt integer default 0, ChangedAt integer default 0);
insert into 'equipment_operation_result_new' ('uuid', 'equipment_operation_uuid', 'start_date', 'end_date', 'operation_result_uuid', 'type') select "uuid", "equipment_operation_uuid", "start_date", "end_date", "operation_result_uuid", "type" from 'equipment_operation_result';
drop table if exists 'equipment_operation_result';
alter table 'equipment_operation_result_new' rename to 'equipment_operation_result';

drop table if exists 'measure_value_new';
create table 'measure_value_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'equipment_operation_uuid' text not null, 'operation_pattern_step_result' text not null, 'date' integer not null, 'value' text not null, 'attempt_send_date' integer default null, 'attempt_count' integer not null default 0, 'updated' integer not null default 0, CreatedAt integer default 0, ChangedAt integer default 0);
insert into 'measure_value_new' ('uuid', 'equipment_operation_uuid', 'operation_pattern_step_result', 'date', 'value') select "uuid", "equipment_operation_uuid", "operation_pattern_step_result", "date", "value" from 'measure_value';
drop table if exists 'measure_value';
alter table 'measure_value_new' rename to 'measure_value';

