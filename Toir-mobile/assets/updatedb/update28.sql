drop table if exists 'equipment_documentation_new';
create table 'equipment_documentation_new' ('_id' integer not null primary key, 'uuid' text not null unique, 'equipment_uuid' text not null, 'documentation_type_uuid' text not null, 'title' text not null, 'path' text not null, CreatedAt integer default 0, ChangedAt integer default 0, required integer default 0);
insert into 'equipment_documentation_new' ('uuid', 'equipment_uuid', 'documentation_type_uuid', 'title', 'path', 'CreatedAt', 'ChangedAt') select uuid, equipment_uuid, documentation_type_uuid, title, path, CreatedAt, ChangedAt from 'equipment_documentation';
drop table if exists 'equipment_documentation';
alter table 'equipment_documentation_new' rename to 'equipment_documentation';

