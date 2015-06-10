drop table if exists 'equipment_operation';
create table 'equipment_operation' ('uuid' text not null primary key, 'task_uuid' text not null, 'equipment_uuid' text not null, 'operation_type_uuid' text not null, 'operation_pattern_uuid' text not null, 'operation_status_uuid' text not null);
