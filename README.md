# Social Platform API - Backend Test

A production-ready social media platform API built with Spring Boot, featuring dual database support (PostgreSQL/MongoDB), JWT authentication, content moderation, and post interactions.

## Quick Setup

### Prerequisites
- Java 21+
- Maven
- Docker & Docker Compose

### 1. Start Databases

```bash
docker-compose up -d
```

This starts:
- PostgreSQL on port `5332` (mapped from 5432)
- MongoDB replica set on port `27017`

### 2. Run the Application

**With PostgreSQL (default):**
```bash
mvn spring-boot:run
```

**With MongoDB:**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=mongodb
```

The API runs on `http://localhost:8080`

### 3. Default Admin Account
```
Username: admin
Password: admin123
```

## Database Profiles

The application supports two database backends. Switch between them using Spring profiles:

### PostgreSQL Profile
- **Profile:** `postgres` (default)
- **Port:** 5332
- **Database:** socials_db
- **Config:** `application-postgres.properties`

```properties
spring.profiles.active=postgres
```

### MongoDB Profile  
- **Profile:** `mongodb`
- **Port:** 27017
- **Database:** socials_db
- **Config:** `application-mongodb.properties`

```properties
spring.profiles.active=mongodb
```

**Note:** The application automatically creates required schemas/collections on startup.

## API Structure

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login (returns JWT token)
- `GET /api/auth/me` - Get current user info

### User Posts
- `POST /api/posts` - Create post (auto-status: UNDER_APPROVAL)
- `GET /api/posts/feed` - Public feed (APPROVED posts only)
- `GET /api/posts/my-posts` - User's posts (all statuses)

### Admin Moderation
- `GET /api/admin/posts` - All posts
- `PUT /api/admin/posts/{id}/approve` - Approve post
- `PUT /api/admin/posts/{id}/reject` - Reject post (requires reason)

### Interactions (Bonus)
- `PUT /api/interactions/like/posts/{id}` - Like post
- `PUT /api/interactions/unlike/posts/{id}` - Unlike post
- `PUT /api/interactions/comments/posts/{id}` - Comment on post
- `DELETE /api/interactions/comments/{id}` - Delete comment

## Advanced Filtering

The API supports dynamic filtering using query parameters with operators.

### Query Format
```
?filter.{field}={operator}::{value}
```

### Available Operators
- `eq` - Equals
- `ne` - Not equals
- `gt` - Greater than
- `lt` - Less than
- `regex` - Contains (text search)

### Example 1: Get Posts Under Approval (Moderation Queue)

```http
GET /api/admin/posts?filter.status=eq::UNDER_APPROVAL
Authorization: Bearer {admin_token}
```

This filters posts where `status` equals `UNDER_APPROVAL`, returning only posts awaiting moderation.

### Example 2: Search Posts by Content

```http
GET /api/admin/posts?filter.content=like::test&filter.status=eq::APPROVED
Authorization: Bearer {admin_token}
```

This returns approved posts containing "test" in their content.

### Additional Query Parameters

**Pagination:**
```http
GET /api/posts/feed?page=1&limit=20
```

**Sorting:**
```http
GET /api/posts/feed?sortBy=createdAt&sortDirection=desc
```

**Combined:**
```http
GET /api/admin/posts?filter.status=eq::UNDER_APPROVAL&page=1&limit=10&sortBy=createdAt&sortDirection=desc
```

## Architecture Highlights

### Dual Database Support
- **Repository Pattern** with `IUserRepository`, `IPostRepository` interfaces
- Profile-based implementations: `UserRepository` (PostgreSQL) and `UserMongoRepository` (MongoDB)
- Seamless switching without code changes

### Security
- JWT-based authentication
- Role-based access control (`USER`, `ADMIN`)
- `@PreAuthorize` annotations for endpoint protection

### Data Model
- **BaseEntity/BaseDocument** - Common fields (id, timestamps, soft delete)
- **User** - Authentication and profile
- **Post** - Content with status workflow (UNDER_APPROVAL → APPROVED/REJECTED)
- **PostLike** - Like tracking
- **PostComment** - Comment management

### Business Logic
1. New posts automatically set to `UNDER_APPROVAL`
2. Public feed shows only `APPROVED` posts
3. Admins can approve/reject with reason tracking
4. Atomic counter updates for likes/comments
5. Soft delete for all entities

## Testing with requests.http

The included `requests.http` file contains a complete workflow:

1. **Register** a new user
2. **Login** to get JWT token (auto-saved as `{{userToken}}`)
3. **Create posts** (starts as UNDER_APPROVAL)
4. **View your posts** (all statuses)
5. **Admin login** (get `{{adminToken}}`)
6. **Moderation queue** (filter UNDER_APPROVAL)
7. **Approve/Reject** posts
8. **Public feed** (only APPROVED posts visible)
9. **Like/Comment** on approved posts

## Project Structure

```
src/main/java/com/mayar/social_platform/
├── common/
│   ├── config/          # Security, MongoDB config
│   ├── security/        # JWT util, filters, UserPrincipal
│   ├── query/           # Query builder for filtering
├── modules/
│   ├── user/
│   │   ├── controller/  # AuthController
│   │   ├── service/     # UserService
│   │   ├── repository/  # Dual implementations
│   │   └── entity/      # User, UserDocument
│   └── post/
│       ├── controller/  # UserPostController, AdminPostController
│       ├── service/     # PostService, PostInteractionService
│       ├── repository/  # Post, Like, Comment repositories
│       └── entity/      # Post, PostDocument, etc.
```

## Key Features Implemented

✅ User registration & JWT authentication  
✅ Post creation with automatic moderation workflow  
✅ Public feed (approved posts only)  
✅ User's post tracker (all statuses)  
✅ Admin moderation queue with filtering  
✅ Approve/Reject posts with reason tracking  
✅ Like/Unlike posts (atomic counters)  
✅ Comment on posts 
✅ Advanced filtering with multiple operators  
✅ Dual database support (PostgreSQL/MongoDB)   
✅ Audit fields (reviewedBy, timestamps)

## Environment Variables

Default values are set in `application.properties`. Override if needed:

```bash
# PostgreSQL
export DB_HOST=localhost
export DB_PORT=5332
export DB_NAME=socials_db

# MongoDB  
export MONGO_HOST=localhost
export MONGO_PORT=27017

# JWT
export JWT_SECRET=your-secret-key-here

# Admin
export ADMIN_USERNAME=admin
export ADMIN_PASSWORD=admin123
```

## Notes

- PostgreSQL port is mapped to `5332` (not default 5432) to avoid conflicts
- MongoDB uses replica set for production readiness
- Default admin user is created on first startup
- All timestamps are in UTC
