-- Tạo cơ sở dữ liệu
CREATE DATABASE motel5;
USE motel5;

-- Tạo bảng tài khoản người dùng
CREATE TABLE accounts(
    account_id INT PRIMARY KEY IDENTITY(1,1),
    active BIT NOT NULL DEFAULT 1,
    avatar VARCHAR(255),
    citizen_id VARCHAR(255),
    create_date DATE DEFAULT GETDATE(),
    dob DATE,
    email VARCHAR(255) UNIQUE NOT NULL,
    fullname NVARCHAR(255),
    gender BIT,
    [password] VARCHAR(255),
    phone VARCHAR(255) NULL,
    [role] VARCHAR(50) DEFAULT 'user',
);

-- Tạo bảng nhà trọ
CREATE TABLE dbo.motels(
    motel_id INT PRIMARY KEY IDENTITY(1,1),
    create_date DATE DEFAULT GETDATE(),
    descriptions NVARCHAR(MAX),
    detail_address NVARCHAR(255),
    district NVARCHAR(255),
    district_id VARCHAR(255),
    image VARCHAR(255),
    province NVARCHAR(255),
    province_id VARCHAR(255),
    status BIT NOT NULL DEFAULT 1,
    ward NVARCHAR(255),
    account_id INT,
    FOREIGN KEY (account_id) REFERENCES dbo.accounts(account_id)
);

-- Tạo bảng danh mục phòng
CREATE TABLE dbo.category_room(
    category_room_id INT PRIMARY KEY IDENTITY(1,1),
    descriptions NVARCHAR(255),
    quantity INT,
    status BIT NOT NULL DEFAULT 1
);

-- Tạo bảng phòng trọ
CREATE TABLE dbo.motel_room(
    motel_room_id INT PRIMARY KEY IDENTITY(1,1),
    create_date DATE DEFAULT GETDATE(),
    descriptions NVARCHAR(255),
    length FLOAT CHECK (length >= 1),
    width FLOAT CHECK (width >= 1),
	room_price FLOAT,
    electricity_price FLOAT,
    water_price FLOAT,
    wifi_price FLOAT,
    status BIT NOT NULL DEFAULT 0,
    video VARCHAR(255),
    category_room_id INT,
    motel_id INT,
    room_status NVARCHAR(255),
    FOREIGN KEY (category_room_id) REFERENCES dbo.category_room(category_room_id),
    FOREIGN KEY (motel_id) REFERENCES dbo.motels(motel_id)
);

CREATE TABLE dbo.image(
    image_id [int] PRIMARY KEY IDENTITY(1,1),
    name [varchar](255) NULL,
    motel_room_id [int] NULL,
    FOREIGN KEY (motel_room_id) REFERENCES dbo.motel_room (motel_room_id)
    );


-- Tạo bảng người thuê
CREATE TABLE dbo.renter(
    renter_id INT PRIMARY KEY,
    change_room_date DATE,
    check_out_date DATE,
    renter_date DATE,
    motel_room_id INT,
    FOREIGN KEY (motel_room_id) REFERENCES dbo.motel_room(motel_room_id),
    FOREIGN KEY (renter_id) REFERENCES dbo.accounts(account_id)
);

-- Tạo bảng hóa đơn
CREATE TABLE dbo.invoice(
    invoice_id INT PRIMARY KEY IDENTITY(1,1),
    create_date DATE DEFAULT GETDATE(),
    end_date DATE,
    total_price FLOAT,
    invoice_status NVARCHAR(255),
    renter_id INT,
    motel_room_id INT,
    FOREIGN KEY (renter_id) REFERENCES dbo.renter(renter_id),
    FOREIGN KEY (motel_room_id) REFERENCES dbo.motel_room(motel_room_id)
);

-- Tạo bảng chỉ số điện
CREATE TABLE dbo.electricity(
    electricity_id INT PRIMARY KEY IDENTITY(1,1),
    create_date DATE DEFAULT GETDATE(),
    electricity_index FLOAT,
    invoice_id INT,
    FOREIGN KEY (invoice_id) REFERENCES dbo.invoice(invoice_id)
);

-- Tạo bảng chỉ số nước
CREATE TABLE dbo.water(
    water_id INT PRIMARY KEY IDENTITY(1,1),
    create_date DATE DEFAULT GETDATE(),
    water_index FLOAT,
    invoice_id INT,
    FOREIGN KEY (invoice_id) REFERENCES dbo.invoice(invoice_id)
);

-- Tạo bảng bài đăng
CREATE TABLE dbo.posts(
    post_id INT PRIMARY KEY IDENTITY(1,1),
    create_date DATE DEFAULT GETDATE(),
    status BIT NOT NULL DEFAULT 1,
    title NVARCHAR(255),
    motel_id INT,
    FOREIGN KEY (motel_id) REFERENCES dbo.motels(motel_id)
);

-- Tạo bảng bài đăng yêu thích
CREATE TABLE dbo.favorite_post(
    favorite_post_id INT PRIMARY KEY IDENTITY(1,1),
    create_date DATE DEFAULT GETDATE(),
    status BIT NOT NULL,
    account_id INT,
    post_id INT,
    FOREIGN KEY (account_id) REFERENCES dbo.accounts(account_id),
    FOREIGN KEY (post_id) REFERENCES dbo.posts(post_id)
);

CREATE TABLE dbo.request_authority(
    request_authority_id [int] PRIMARY KEY IDENTITY(1,1) NOT NULL,
    [image] [varchar](255) NULL,
    createdate [date] NULL,
    descriptions [nvarchar](255) NULL,
    respdescriptions [nvarchar](255) NULL,
    responsedate [date] NULL,
    account_id [int] NULL,
    request_authority_status [nvarchar](255) NULL,
    FOREIGN KEY (account_id) REFERENCES dbo.accounts (account_id));

CREATE TABLE notifications (
 notification_id INT PRIMARY KEY IDENTITY(1,1),
 user_id INT NOT NULL,
 message NVARCHAR(MAX) NULL,
 create_date DATETIME DEFAULT GETDATE(),
 FOREIGN KEY (user_id) REFERENCES accounts(account_id)
);
CREATE TABLE Account_Notifications (
    account_notification_id PRIMARY KEY IDENTITY(1,1),
    account_id INT,
    notification_id INT,
    create_date DATE DEFAULT GETDATE(),
    FOREIGN KEY (account_id) REFERENCES Accounts(account_id),
    FOREIGN KEY (notification_id) REFERENCES notifications(notification_id)
);
-- Tạo bảng đánh giá
CREATE TABLE dbo.rating (
    rating_id INT PRIMARY KEY IDENTITY(1,1),
    rating_value INT NOT NULL CHECK (rating_value BETWEEN 1 AND 5),
    create_date DATETIME DEFAULT GETDATE(),
    account_id INT,
    motel_id INT,
    motel_room_id INT,
    FOREIGN KEY (account_id) REFERENCES dbo.accounts(account_id),
    FOREIGN KEY (motel_id) REFERENCES dbo.motels(motel_id),
    FOREIGN KEY (motel_room_id) REFERENCES dbo.motel_room(motel_room_id)
);


-- Tạo bảng phản hồi
CREATE TABLE dbo.feedback (
    feedback_id INT PRIMARY KEY IDENTITY(1,1),
    feedback_text NVARCHAR(MAX) NOT NULL,
    create_date DATETIME DEFAULT GETDATE(),
    account_id INT,
    motel_id INT,
    motel_room_id INT,
    FOREIGN KEY (account_id) REFERENCES dbo.accounts(account_id),
    FOREIGN KEY (motel_id) REFERENCES dbo.motels(motel_id),
    FOREIGN KEY (motel_room_id) REFERENCES dbo.motel_room(motel_room_id)
);

-- Tạo người dùng quản trị mặc định
INSERT dbo.accounts (active, avatar, citizen_id, email, fullname, gender, [password], phone, [role]) VALUES 
(1, NULL, NULL, 'admin@gmail.com', 'ADMIN', 0, '$2a$10$uXuofG8QEf4SFRpdWSxt3u9U4/hEX4FPLj8rHdbPo9bipmRjMqVsy', NULL, 'admin');
INSERT INTO dbo.motels (create_date, descriptions, detail_address, district, district_id, image, province, province_id, status, ward, account_id)
VALUES (GETDATE(), N'Mô tả nhà trọ', N'Địa chỉ chi tiết', N'Quận/Huyện', '1234', 'hinhanh.jpg', N'Tỉnh/Thành phố', '5678', 1, N'Phường/Xã', 1);
SELECT * FROM dbo.motels;
INSERT INTO dbo.category_room (descriptions, quantity, status) VALUES
('Single Room', 10, 1),
('Double Room', 5, 1);
select *from category_room
select * from accounts