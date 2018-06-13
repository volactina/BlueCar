use bluecarproject;
delete from raw_send;
update car set status=0;
update car_detail set update_status=0;
delete from obstacle;
update car set x=0,y=0,z=0,t=0;