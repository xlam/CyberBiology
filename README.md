[![Build Status](https://travis-ci.org/xlam/CyberBiology.svg?branch=master)](https://travis-ci.org/xlam/CyberBiology)
[![GitHub release](https://img.shields.io/github/release/xlam/CyberBiology.svg)](https://github.com/xlam/CyberBiology/releases/latest)

# CyberBiology

Проект "Искусственная жизнь", изначально представленный на канале [foo52ru](https://www.youtube.com/watch?v=PCx228KcOow), и впервые переписанный на Java пользователем CyberBi: [CyberBiology/CyberBiology](https://github.com/CyberBiology/CyberBiology).

## Системные требования

Для установки и запуска требуются установленные и настроенные для Вашей системы:
- [Java 8 JDK](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [git](https://git-scm.com/)
- [maven](https://maven.apache.org/)

## Установка и запуск

### Запуск готового JAR файла

1. Скачайте последний релиз по ссылке https://github.com/xlam/CyberBiology/releases/latest
2. Запустите (X.X.X - версия скачанного JAR файла)
```
java -jar cyberbiology-X.X.X.jar
```

### Сборка из исходных кодов

1. Клонирование репозитория
```bash
  git clone https://github.com/xlam/CyberBiology.git
```
2. Переход в каталог проекта
```bash
  cd CyberBiology
```
4. Сборка приложения
```bash
  mvn clean package
```
4. Запуск
```bash
  mvn exec:java
```
## Скриншоты

![20181128_cb_02](https://user-images.githubusercontent.com/179263/49165762-5f31dc00-f34b-11e8-8fd0-450b10b2d0b5.png)

![20181213_cb_07](https://user-images.githubusercontent.com/179263/49951639-d5217000-ff13-11e8-81bd-c092c9e2eb9d.png)

![20181213_cb_08](https://user-images.githubusercontent.com/179263/49951652-da7eba80-ff13-11e8-8cae-4dc19c13b676.png)

![20181128_cb_01](https://user-images.githubusercontent.com/179263/49165619-095d3400-f34b-11e8-886d-4c35a2189855.png)
