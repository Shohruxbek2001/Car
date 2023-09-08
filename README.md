# Справочник автомобилей


## Использованные технологии
- Scala
- skunk
- typelevel
- http4s
- psql

### Установка зависимостей
```bash
sbt install
```

### Запуск
```bash
sbt run
```
### Первым делом нужно создать
- Макри
- Годы выпусков
- Цвета
### Потом можно создать машину с помощью id(марки, года, цвета) 
```json
{
	"number": "05M313SD",
	"markId": "b26996df-59e8-4850-adda-7db8b5be0256",
	"colorId": "f8e7d89e-695c-482c-9a93-8fcbe68380ef",
	"yearId": "b742c904-3d35-430e-bafa-01516695b80c"
}
```
### В фильтрации все параметры опциональны. Можно применят фильтр по всем необходимым параметрам:
```json
{
  "year": "2010"
}
```
```json
{
  "mark": "Chevrolet"
}
```
```json
{
  "year": "2010",
  "mark": "Chevrolet",
  "color": "Black"
}
```

### Чтобы соединить базы заполните поля в файле main.scala
```scala
    Session.single[IO]( // (2)
      host = "localhost",
      port = 5432,
      user = "test",
      database = "test",
      password = Some("Password")
    )
```