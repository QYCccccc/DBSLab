-- backup data and log file
use master;

EXEC sp_addumpdevice 'disk', 'lab1','/home/lab1data.bak';
GO
exec sp_addumpdevice 'disk',  'lab1Log','/home/lab1Log.bak';
GO
BACKUP DATABASE lab1 to lab1;
GO
-- BACKUP DATABASE lab1 to DISK='/home/lab1data.bak';
BACKUP log lab1 to lab1Log;
GO

IF DB_ID('lab1') is not null 
    drop DATABASE lab1;
else
    PRINT 'not exist'
go

select * from sys.sysdatabases;
go
RESTORE DATABASE lab1 FROM lab1;
GO
restore log lab1 from lab1Log;
go
select * from sys.sysdatabases;
go
USE lab1;
go
select * from sys.sysobjects WHERE xtype = 'U';
go

-- create user and grant priviliges

create login userqyc
    with password = 'Qyc123456';
go
use lab1;
go
create user qyc for login userqyc;
go 
grant select on users to qyc;
go



-- (1)分别用一条sql语句完成对博文表基本的增、删、改的操作
use lab1;
insert into mblog(Title,UID,Pyear,Pmonth,Pday,cont)
VALUES
(N'音乐台',5,2019,4,12,'dasdsadsaffgs')
go
select title from mblog where uid = 5;

-- Update rows in table 'mblog'
UPDATE mblog
SET
   UID = 6 
WHERE Title = N'音乐台';	
GO
SELECT * from mblog
WHERE Title = N'音乐台';
GO

-- Delete rows from table 'mblog'
DELETE FROM mblog
WHERE Title = N'音乐台';	/* add search conditions here */
GO
SELECT * from mblog
WHERE Title = N'音乐台';
GO

-- 将关注3号用户的用户信息插入到一个自定义的新表FANS_3中
IF OBJECT_ID('fans_3','U') IS NOT NULL
    DROP TABLE fans_3;
GO
CREATE TABLE fans_3
(
    uid INT NOT NULL, 
    name NVARCHAR(10) NOT NULL,
    sex NVARCHAR(2) NOT NULL,
    byear INT NULL,
    city NVARCHAR(20) NULL
);
GO
insert into fans_3(uid, name,sex,byear,city)
select uid,name,sex,byear,city 
from users
where uid in (
    select uid 
    from follow
    where UIDFLED = 3
);
GO
SELECT * FROM fans_3;
GO

-- create a table without primary key, and then insert duplicated
-- records into the table, finally observer delete and update operation
-- happened

if OBJECT_ID('duptable','U') is not NULL
    drop table duptable;
go 

create table duptable(
    id int not NULL,
    age int not NULL
);

insert into duptable(id,age)
values
    (1,2),
    (1,2),
    (1,2),
    (2,3),
    (2,3),
    (2,3),
    (3,4),
    (3,4);
go 

select * from duptable;
go

delete from duptable where id = 1;
go
select * from duptable;
go

update duptable
set age = 18
where id = 2;
go 
select * from duptable;
go  

-- create a trigger for thumb table
use lab1;
go
-- delete invalid records from thumb before test
delete t 
from thumb as t
    inner join mblog as b 
        on t.bid = b.bid
where t.uid = b.uid;
go 

if OBJECT_ID('tr_thumb_insert','TR') is not null 
    drop trigger tr_thumb_insert;
else
    print 'not exist';
go

create trigger tr_thumb_insert
on thumb
instead of insert
as 
begin 
    declare @uid int;
    declare @bid int;
    print 'start'

    declare thumb_cursor CURSOR for
    select UID,BID from inserted

    open thumb_cursor

    fetch next from thumb_cursor
    into @uid,@bid 

    while @@FETCH_STATUS = 0
    begin 
        if not exists (select * from mblog as m
            where m.BId = @bid and m.UID = @uid)
            begin
                insert into thumb(UID,BID)
                values (@uid,@bid);
            end
        else
            print 'invalid record'

        fetch next from thumb_cursor
        into @uid,@bid 
    end
    -- 结束后要关闭并且释放游标
    close thumb_cursor
    deallocate thumb_cursor 

    print 'end'
end
go


create trigger tr_thumb_update
on thumb
instead of update 
as 
begin
    declare @duid int;
    declare @dbid int;
    declare @uid int;
    declare @bid int;
    set @uid = (select top 1 UID from inserted);
    set @bid = (select top 1 BID from inserted);
    set @duid = (select top 1 UID from deleted);
    set @dbid = (select top 1 BID from deleted);
    if not exists(select * from mblog as m
        where m.BId = @bid and m.UID = @uid)
        update thumb
        set BID =@bid, UID = @uid
        where BID = @dbid and UID=@duid;
    else
        print 'invalid record';
end;
go

insert into thumb(BID,UID)
values (1,2),
       (2,1),
       (1,3),
       (1,1);
go
delete from thumb where UID=2 and BID=1;
select * from thumb;
go
insert into thumb(BID,UID)
values (1,5);
go
select * from thumb;
go

use lab1;

update thumb
set UID = 2
where BID = 4;
go
update thumb
set BID = 5
where UID = 1 and BID = 4;
go


-- 查询“张三”用户关注的所有用户
select * from users
where uid in (select UIDFLED from follow
                where uid = (select uid from users
                                where name = N'张三'))
order by byear desc,uid;
GO

-- 查找没有被任何人点赞的博文
select distinct bid from thumb;
go

select b.bid,b.Title,users.name
from mblog as b 
inner join users
on b.UID = users.uid
where b.BId not in (select distinct BId from thumb)
order by b.Title;
go

-- 查找1996年以后出生的武汉市用户发表的进入过头条的博文ID
select mblog.BId,mblog.Title,users.uid,users.city
from mblog
inner join users 
    on users.uid = mblog.UID
where users.byear > 1996 and users.city = N'武汉';
go

-- 查找订阅了所有分类的用户ID
INSERT INTO sub(UID,LID)
VALUES
(5,1),
(5,2),
(5,3),
(5,4),
(5,5),
(5,6),
(5,7)
go
SELECT * from sub;
go

select sub.UID
from sub
group by sub.UID
having count(distinct sub.LID)=(select count(*) from label);
go

-- 查找出生年份小于1970年或者大于2010年的用户
INSERT INTO users
( -- columns to insert data into
 [name], [sex], [byear],[city]
)
VALUES
('Tim',N'男',1969,N'武汉'),
('Jessica',N'女',1968,N'武汉'),
('Tony',N'男',1966,N'武汉'),
('James',N'男',2011,N'武汉'),
('Rebecca',N'女',2013,N'武汉')
go

select u.uid,u.byear,u.city
from users as u
where u.byear not between 1970 and 2010;
go

-- 统计每个城市的用户数
select city,COUNT(*) as usersNum
from users
group by city
order by usersNum;
go

-- 统计每个城市的每个出生年份的用户数
select city,byear,count(*) as userNum
from users
group by city,byear
order by city, byear desc;
go

-- 查找被点赞数超过10的博文ID号
INSERT INTO thumb(UID,BId)
VALUES
    (6,4);
go
INSERT INTO thumb(UID,BId)
VALUES
    (5,4);
go
INSERT INTO thumb(UID,BId)
VALUES
    (4,4);
go

INSERT INTO thumb(UID,BId)
VALUES
    (3,2);
go
INSERT INTO thumb(UID,BId)
VALUES
    (4,2);
go
INSERT INTO thumb(UID,BId)
VALUES
    (5,2);
go


select BID, count(*) as num
from thumb
group by BID
having count(*) > 2;
go

-- 查找被2000年后出生的用户点赞数超过10的博文ID号

-- insert some necessary records
INSERT INTO users
( -- columns to insert data into
 [name], [sex], [byear],[city]
)
VALUES
('Jack',N'男',2002,N'武汉'),
('Tom',N'男',2005,N'武汉');
go
select * from thumb;
INSERT INTO thumb(UID,BId)
VALUES
    (14,2);
go
INSERT INTO thumb(UID,BId)
VALUES
    (15,2);
go
INSERT INTO thumb(UID,BId)
VALUES
    (16,2);
go
INSERT INTO thumb(UID,BId)
VALUES
    (17,2);
go
INSERT INTO thumb(UID,BId)
VALUES
    (14,3);
go
INSERT INTO thumb(UID,BId)
VALUES
    (15,3);
go
INSERT INTO thumb(UID,BId)
VALUES
    (16,3);
go
INSERT INTO thumb(UID,BId)
VALUES
    (17,3);
go
-- operation
select t.BID,COUNT(*) as num
from thumb t
inner join users u 
    on u.uid = t.UID
where u.byear > 2000
group by t.BID
having count(*) > 2;
go

-- 查找被2000年后出生的用户点赞数超过10的每篇博文的进入头条的次数
INSERT INTO thumb(UID,BId)
VALUES
    (14,5);
go
INSERT INTO thumb(UID,BId)
VALUES
    (15,5);
go
INSERT INTO thumb(UID,BId)
VALUES
    (16,5);
go
INSERT INTO thumb(UID,BId)
VALUES
    (17,5);
go
INSERT INTO thumb(UID,BId)
VALUES
    (14,1);
go
INSERT INTO thumb(UID,BId)
VALUES
    (15,1);
go
-- operation
select sbid.BID,count(TNO) as topnum
-- from 被2000年后出生的用户点赞数超过2的 BID集合
from (select t.BID
        from thumb t
        inner join users u 
            on u.uid = t.UID
        where u.byear > 2000
        group by t.BID
        having count(*) > 2) as sbid 
left outer join topday
    on topday.BID = sbid.BID
group by sbid.BID;
go


-- 查找订阅了文学、艺术、哲学、音乐中至少一种分类的用户ID
select distinct sub.UID
from sub
inner join label as l
    on l.LId = sub.LID
where l.LName in (N'文学',N'艺术',N'哲学',N'音乐');
go

-- 查找标题中包含了“最多地铁站”和“_华中科技大学”两个词的博文基本信息

BEGIN
    declare @maxbid int;
    set @maxbid = (select top 1 BId from mblog order by BId desc);
    set @maxbid = @maxbid + 1;
    print @maxbid;
    set IDENTITY_INSERT mblog on;
    INSERT INTO mblog(BId,Title,UID,pyear,pmonth,pday,cont)
    VALUES
    (@maxbid,N'地铁_华中科技大学sasda',6,2019,4,10,'aaaaaaaaaaaaa');
    set identity_insert mblog off;
END;
go


select * from mblog
where Title like  N'%最多地铁站%' 
            or Title like N'%\_华中科技大学%' escape N'\';
go

-- 查找所有相互关注的用户对的两个ID号，要求不能使用嵌套查询
-- 如何继续筛选?
select *
from follow t1
inner join follow t2
    on t1.UIDFLED = t2.UID
where t1.UID = t2.UIDFLED
order by t1.UID;
go


-- 查找好友圈包含了5号用户好友圈的用户ID
INSERT INTO friends(UID,FUID)
VALUES
(5,2),
(5,4),
(5,8),
(5,10),
(5,1);
go

select distinct f.UID
from friends f 
where f.FUID in 
    (select FUID from friends where UID = 5)
    and f.UID <> 5;
go

-- 查找2019年4月20日每一篇头条博文的ID号、标题以及该博文的每一个分类ID
select * from mblog;
select * from topday;

insert into topday(Tyear,Tmonth,Tday,BID,TNO)
VALUES
(2019,3,27,8,9)
go

select t.BID, mblog.Title,B_L.LId
from topday t 
inner join mblog
    on mblog.BId = t.BID
left outer join B_L
    on t.BID = B_L.BId
where t.Tyear = 2019 and t.Tmonth = 3 and t.Tday = 27;
go


-- 查找至少有3名共同好友的所有用户对的两个ID号

select f1.UID,f2.UID
from friends f1
inner join friends f2
-- 将拥有共同好友的user进行联接
    on f1.FUID = f2.FUID and f1.UID <> f2.UID
group by f1.UID,f2.UID
HAVING count(*) >= 3
order by f1.UID;
go


-- 创建视图：查阅DBMS内部函数，创建一个显示当日热度排名前十的微博信息的视图，
-- 其中的属性包括：博文ID、博文标题、发表者ID、发表者姓名、被点赞数
BEGIN
    declare @date date;
    declare @year int;
    declare @month int;
    declare @day int;
    set @date = (select CONVERT (date, SYSDATETIME()));
    set @year = (select YEAR(@date));
    set @month = (select MONTH(@date));
    set @day = (select DAY(@date));
    -- print @date;
    -- print @year;
    -- print @month;
    -- print @day;
    insert into topday(Tyear,Tmonth,Tday,BID,TNO)
    VALUES
    (@year,@month,@day,1,1),
    (@year,@month,@day,2,2),
    (@year,@month,@day,3,3),
    (@year,@month,@day,4,4),
    (@year,@month,@day,5,5);
end;
go


drop view curTopday;

create view curTopday
as
select mblog.BId,mblog.Title,mblog.UID,users.name,count(*) as thumbNum
from topday t 
inner join mblog
    on t.BID = mblog.BId
inner join users 
    on mblog.UID = users.uid
inner join thumb
    on thumb.BID = t.BID
where t.Tyear = (select YEAR(SYSDATETIME())) 
    and t.Tmonth = (select MONTH(SYSDATETIME())) 
    and t.Tday = (select DAY(SYSDATETIME()))
group by mblog.BId,mblog.Title,mblog.UID,users.name;
GO


select * from curTopday;
go