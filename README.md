# Cloud Web Hosting Server

## HW 개요
본 시스템은 사용자로부터의 요청을 처리하고, 가상 머신의 생성·삭제 및 상태 확인 등의 기능을 제공합니다. <br>
전체 시스템은 클라이언트 요청 처리, 인증 및 권한 제어, 가상 머신 관리, 데이터 저장의 <br>
4가지 주요 컴포넌트로 구성되어 있습니다.

## 주요 시스템 구성요소
![image](https://github.com/user-attachments/assets/1ee14af5-c196-4abd-881c-70c0a587face)
<br>
Spring Boot는 클라이언트의 VM 생성 또는 삭제 요청을 수신한 후, 사용자에게는 즉시 응답을 반환합니다. <br>
이후, Python gRPC 서버로 해당 요청을 Create / Delete gRPC Request형태로 전달합니다. <br>
Python 서버는 요청에 따라 다음 쉘 스크립트들을 실행합니다. <br>
- **make_image.sh**
- **create_vm.sh**
- **setup_vm.sh**
- **delete_vm.sh**

처리 완료 후, gRPC를 통해 결과를 Spring Boot에 전달합니다.
<br>
**VM 정보 요청 (GET /host)** 

Spring Boot에서 직접 쉘 스크립트 check_vm.sh를 실행하여 현재 VM 상태를 확인합니다. <br>
스크립트 실행 결과를 사용자에게 응답으로 반환합니다.

## **System Architecture**
![image](https://github.com/user-attachments/assets/44d0263f-0434-4803-8ef7-e25fe51041df)

## **Directory**
```
📦CloudApiGateway
 ┣ 📂src
 ┃ ┣ 📂main
 ┃ ┃ ┣ 📂java
 ┃ ┃ ┃ ┗ 📂kr
 ┃ ┃ ┃ ┃ ┗ 📂ac
 ┃ ┃ ┃ ┃ ┃ ┗ 📂dankook
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂CloudApiGateway
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂config
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂principal
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜PrincipalDetails.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜PrincipalDetailsService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜CorsConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜SecurityConfig.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂controller
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜AuthController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜HostingController.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂dto
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂request
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜LoginRequest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜RegisterRequest.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📂response
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ApiMessageResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ApiResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜SshInfo.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜TokenResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜VmInfoResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂entity
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜BaseEntity.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜Member.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜Vm.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂exception
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ApiErrorCode.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ApiException.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ErrorCode.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜ErrorResponse.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜GlobalExceptionHandler.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜TokenErrorCode.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜ValidationException.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂jwt
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜JwtAccessDeniedHandler.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜JwtAuthenticationEntryPoint.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜JwtErrorResponseHandler.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜JwtFilter.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜JwtTokenProvider.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂repository
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜MemberRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜VmRepository.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📂service
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜AuthService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┣ 📜HostingService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜VmGrpcService.java
 ┃ ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜CloudApiGatewayApplication.java
 ┃ ┃ ┣ 📂proto
 ┃ ┃ ┃ ┗ 📜vm.proto
 ┃ ┃ ┗ 📂resources
 ┃ ┃ ┃ ┗ 📜application.properties
 ┣ 📜build.gradle
 ┣ 📜gradlew
 ┗ 📜settings.gradle
📦scripts
 ┣ 📜check_vm.sh
 ┣ 📜create_vm.sh
 ┣ 📜delete_vm.sh
 ┣ 📜initial_setting.sh
 ┣ 📜initial_table.sql
 ┣ 📜make_image.sh
 ┗ 📜setup_vm.sh
📦vm-controller
 ┣ 📜app.py
 ┗ 📜vm.proto
```

## **Settings**
### Ubuntu 기본 환경 구축
#### 사용자 홈 디렉토리로 이동
```Bash
cd ~
sudo apt update
```
#### 프로젝트 clone 및 해당 프로젝트로 이동
```Bash
git clone https://github.com/ohchanKyu/cloud-web-hosting-server.git
cd ./cloud-web-hosting-server
```
#### 필요한 패키지 추가 설치 및 설정 스크립트 실행
```Bash
cd ./scripts
chmod +x *.sh
./initial_setting.sh
```
![image](https://github.com/user-attachments/assets/21c44a74-8fdc-4ced-a2d7-88fa9ab29dc6)

#### Nginx 버전 및 상태 확인
```Bash
nginx -v
sudo systemctl status nginx
sudo netstat -tuln | grep :80
```
#### Nginx 생성된 사이트 설정 확인
```Bash
sudo cat /etc/nginx/sites-available/common
```
#### 환경변수 적용 및 iptable / 방화벽 설정
```Bash
source ~/.bashrc
sudo iptables --policy FORWARD ACCEPT
sudo ufw disable
```

<br>

### MySQL 설치 및 설정
#### MySQL 설치 및 버전확인
```Bash
sudo apt-get install mysql-server
mysql --version
```
#### MySQL 초기 보안설정
```Bash
sudo mysql_secure_installation
VALIDATE PASSWORD COMPONENT → no
Remove anonymous users → yes
Disallow root login remotely → yes
Remove test database and access to it? → yes
Reload priviledge tables now → yes
```
#### MySQL 접속 및 root 비밀번호 설정
```Bash
sudo mysql -u root 
ALTER USER ‘root’@’localhost’ IDENTIFIED WITH mysql_native_password by ‘1379’;
quit
```
#### 홈 디렉터리로 이동 후 MySQL 재접속
```Bash
cd ~
sudo mysql -u root -p
> password 1379
```
#### MySQL Script 파일 적용
```Bash
source ./cloud-web-hosting-server/scripts/initial_table.sql;
show databases;
exit
```
<br>

### Spring boot 어플리케이션 실행
```Bash
cd ~/cloud-web-hosting-server/CloudApiGateway
chmod +x gradlew
./gradlew generateproto
./gradlew build
cd ./build/libs
java -jar CloudApiGateway-0.0.1-SNAPSHOT.jar
```
<br>

### Python 어플리케이션 실행
```Bash
cd ~/cloud-web-hosting-server/vm-controller
source venv/bin/activate
ls
python3 app.py
```

<br>

## API Endpoints

#### **POST** `/register`
- **Description**: 사용자 회원가입
- **Authentication**: Not required.
- **Curl**
```Bash
curl -X POST http://localhost/register \
-H "Content-Type: application/json" \
-d '{
  "name": "User1",
  "userId": "user1234",
  "password": "!user1234A"
}'
```
- **Success Response**
```JS
Status: 201
{
   "success": true,
   "statusCode": 201,
   "message": "Registration was successful."
}
```
- **Error Response**
```JS
Status: 400 - 이름, 아이디, 비밀번호가 누락된 경우
{
  "timestamp": "2025-06-03T03:01:26.086188293",
  "statusCode": 400,
  "success": false,
  "message": "ID is required." | "message": "Name is required." | "message": "Password is required."
}

Status: 400 - 필드 유효성 검사에 실패한 경우
{
  "timestamp": "2025-06-03T03:01:26.086188293",
  "statusCode": 400,
  "success": false,
  "message": "Id must be between 7 and 30 characters." |
  "message": "Password must be at least 8 characters long. and
   password must include at least one number and one special character."
}

Status: 400 - 가입 아이디가 중복되는 경우
{
  "timestamp": "2025-06-03T03:01:26.086188293",
  "statusCode": 400,
  "success": false,
  "message": "This is duplicated Id."
}
```
<br>

#### **POST** `/login`
- **Description**: 사용자 로그인
- **Authentication**: Not required.
- **Curl**
```Bash
curl -X POST http://localhost/login \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user1234",
    "password": "!user1234A"
}'
```
- **Success Response**
```JS
Status: 200
{
  "statusCode": 200,
  "success": true,
  "data": { “accessToken” : "eyJhbGciOi..." }
}
```
- **Error Response**
```JS
Status: 400 - 아이디, 비밀번호가 누락된 경우
{
  "timestamp": "2025-06-03T03:01:26.086188293",
  "statusCode": 400,
  "success": false,
  "message": "ID is required." | "message": "Password is required."
}

Status: 404 - 입력한 사용자 아이디에 해당하는 사용자 엔티티가 없는 경우
{
  "timestamp": "2025-06-03T03:01:26.086188293",
  "statusCode": 404,
  "success": false,
  "message": "Not found member with your primary key or userId" 
}

Status: 401 - 입력한 사용자 아이디에 해당하는 인테티는 존재하지만 패스워드와 일치하지 않는 경우
{
  "timestamp": "2025-06-03T03:01:26.086188293",
  "statusCode": 401,
  "success": false,
  "message": "There is no member matching the provided username and password."
}
```
<br>

#### **POST** `/host`
- **Description**: 사용자 VM 생성
- **Authentication**: Required.
- **Curl**
```Bash
curl -X POST http://localhost/host \
-H "Authorization: Bearer <ACCESS_TOKEN>"
```
- **Success Response**
```JS
Status: 201
{
  "success": true,
  "statusCode": 201,
  "message": “The VM has been successfully created.Please check the VM status via a GET /host request 
   and connect afterward.”
}
```
- **Error Response**
```JS
Status: 401 - JWT 토큰 누락 및 만료된 JWT 토큰 사용
{
  "timestamp": "2025-06-03T03:01:26.086188293",
  "statusCode": 401,
  "success": false,
  "message": "AccessToken is Required." | "message": "AccessToken is expired."
}

Status: 409 - 사용자가 2개 이상의 호스팅을 요구 ( 사용자당 1개 호스팅 제한 )
{
  "timestamp": "2025-06-03T03:01:26.086188293",
  "statusCode": 409,
  "success": false,
  "message": "Already exist your VM." 
}
```

<br>

#### **GET** `/host`
- **Description**: 사용자 VM 조회
- **Authentication**: Required.
- **Curl**
```Bash
curl -X GET http://localhost/host \
-H "Authorization: Bearer <ACCESS_TOKEN>"
```
- **Success Response**
```JS
Status: 200
{
  "statusCode": 200,
  "success": true,
  "data": {
    "vmId": "5DQc7CtQQm-G-7n5EjBadg",
    "status": "running",
    "webUrl": "http://220.149.241.197/5DQc7CtQQm-G-7n5EjBadg/",
    "ssh": {
      "user": "ubuntu",
      "host": "220.149.241.197",
      "port": 24314,
      "command": "ssh ubuntu@220.149.241.197 -p 24314"
    }
  }
}
```
**Response Description**

| 속성명        | 타입       | 설명                                |
|---------------|------------|-------------------------------------|
| **`success`** | `boolean`  | 요청이 성공했는지 여부 (`true` 또는 `false`). |
| **`statusCode`** | `integer` | HTTP 상태 코드 (예: `400`, `404`, `500`). |
| **`data`** | `Object` | 요청에 대한 응답 데이터 객체 |
| **`vmId`** | `string` | 생성된 vmId ( 사용자 고유의 random Id ) |
| **`status`** | `string` | VM의 현재 상태 |
| **`webUrl`** | `string` | 해당 VM에 접속할 수 있는 URL |
| **`ssh`** | `Object` | ssh 접속을 위한 정보를 가지는 객체 |
| **`user`** | `string` |  원격 서버에 접속할 때 사용할 사용자 계정 이름 |
| **`host`** | `string` |  접속할 원격 서버의 공인 IP 주소 |
| **`port`** | `integer` |  SSH 접속에 사용할 포트 번호 |
| **`command`** | `string` |  터미널에서 실제로 입력할 SSH 접속 명령어 |

- **Error Response**
```JS
Status: 401 - JWT 토큰 누락 및 만료된 JWT 토큰 사용
{
  "timestamp": "2025-06-03T03:01:26.086188293",
  "statusCode": 401,
  "success": false,
  "message": "AccessToken is Required." | "message": "AccessToken is expired."
}

Status: 404 - 사용자가 생성한 VM이 존재하지 않는 경우
{
  "timestamp": "2025-06-03T03:01:26.086188293",
  "statusCode": 404,
  "success": false,
  "message": "Not found your vm instance." 
}
```
<br>

#### **DELETE** `/host`
- **Description**: 사용자 VM 삭제
- **Authentication**: Required.
- **Curl**
```Bash
curl -X DELETE http://localhost/host \
-H "Authorization: Bearer <ACCESS_TOKEN>"
```
- **Success Response**
```JS
Status: 200
{
 "success": true,
 "statusCode": 201,
 "message": “Your VM has been successfully deleted. To create a new VM, please send a POST request to /host.”
}
```
- **Error Response**
```JS
Status: 401 - JWT 토큰 누락 및 만료된 JWT 토큰 사용
{
  "timestamp": "2025-06-03T03:01:26.086188293",
  "statusCode": 401,
  "success": false,
  "message": "AccessToken is Required." | "message": "AccessToken is expired."
}

Status: 404 - 사용자가 생성한 VM이 존재하지 않는 경우
{
  "timestamp": "2025-06-03T03:01:26.086188293",
  "statusCode": 404,
  "success": false,
  "message": "Not found your vm instance." 
}
```

## 사용자의 VM 생성 확인을 위한 웹 브라우저 접속 및 쉘 접속 확인

### 웹 브라우저 접속
- 접속 테스트에 사용된 사설 IP : 10.0.10.175
- 실제 VM의 공인 IP : 220.149.241.197
<br>

아래는 웹 브라우저를 통해 사설 IP로 접속한 화면입니다. <br>
이는 사설 네트워크 상에서 VM이 정상적으로 실행되고 있으며, 외부 요청에 응답 가능한 상태임을 의미합니다.
```Bash
http://10.0.10.175/5DQc7CtQQm-G-7n5EjBadg/
```
![image](https://github.com/user-attachments/assets/ae536279-0a8f-4c96-a354-fe991b310692)
※ Nginx 설정이 모두 완료될 때까지 시간이 걸릴 수 있습니다.

<br>

### SSH를 이용한 쉘 접속
```Bash
ssh ubuntu@10.0.10.175 -p 24314
```
![image](https://github.com/user-attachments/assets/bb2e712b-3a04-4869-bbe5-c6dc4696b6e6)

```Bash
계정 이름 : Ubuntu
계정 비밀번호 : Ubuntu
```
![image](https://github.com/user-attachments/assets/49891bc6-91b4-42ac-bec7-c0f1bdf6cb6f)


#### 생성된 VM에서 Nginx 설정 확인
```Bash
nginx -v
sudo systemctl status nginx
```
![image](https://github.com/user-attachments/assets/0a0dca37-236e-421d-ae24-6fb25b861626)

```Bash
cd /etc/nginx/sites-available
cat default
```
![image](https://github.com/user-attachments/assets/3850a9c3-e32a-4b9c-99ee-dece2f191c15)

```Bash
cat /var/www/html/<생성된 VM ID>/index.html
```
![image](https://github.com/user-attachments/assets/d76e42d9-e242-4b8f-95a5-bc8f1de58453)
![image](https://github.com/user-attachments/assets/e536660c-9f03-4614-a5fc-9f92a527130e)


