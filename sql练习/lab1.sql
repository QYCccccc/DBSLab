-- Create a new database called 'lab1'
-- Connect to the 'master' database to run this snippet
USE master
GO
-- Create the new database if it does not exist already
IF NOT EXISTS (
    SELECT name
        FROM sys.databases
        WHERE name = N'lab1'
)
CREATE DATABASE lab1;
GO

USE lab1
GO

-- Create a new table called 'users' in schema 'lab1'
-- Drop the table if it already exists
IF OBJECT_ID('users', 'U') IS NOT NULL
BEGIN
    PRINT 'exist';
    DROP TABLE users;
END;
ELSE
    PRINT 'not exist';
GO
-- Create the table in the specified schema
CREATE TABLE users
(
    uid INT PRIMARY KEY NOT NULL IDENTITY,  -- primary key column
    name NVARCHAR(10) NOT NULL,
    sex NVARCHAR(2) NOT NULL,
    byear INT NULL,
    city NVARCHAR(20) NULL
);
GO


-- Create a new table called 'label' in schema 'SchemaName'
-- Drop the table if it already exists
IF OBJECT_ID('label', 'U') IS NOT NULL
DROP TABLE label
GO
-- Create the table in the specified schema
CREATE TABLE label
(
    LId INT NOT NULL PRIMARY KEY IDENTITY, -- primary key column
    LName [NVARCHAR](10) NOT NULL
);
GO


-- Create a new table called 'mblog' in schema 'SchemaName'
-- Drop the table if it already exists
IF OBJECT_ID('mblog', 'U') IS NOT NULL
DROP TABLE mblog
GO
-- Create the table in the specified schema
CREATE TABLE mblog
(
    BId INT NOT NULL PRIMARY KEY IDENTITY, -- primary key column
    Title [NVARCHAR](50) NOT NULL,
    UID INT NOT NULL FOREIGN KEY(UID) REFERENCES users(uid),
    pyear INT NULL,
    pmonth INT NULL,
    pday INT NULL,
    cont NVARCHAR(100) NULL
    -- specify more columns here
);
GO


-- Create a new table called 'B_L' in schema 'SchemaName'
-- Drop the table if it already exists
IF OBJECT_ID('B_L', 'U') IS NOT NULL
DROP TABLE B_L
GO
-- Create the table in the specified schema
CREATE TABLE B_L
(
    BId INT NOT NULL FOREIGN KEY (BId) REFERENCES mblog(BId), 
    LId INT NOT NULL FOREIGN KEY (LId) REFERENCES label(LId)
);
GO

-- Create a new table called 'follow' in schema 'SchemaName'
-- Drop the table if it already exists
IF OBJECT_ID('follow', 'U') IS NOT NULL
DROP TABLE follow
GO
-- Create the table in the specified schema
CREATE TABLE follow
(
    UID INT NOT NULL FOREIGN KEY (UID) REFERENCES users(uid),
    UIDFLED INT NOT NULL FOREIGN key (UIDFLED) REFERENCES users(uid)
);
GO

-- Create a new table called 'friends' in schema 'SchemaName'
-- Drop the table if it already exists
IF OBJECT_ID('friends', 'U') IS NOT NULL
DROP TABLE friends
GO
-- Create the table in the specified schema
CREATE TABLE friends
(
    UID INT NOT NULL FOREIGN KEY (UID) REFERENCES users(uid),
    FUID INT NOT NULL FOREIGN key (FUID) REFERENCES users(uid)
);
GO

-- Create a new table called 'sub' in schema 'SchemaName'
-- Drop the table if it already exists
IF OBJECT_ID('sub', 'U') IS NOT NULL
DROP TABLE sub
GO
-- Create the table in the specified schema
CREATE TABLE sub
(
    UID INT NOT NULL FOREIGN KEY (UID) REFERENCES users(uid),
    LID INT NOT NULL FOREIGN key (LID) REFERENCES label(LId)
);
GO


-- Create a new table called 'thumb' in schema 'SchemaName'
-- Drop the table if it already exists
IF OBJECT_ID('thumb', 'U') IS NOT NULL
DROP TABLE thumb
GO
-- Create the table in the specified schema
CREATE TABLE thumb
(
    UID INT NOT NULL FOREIGN KEY (UID) REFERENCES users(uid),
    BID INT NOT NULL FOREIGN KEY (BID) REFERENCES mblog(BId)
);
GO

-- Create a new table called 'topday' in schema 'SchemaName'
-- Drop the table if it already exists
IF OBJECT_ID('topday', 'U') IS NOT NULL
DROP TABLE topday
GO
-- Create the table in the specified schema
CREATE TABLE topday
(
    Tyear INT ,
    Tmonth INT,
    Tday INT,
    BID INT NOT NULL FOREIGN KEY (BID) REFERENCES mblog(BId),
    TNO INT NOT NULL,
    CONSTRAINT pk
    PRIMARY KEY(Tyear,Tmonth,Tday,TNO)
);
GO

-- Insert rows into table 'users'
INSERT INTO users
( -- columns to insert data into
 [name], [sex], [byear],[city]
)
VALUES
('Bob',N'男',1997,N'武汉'),
('Alice',N'女',1997,N'武汉'),
(N'张一',N'男',1999,N'广州'),
(N'张二',N'男',1999,N'广州'),
(N'张三',N'男',1999,N'广州'),
(N'李四',N'男',1998,N'深圳'),
(N'王五',N'男',1996,N'东莞'),
(N'小红',N'女',1997,N'北京'),
(N'小芳',N'女',1997,N'北京'),
(N'小蓝',N'女',1999,N'上海')
GO

-- Insert rows into table 'label'
INSERT INTO label(LName)
VALUES 
(N'编程'),
(N'电子产品'),
(N'娱乐'),
(N'文学'),
(N'艺术'),
(N'哲学'),
(N'音乐')
GO

INSERT INTO mblog(Title,UID,pyear,pmonth,pday,cont)
VALUES
('C',1,2019,4,10,'aaaaaaaaaaaaa'),
('C++',1,2019,4,10,'bbbbbbbbbbbb'),
('C#',1,2019,4,10,'cccccccccccc'),
('Java',2,2019,3,27,'dddddddddd'),
('apple',8,2019,1,10,'mmmmmmmmmmmmm'),
(N'华中科技大学占有最多地铁站',3,2019,2,2,'asdasdasdasd'),
(N'华中科技大学被称为最多地铁站的大学',4,2019,2,3,'fdsdfasfsaafasdfa')
GO

INSERT INTO B_L(BID, LID)
VALUES
(1,1),
(2,1),
(3,1),
(4,1),
(5,2),
(6,3),
(7,3)
GO

INSERT INTO follow(UID, UIDFLED)
VALUES
(1,2),
(1,4),
(1,6),
(1,8),
(1,10),
(2,1),
(2,3),
(2,5),
(2,7),
(2,9),
(3,5),
(3,7),
(3,10),
(4,5),
(4,2),
(4,8),
(5,3),
(5,9),
(5,10)
GO

INSERT INTO friends(UID,FUID)
VALUES
(6,2),
(2,6),
(6,4),
(4,6),
(6,8),
(8,6),
(6,10),
(10,6),
(7,1),
(1,7),
(7,3),
(3,7),
(7,5),
(5,7),
(7,6),
(6,7),
(7,9),
(9,7),
(8,5),
(5,8),
(8,7),
(7,8),
(8,10),
(10,8),
(9,5),
(5,9),
(9,2),
(2,9),
(9,8),
(8,9),
(10,3),
(3,10),
(10,9),
(9,10)
GO

INSERT INTO sub(UID,LID)
VALUES
(1,1),
(1,2),
(1,3),
(1,4),
(2,2),
(2,3),
(2,4),
(3,5),
(3,7),
(4,1),
(6,3),
(6,5),
(7,1),
(7,2)
GO

INSERT INTO thumb(UID,BId)
VALUES
(1,4),
(2,2),
(2,3),
(3,5),
(3,7),
(4,1),
(6,3),
(6,5),
(7,1),
(7,2)
GO

insert into topday(Tyear,Tmonth,Tday,BID,TNO)
VALUES
(2019,3,27,1,1),
(2019,3,28,1,3),
(2019,3,29,1,6),
(2019,3,30,1,1),
(2019,4,1,1,5),
(2019,3,27,2,2),
(2019,3,28,2,4),
(2019,3,29,2,5),
(2019,3,27,3,6),
(2019,3,28,3,7),
(2019,3,27,4,3),
(2019,3,28,4,8)
GO