insert into user (id, first_name, last_name, email, roles, password)
values (1, 'goku', 'son', 'goku.son@hotmail.com', 'USER', '{bcrypt}$2a$10$BmWgGrkptSleGv6mv.nQ0OJnPMf61vjZUTiX7m6bAh/xYhEKukUP2');
insert into user (id, first_name, last_name, email, roles, password)
values (2, 'gohan', 'son', 'gohan.son@hotmail.com', 'USER', '{bcrypt}$2a$10$BmWgGrkptSleGv6mv.nQ0OJnPMf61vjZUTiX7m6bAh/xYhEKukUP2');
insert into profile (id, name, description)
values (1, 'Administrator', 'All permission');
insert into user_profile (profile_id, user_id)
values (1, 1);
insert into user_profile (profile_id, user_id)
values (1, 2);
select user_id
from user_profile
where id = 1;