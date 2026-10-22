1. Create User entity
	id
	email
	password
	role
	enabled
	createdAt
	updatedAt
2. Create Role enum
	USER
	ADMIN
3. Create UserRepository
	Spring Data JPA
	MySQL
4. Create DTOs
	UserRequest
	UserResponse
5. Create UserService
	Create user
	Find user
	Get all users
	Update user
	Delete user
6. Create UserController
	POST /users
	GET /users
	GET /users/{id}
	PUT /users/{id}
	DELETE /users/{id}
7. Add validation
	Email required/valid
	Password required
	Role validation
8. Test everything with Postman