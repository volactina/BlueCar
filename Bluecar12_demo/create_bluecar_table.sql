create database if not exists bluecarproject;

use bluecarproject;

drop table if exists raw_send;
create table if not exists raw_send(
				send_id int,
				receive_id int,
				message_id int,
				status int,
				message varchar(255)
				);

drop table if exists car;
create table if not exists car(
				car_id int not null primary key,
				status boolean not null default false,
				pos_id int default 0,
				x int default 0,
				y int default 0,
				z int default 0,
				t date default 0
				);

insert into car values('1',false,'0','0','0','0',0);
insert into car values('2',false,'0','0','0','0',0);
insert into car values('3',false,'0','0','0','0',0);

drop table if exists car_detail;
create table if not exists car_detail(
					car_id int not null primary key,
					update_status int not null,
					left_motor int,
					right_motor int,
					rotate_per_sec float,
					move_per_sec float,
					auto_report_obstacle boolean not null default true,
					maxreportpersec_obstacle int default 5,
					auto_report_sensor boolean not null default false,
					maxreportpersec_sensor int default 5
					);
insert into car_detail values(1,0,200,200,225,100,false,5,false,5);
insert into car_detail values(2,0,200,200,225,100,false,5,false,5);
insert into car_detail values(3,0,200,200,225,100,false,5,false,5);

drop table if exists obstalce;
create table if not exists obstacle(
					car_id int,
					x float not null,
					y float not null
					);

drop table if exists car_code;
create table if not exists car_code(
					car_id int,
					status int,
					code varchar(255)
					);
