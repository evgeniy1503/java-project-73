### Hexlet tests and linter status:
[![Actions Status](https://github.com/evgeniy1503/java-project-73/workflows/hexlet-check/badge.svg)](https://github.com/evgeniy1503/java-project-73/actions)
[![Java CI](https://github.com/evgeniy1503/java-project-73/actions/workflows/workflows.yml/badge.svg)](https://github.com/evgeniy1503/java-project-73/actions/workflows/workflows.yml)
<a href="https://codeclimate.com/github/evgeniy1503/java-project-73/maintainability"><img src="https://api.codeclimate.com/v1/badges/59f5d8fcd87ee1c55fed/maintainability" /></a>
<a href="https://codeclimate.com/github/evgeniy1503/java-project-73/test_coverage"><img src="https://api.codeclimate.com/v1/badges/59f5d8fcd87ee1c55fed/test_coverage" /></a>

<h2>Описание</h2>
<p><a href="https://taskmanager-sb8q.onrender.com/"><u><b>Task Manager</b></u></a> – система управления задачами. Она позволяет ставить задачи, назначать исполнителей и менять их статусы. Для работы с системой требуется регистрация и аутентификация.</p>

<h5><a href="https://taskmanager-sb8q.onrender.com/signup">Регистрация и Авторизация пользователя</a></h5>

<h5><a href="https://taskmanager-sb8q.onrender.com/labels">Список меток, статусов для задач</a></h5>

<h5><a href="https://taskmanager-sb8q.onrender.com/tasks">Список задач с возможностью фильтрации</a></h5>

<h5><a href="https://taskmanager-sb8q.onrender.com/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config">Задокументированные методы API (контроллеров для пользователей, задач и меток)</a></h5>

### Технологии

1. Java 17
2. Spring Boot, WVC, Data
3. Swagger, Lombok
4. Gradle
5. Liquibase
6. Spring Security, JWT

### Разработка

Прежде чем Вы сможете собрать этот проект, Вы должны установить и настроить следующие зависимости на своем компьютере:

1. JDK 17
2. Gradle 7.4
3. Node.js 16.13.1

### Сборка проекта

```bash
make build
```

## Тестирование проекта

Для запуска теста приложения, запустите:

```bash
make test
```

## Запустите приложение с локальной базой данных

```bash
make start
```