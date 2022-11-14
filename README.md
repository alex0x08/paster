Paster
==================================

Small text&code review tool.

![alt text](https://github.com/alex0x08/paster/blob/develop/docs/images/paster-demo-video.gif?raw=true)

Project description
-------------------

Paster is small standalone web-based review tool.

Project was initially created for internal usage, to make quick reviews on 'raw' data like
XML and json samples, logs, various code samples.

Documentation
-------------
TODO

Building
========
You will need Java 18 or later to build and run Paster.
To build, run from parent folder:

``` shell
  mvn -P Prod clean install
```
Paster distribution archive will be located in

    `paster-dist/target/paster-dist-${version}-bin.zip`.

The first build may take a longer than expected as Maven downloads all the dependencies.

The build tests do a lot of stress testing, and on some machines it is necessary to set the file descriptor limit to greater than 2048 for the tests to all pass successfully.

It is possible to bypass tests by building with `mvn clean install -DskipTests`.

Professional Services
---------------------

Expert advice and production support are available through [Webtide.com](https://webtide.com).


Пастер
==================================
Мелкая софтвина для ревью кода, логов и всяких левых json/xml.

Выглядит как-то так:
![alt text](https://github.com/alex0x08/paster/blob/develop/docs/images/paster2-screenshot1.jpg?raw=true)

Описание
-------------------
Это веб-приложение, поэтому нужен браузер, желательно новый.
Поскольку мы используем ES6 сразу в браузере, на старом Пастер просто не заработает.

Создавалось оно еще в 2011м, для проведения внутреннего ревью 'здесь и сейчас'
и развернутых ответов по кускам логов, каким-то json-конфигам и xml запросам, взятым непонятно откуда.

Основной кейс использования: подчеркнуть красным нужное место в логе с ошибкой и вставить ниже комментарий с решением.


Документация
-------------
КОГДА-НИБУДЬ БУДЕТ

Сборка
========
Для сборки и работы нужна Java версии 18 и старше.
Еще нужен Apache Maven, если вдруг будете собирать не из среды разработки.

Запуск продуктовой сборки:

``` shell
  mvn -P Prod clean install
```
ZIP-архив с дистрибьютивом будет в

    `paster-dist/target/paster-dist-${version}-bin.zip`.

Первая сборка будет дольше ожидаемого, тк будут скачиваться зависимые библиотеки.
