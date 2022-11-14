Paster
==================================
Small text&code review tool.

![alt text](https://github.com/alex0x08/paster/blob/develop/docs/images/paster-demo-video.gif?raw=true)

Project description
-------------------

Paster is small standalone web-based review tool.

Project was initially created for internal usage, to make quick reviews on 'raw' data like
XML and json samples, logs, various code samples.

Technical details
---------------
By historical reasons, Paster is based on very old technologies like
JSP/JSTL and Apache Tiles3.
So to keep running it was required to resurrect Tiles3 and adopt it for modern JEE, because Apache Tiles is not maintained anymore and stopped somewhere on Servlet 3.0 API.

Same story was with key UI framework: we used Mootools initially, with lots of plugins.
But since Mootools is not maintained anymore - EVERYTHING on client side been rewritten to pure ES6.

But now, you can see something completely unbelievable: 
JSP/JSTL +Tiles3 web application running on super modern Jakarta 9.1 API.
With client javascript migrated to ES6 in browser, hell yeah!

BTW, did I mention that all Paster's backend is on Scala? 
Suppose you're not surprised  ;)

And if needed, we can help with such upgrade of your outdated project too.
[Contact Us](mailto:alex3.145@gmail.com)

Documentation
-------------


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

Expert advice and production support are available through [0x08.site](https://0x08.site).


Пастер
==================================
Вебприложение для проведения ревью кода, логов и неструктурированных данных вроде json/xml.

Выглядит как-то так:
![alt text](https://github.com/alex0x08/paster/blob/develop/docs/images/paster2-screenshot1.jpg?raw=true)

Описание
-------------------
Это веб-приложение, поэтому нужен браузер, желательно новый.
Поскольку мы используем ES6 сразу в браузере, на старом Пастер просто не заработает.

Создавалось оно еще в 2011м, для проведения внутреннего ревью 'здесь и сейчас'
и развернутых ответов по кускам логов, каким-то json-конфигам и xml запросам, взятым непонятно откуда.

Основной кейс использования: подчеркнуть красным нужное место в логе с ошибкой и вставить ниже комментарий с решением.

Технические детали
---------------
По историческим причинам, проект Пастера основан на 
очень сильно устаревших технологиях: JSP/JSTL и Apache Tiles
Чтобы продолжать его поддерживать, пришлось самостоятельно
оживлять заброшенный проект Apache Tiles и приделывать ему
поддержку Jakarta EE 9.1

Дальше Tiles был сильно порезан, были выкинуты практически все зависимости и все что не используется в Пастере.
Затем по коду прошлись различными анализаторами, убрали все ошибки и перевели на 11ю версию Java.

То что получилось можно найти тут:
https://github.com/alex0x08/paster/tree/master/libs/tiles-jakarta-stripped

Примерно такой же масштаб приключений был с клиентской частью, поскольку изначально Пастер был построен вокруг Mootools и его плагинов.
Когда проект Mootools был заморожен и заброшен - весь клиентский код пришлось переписывать с 0.

Для упрощения дальнейшей поддержки, вся клиентская часть была
переписана на чистый ES6, используемый без трансляции сразу в браузере.
Да, теперь так можно.

Но в итоге появилось технологическое чудо, существование
которого считают невозможным обитатели Stackoverflow:

 **Классическое вебприложение из 201х : Spring/Hibernate/JSP/Tiles3
нативно работающее на Jakarta 9.1 API, на чистом Servlet 6.0.**

Еще у нас есть Spring 6 с xml конфигурацией, без Spring Boot и Spring Data - все выборки на чистом Criteria API. 

Вообщем если у вас есть старый но работающий проект, на устаревших технологиях - не выбрасывайте/переписывайте с 0.
Лучше [напишите](mailto:alex3.145@gmail.com) нам и мы поможем с модернизацией.


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
