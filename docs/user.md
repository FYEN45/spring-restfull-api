# USER API SPEC

## Register User

Endpoint : POST /api/users/

Request Body :

```json
{
	"username": "ferrygun45",
	"password": "yourpassword",
	"name": "Ferry Gunawan"
}
```

Response Body (Success) :

```json
{
	"data": "OK"
}
```

Response Body (Failed) :

```json
{
	"errors": "Username must not blank, ???"
}
```

---

## Login User

Endpoint : POST /api/auth/login

Request Body :

```json
{
	"username": "ferrygun45",
	"password": "yourpassword"
}
```

Response Body (Success) :

```json
{
	"data": {
		"token": "TOKEN",
		"expiredAt": 12345678 // milliseconds
	}
}
```

Response Body (Failed, 401) :

```json
{
	"errors": "Username or Password wrong"
}
```

---

## Get User

Endpoint : GET /api/users/current

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
	"username": "ferrygun45",
	"name": "Ferry Gunawan"
}
```

Response Body (Failed, 401) :

```json
{
	"errors": "Unauthorized"
}
```

---

## Update User

Endpoint : PATCH /api/users/current

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body (Success) :

```json
{
	"name": "Ferry Gunawan", // put if only want to update name
	"password": "new password" // put if only want to update name
}
```

Response Body (Failed, 401) :

```json
{
	"errors": "Unauthorized"
}
```

---

## Logout User

Endpint : DELETE /api/auth/logout

Request Header :

- X-API-TOKEN : Token (Mandatory)

Response Body :

```json
{
	"data": "OK"
}
```
