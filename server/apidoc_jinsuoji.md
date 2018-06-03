# 今琐记网络API

> 前言：本文档中出现了大量http请求响应，需要事先了解http协议里请求响应的结构。
> 直接拿idea的测试restfulapi的功能测得的，请求自己写的，响应是直接复制的收到的响应。

## 通用错误响应

### 找不到API 对应不是个API或方法错误

``` http
HTTP/1.1 400 Bad Request
X-Powered-By: Express
Content-Type: application/json; charset=utf-8
Content-Length: 35
ETag: W/"23-chzgsNYxNH1V9qP5/omlYiVtVZ4"
Date: Sat, 26 May 2018 11:42:06 GMT
Connection: keep-alive

{"error":"API_NOT_FOUND","data":""}

```

### 缺少必选参数

``` http
HTTP/1.1 400 Bad Request
X-Powered-By: Express
Content-Type: application/json; charset=utf-8
Content-Length: 42
ETag: W/"2a-riCR4jQPMEoaJJTGvG2ftP8Qlzs"
Date: Sat, 26 May 2018 12:41:57 GMT
Connection: keep-alive

{"error":"MISSING_ARGS","data":"username"}

```

## 注册API

### API概览

URL：`/register`

Method: `POST`

用途：注册。目前可随意注册。

参数：
- `username`(**必填**)：用户名
- `storedPassword`(**必填**)：用户名和密码的MD5

### 请求示例
``` http
POST http://localhost:3001/register
Content-Type: application/json

{"username":"jinsuoji","storedPassword":"3058316539bc9b77738128e37dc89728"}

```
其中storedPassword是'jinsuoji'（用户名）+'jinsuoji'（密码）的MD5.

### 响应示例

#### 创建成功
``` http
HTTP/1.1 200 OK
X-Powered-By: Express
Content-Type: application/json; charset=utf-8
Content-Length: 9
ETag: W/"9-D5Qd9FW/fsMQJ/dx8nLVGxKDEeI"
Date: Sat, 26 May 2018 11:50:10 GMT
Connection: keep-alive

"success"

```

#### 用户名过长
``` http
HTTP/1.1 413 Payload Too Large
X-Powered-By: Express
Content-Type: application/json; charset=utf-8
Content-Length: 91
ETag: W/"5b-f+3BU4dViBZHM7NiO/HcF9GTiZs"
Date: Sat, 26 May 2018 11:58:20 GMT
Connection: keep-alive

{"error":"USERNAME_TOO_LONG","data":"abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz"}

```
返回的data是过长的用户名.

#### 用户名已存在
``` http
HTTP/1.1 409 Conflict
X-Powered-By: Express
Content-Type: application/json; charset=utf-8
Content-Length: 41
ETag: W/"29-kM2uM+7qKbhzUcppajQtYCX/KJA"
Date: Sat, 26 May 2018 12:00:45 GMT
Connection: keep-alive

{"error":"USER_EXISTS","data":"jinsuoji"}

```

#### 用户名不合法
``` http
HTTP/1.1 400 Bad Request
X-Powered-By: Express
Content-Type: application/json; charset=utf-8
Content-Length: 41
ETag: W/"29-pB4Y58x3ZUI33KtgYkp0gKPqBNk"
Date: Thu, 31 May 2018 06:04:42 GMT
Connection: keep-alive

{"error":"USERNAME_INVALID","data":"../"}

```

## 预登录API

### API概览

URL: `/login_salt`

Method: `POST`

用途：预登录。为登录操作请求“盐”。

参数：
- `username`(**必填**)：用户名

### 请求示例
``` http
POST http://localhost:3001/login_salt
Content-Type: application/json

{"username": "jinsuoji", "encrypted": null}

```

### 响应示例

#### 成功

``` http
HTTP/1.1 200 OK
X-Powered-By: Express
Content-Type: application/json; charset=utf-8
Content-Length: 31
ETag: W/"1f-671cugqtVqEmpeUqJ7nt3FfQMDw"
Date: Sat, 26 May 2018 12:27:15 GMT
Connection: keep-alive

{"salt":"lOsgC3jYeTl39mwC6LcX"}

```

#### 用户名不存在

``` http
HTTP/1.1 404 Not Found
X-Powered-By: Express
Content-Type: application/json; charset=utf-8
Content-Length: 45
ETag: W/"2d-YhtTVQ+MTzFnmGsxqX84/fc+kNc"
Date: Sat, 26 May 2018 12:15:29 GMT
Connection: keep-alive

{"error":"USER_NOT_FOUND","data": "jinsuoji2"}

```

## 登录API

### API概览

URL: `/login`

Method: `POST`

用途：登录，验证登录或准备进行登录操作。

参数：
- `username`(**必填**)：用户名
- `salt`(**必填**)：预登录时获得的盐
- `encrypted`(**必填**)：salt+storedPassword的MD5
- `req`(**可选** *默认`false`*)：false=仅验证，true=请求token为下一步操作

### 请求示例

``` http
POST http://localhost:3001/login
Content-Type: application/json

{"username":"jinsuoji","encrypted":"0949a923ead03eac1e79fbfbfceb2130","salt":"lOsgC3jYeTl39mwC6LcX"}

```

### 响应示例

#### 验证成功（`req=false`）

``` http
HTTP/1.1 200 OK
X-Powered-By: Express
Content-Type: application/json; charset=utf-8
Content-Length: 9
ETag: W/"9-D5Qd9FW/fsMQJ/dx8nLVGxKDEeI"
Date: Sat, 26 May 2018 12:29:27 GMT
Connection: keep-alive

"success"

```

#### 请求token成功（`req=true`）

``` http
HTTP/1.1 200 OK
X-Powered-By: Express
Content-Type: application/json; charset=utf-8
Content-Length: 44
ETag: W/"2c-639gG4FBxTX0KJuZa6ZB+XsqAFY"
Date: Sat, 26 May 2018 12:37:06 GMT
Connection: keep-alive

{"token":"UJAEBCKARWFkqotW1WcNiypWT1RZpeQV"}

```

#### 盐使用过了或不是最新请求的或假的

``` http
HTTP/1.1 400 Bad Request
X-Powered-By: Express
Content-Type: application/json; charset=utf-8
Content-Length: 54
ETag: W/"36-Btd9c8J93XhV4guV5t38673MnSg"
Date: Sat, 26 May 2018 12:31:26 GMT
Connection: keep-alive

{"error": "SALT_EXPIRED","data": "lOsgC3jYeTl39mwC6LcX"}

```

#### 用户名或密码错误

``` http
HTTP/1.1 401 Unauthorized
X-Powered-By: Express
Content-Type: application/json; charset=utf-8
Content-Length: 43
ETag: W/"2b-udWyDnQkyoYmE6ami2qyxvHOO2E"
Date: Sat, 26 May 2018 12:40:31 GMT
Connection: keep-alive

{"error":"AUTHENTICATION_FAILED","data":""}

```

## 同步API

### API概述

URL: `/sync`

Method: `PUT`/`GET`

用途：向上同步，上传；向下同步，下载。

参数：无

**必填**请求头：Token：填写先前请求的Token

### 请求示例

``` http
GET http://localhost:3001/sync
Token: fFdszLXXr69binioHhkJam9CM5q7GAcE
Content-Type: application/json


```
``` http
PUT http://localhost:3001/sync
Token: UJAEBCKARWFkqotW1WcNiypWT1RZpeQV
Content-Type: application/json

{"todoList":[],"expenseList":[],"expenseCategoryList":[]}

```

### 返回示例

#### 上传成功
``` http
HTTP/1.1 201 Created
X-Powered-By: Express
Content-Type: application/json; charset=utf-8
Content-Length: 17
ETag: W/"11-HXdQV+XCIcjyNXgvBIk6FMoB5Jw"
Date: Sat, 26 May 2018 13:07:48 GMT
Connection: keep-alive

{"msg":"created"}

```

#### 下载成功
``` http
HTTP/1.1 200 OK
X-Powered-By: Express
Content-Type: application/json; charset=utf-8
Content-Length: 57
ETag: W/"39-S0GK2sFZ1tyKdf+ZNTF2spyb+Vg"
Date: Sat, 26 May 2018 13:20:22 GMT
Connection: keep-alive

{"todoList":[],"expenseList":[],"expenseCategoryList":[]}

```

#### token过期或无效
``` http
HTTP/1.1 401 Unauthorized
X-Powered-By: Express
Content-Type: application/json; charset=utf-8
Content-Length: 67
ETag: W/"43-YBI9ODOsLCDiKu8xOaMfo+zxg2M"
Date: Sat, 26 May 2018 12:51:01 GMT
Connection: keep-alive

{"error": "TOKEN_EXPIRED","data": "UJAEBCKARWFkqotW1WcNiypWT1RZpeQV"}

```

