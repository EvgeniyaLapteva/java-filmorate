# Java-filmorate
## Диаграмма базы данных проекта
![image](https://github.com/EvgeniyaLapteva/java-filmorate/blob/2053f32f6f46e56c66d41eba1155757f21a28355/src/main/resources/diagram.png)
***

_Сервис для подбора фильма на вечер на основе рекомендаций от других пользователей._

**Стек: Rest-сервисы с использованием Spring Boot, Maven, Lombok и взаимодействие с БД.**

### Примеры запросов к базе данных

1. Получение списка всех фильмов
```
SELECT *
FROM films;
```
2. Получение фильма по id
```
SELECT *
FROM films
WHERE film_id = {id};
```
3. Получение списка популярных фильмов
```
SELECT f.name,
        COUNT(l.user_id) AS likes_count
FROM likes AS l
RIGHT OUTER JOIN films AS f ON l.film_id=f.film_id
GROUP BY f.name
ORDER BY likes_count DESC
LIMIT {count};
```
4. Получение списка всех пользователей
```
SELECT *
FROM users;
```
5. Получение пользователя по id
```
SELECT *
FROM users
WHERE user_id = {id};
```
6. Получение списка друзей пользователя по id
```
SELECT *
FROM users
WHERE user_id IN (SELECT friend_id
                FROM friendship
                WHERE user_id = {id}
                AND status = 'true');
```
7. Получение списка общих друзей пользователей {id} и {other_id}
```
SELECT friend_id
From Friendship
WHERE user_id = {id}
AND status = 'true'
AND friend_id IN (SELECT friend_id
                FROM friendship
                WHERE user_id = {other_id}
                AND status = 'true'
                AND friend_id <> {id})
AND friend_id <> {other_id};
```


