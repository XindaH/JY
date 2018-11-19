drop procedure if exists withdraw;
delimiter //
Create procedure withdraw(IN course varchar(10),IN id int(11), IN curYear int, IN quarter char(2), IN nextYear int, IN nextQuarter char(2), OUT con int)
-- con: 0 is success;1 is no fail;
begin
declare y int;
declare q char(2);
if (select Grade from transcript where ((Year=curYear and Semester=quarter)  or (Year=nextYear and Semester=nextQuarter)) and StudId=id and UoSCode=course) is null
then 
select Year, Semester into y,q from transcript where  StudId=id and UoSCode=course;
start transaction;
delete from transcript where UoSCode=course and StudId=id;
update uosoffering set Enrollment=Enrollment-1 where UoSCode=course and Year=y and Semester=q;
commit;
set con=0;
else 
set con=1;
end if;
end //
delimiter ;

drop procedure if exists enrollment;
delimiter //
Create procedure enrollment(IN course varchar(10),IN Student_id int(11),IN y int(11), IN q char(2),IN ny int(11), IN nq char(2), OUT con int)
-- con: 4 is satisfy;1 is no space;2 is need pre. 3 is enrolled before;
begin
declare num int;
declare res int;
set num  = (select count(*) from requires where UoSCode = course);
set res = (select count(*) from requires, transcript A, uosoffering where requires.UoSCode = course and 
A.StudId = Student_id and requires.PrereqUoSCode = A.UoSCode and A.grade is not NULL and 
A.UoSCode != course and uosoffering.UoSCode = course and ((uosoffering.year = y and 
uosoffering.Semester = q) or (uosoffering.year = ny and 
uosoffering.Semester = nq))  and uosoffering.MaxEnrollment > uosoffering.Enrollment and requires.EnforcedSince<=curdate() );
if (select count(*) from transcript where StudId = Student_id and UoSCode = course) = 1
then set con=3;
elseif(exists (select * from uosoffering where UoSCode=course and Enrollment=MaxEnrollment and Year=y and Semester=q)) 
then set con=1;
elseif num = res then 
	set con=4;
	start transaction;
	insert into transcript values(Student_id, course,q,y,null);
	update uosoffering set Enrollment=Enrollment+1 where UoSCode=course;
    commit;
else set con=2;
end if;

end //
delimiter ;

drop trigger if exists belowHalf;
delimiter //
create trigger belowHalf after update on uosoffering
for each row
begin
	 if  new.Enrollment < 0.5*new.MaxEnrollment
     then
		 update Warning set signal1=1;
	else update Warning set signal1=0;
	end if;
end	//
delimiter ;

drop procedure if exists ChangeAddress;

DELIMITER //
CREATE PROCEDURE ChangeAddress(in student_ID INT, in new_Address CHAR(50)) 
BEGIN
declare exit handler for sqlexception, sqlwarning
begin
    rollback;
end;
start transaction;
	update student 
	set Address = new_Address
    where Id = student_ID;
commit;
END //
DELIMITER ;

drop procedure if exists ChangePassword;

DELIMITER //
CREATE PROCEDURE ChangePassword(in student_ID INT, in new_Password CHAR(10)) 
BEGIN
declare exit handler for sqlexception, sqlwarning
begin
    rollback;
end;
start transaction;
	update student 
	set Password = new_Password
    where Id = student_ID;
commit;
END //
DELIMITER ;