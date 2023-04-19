# Java-filmorate
## Диаграмма базы данных проекта
![image](https://github.com/EvgeniyaLapteva/java-filmorate/blob/bb3b69ddca70edb3798b2e68e8356a95f860542d/src/main/resources/diagram.png)
***
### Примеры запросов к базе данных

1. Получение списка всех фильмов
```
SELECT *
FROM Films;
```
2. Получение фильма по id
```
SELECT *
FROM Films
WHERE film_id = {id};
```
3. Получение списка популярных фильмов
```
SELECT f.name,
        COUNT(l.user_id) AS likes_count
FROM Likes AS l
RIGHT OUTER JOIN Films AS f ON l.film_id=f.film_id
GROUP BY f.name
ORDER BY likes_count DESC
LIMIT {count};
```
4. Получение списка всех пользователей
```
SELECT *
FROM Users;
```
5. Получение пользователя по id
```
SELECT *
FROM Users
WHERE user_id = {id};
```
6. Получение списка друзей пользователя по id
```
SELECT user_id
FROM Users
WHERE user_id IN (SELECT friend_id
                FROM Friendship
                WHERE user_id = {id}
                AND status = 'true')
OR user_id IN (SELECT user_id
                FROM Friendship
                WHERE friend_id = {id}
                AND status = 'true');
```
7. Получение списка общих друзей пользователей {id} и {other_id}
```
SELECT friend_id
From Friendship
WHERE user_id = {id}
AND status = 'true'
AND friend_id IN (SELECT friend_id
                FROM Friendship
                WHERE user_id = {other_id}
                AND status = 'true'
                AND friend_id <> {id})
AND friend_id <> {other_id};
```


