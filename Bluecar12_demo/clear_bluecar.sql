use bluecarproject;

delete from raw_send;
delete from obstacle;
update car set status=0,x=0,y=0,z=0;
update car_detail set update_status=0;
