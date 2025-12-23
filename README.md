# fbclone (Spring Boot + MySQL) — Facebook core + Messenger chat backend

Tính năng đã có (core):
- Auth: đăng ký / đăng nhập JWT (ROLE_USER, ROLE_ADMIN)
- Profile: xem profile, cập nhật profile của chính mình
- Newsfeed: /api/feed (public + bạn bè + bài của mình)
- Post: tạo/sửa/xoá (soft delete), privacy: PUBLIC / FRIENDS / ONLY_ME
- Comment: tạo/list/xoá (chủ comment hoặc chủ post hoặc admin)
- Reaction: like/love/haha/wow/sad/angry (upsert, 1 user 1 reaction / post)
- Friends: gửi lời mời kết bạn, accept/decline/cancel, list friends, unfriend
- Chat (Messenger):
  - Conversation DIRECT (1-1) & GROUP
  - Message REST: gửi + lấy lịch sử
  - Realtime WebSocket (STOMP): broadcast message theo conversation

## Yêu cầu
- Java 17
- Maven 3.9+
- MySQL chạy bằng XAMPP (hoặc MySQL cài riêng)

## Setup database (XAMPP)
1) Mở XAMPP -> Start MySQL
2) Vào phpMyAdmin -> tạo database: `fbclone` (charset utf8mb4)
3) Sửa `src/main/resources/application.yml` nếu user/pass MySQL của bạn khác.

## Chạy project
```bash
mvn spring-boot:run
```
- Health check: `GET http://localhost:8080/health`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

## Bootstrap admin
Trong `application.yml`:
```yml
app:
  bootstrap-admin:
    enabled: true
    username: "admin"
    email: "admin@local"
    password: "Admin@123456"
```
Lần chạy đầu sẽ tự seed role + tạo admin.

## Auth
- POST `/api/auth/register`
- POST `/api/auth/login`
- GET `/api/auth/me`

Header cho API cần login:
```
Authorization: Bearer <accessToken>
```

## WebSocket (realtime chat)
Endpoint:
- ws: `/ws` (có SockJS)

Client connect STOMP, gửi token qua header:
- Header: `Authorization: Bearer <token>`
hoặc
- Header: `token: <token>`

Send message:
- Destination gửi: `/app/conversations/{conversationId}/send`
- Topic nhận: `/topic/conversations/{conversationId}`

Payload:
```json
{
  "type": "TEXT",
  "content": "hello"
}
```

## Ghi chú về phân quyền (strict)
- ADMIN có thể: xem/sửa/xoá mọi post/comment, ban user, đổi role, xem mọi conversation/message (nếu muốn chặt hơn có thể tắt quyền này ở controller/service).
- USER chỉ được: thao tác dữ liệu của mình; xem FRIENDS post khi đã kết bạn; xem ONLY_ME chỉ của chính mình.
- Chat: chỉ member conversation mới xem/gửi được; group OWNER mới add/remove member (member vẫn có thể leave).

## Thêm chức năng tiếp theo
Bạn có thể mở rộng theo các module này:
- Notification + realtime
- Share/Repost
- Story/Status
- Group/Page
- Report + Moderation queue
- Upload file thực (S3/MinIO) thay vì chỉ lưu URL
