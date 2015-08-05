drop table if exists 'token_new';
create table 'token_new' ('token_type' text not null, 'access_token' text not null, 'expires_in' integer not null, 'username' text not null unique, '.issued' text not null, '.expires' text not null);
insert into 'token_new' ('token_type', 'access_token', 'expires_in', 'username', '.issued', '.expires') select 'bearer' as 'token_type', 'access_token', 'expires_in', 'username', '.issued', '.expires' from 'token';
drop table if exists 'token';
alter table 'token_new' rename to 'token';
