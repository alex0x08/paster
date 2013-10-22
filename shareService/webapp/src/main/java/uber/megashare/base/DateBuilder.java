/**
 * Copyright (C) 2011 aachernyshev <alex@0x08.tk>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uber.megashare.base;

import java.util.Calendar;
import java.util.Date;



/**
 * Билдер для операций с датами
 *
 * @author alex
 * @since 2.0
 */
public class DateBuilder {

    /**
     * объект календаря через который происходит постройка даты
     */
    private Calendar c;
   

    /**
     * создать билдер по-умолчанию
     * дата будет текущей на момент создания
     */
    protected DateBuilder() {
        c = Calendar.getInstance();
    }

    /*
     * создать инстанс билдера для указанного календаря
     */
    protected DateBuilder(Calendar calendar) {
        c = calendar;
    }

    /**
     * Вернуть настроенную дату
     * @return дата
     */
    public Date getDate() {
        return c.getTime();
    }

    /**
     * Установить дату для последующей модификации
     * @param date объект даты
     * @return билдер
     */
    public DateBuilder setDate(Date date) {
        c.setTime(date);
        return this;
    }

    /**
     * Вернуть текущий календарь
     * @return
     */
    public Calendar getCalendar() {
        return c;
    }

    /**
     * Установить входящую дату в 00 часов 00 минут и вернуть с изменениями
     * @param d входящая дата
     * @return измененная дата
     */
    public Date getTimeFormBegin(Date d) {

        c.setTime(d);
        setTimeFromBegin();
        return c.getTime();
    }

    /**
     * Установить входящую дату в 23.59 и вернуть ее
     * @param d входящая дата
     * @return измененная дата
     */
    public Date getTimeToEnd(Date d) {

        c.setTime(d);
        setTimeToEnd();
        return c.getTime();
    }

    /**
     * Установить входящую дату в 12.00 и вернуть ее
     * @param d входящая дата
     * @return измененная дата
     */
    public Date getTimeToNoon(Date d) {

        c.setTime(d);
        setTimeToNoon();
        return c.getTime();
    }

    /**
     * установить время в 00:00:00
     *
     */
    public DateBuilder setTimeFromBegin() {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return this;
    }

    /**
     * установить время в 23:59:59
     *
     */
    public DateBuilder setTimeToEnd() {
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);

        return this;
    }

    /**
     * установить время в 12:00:00
     *
     */
    public DateBuilder setTimeToNoon() {
        c.set(Calendar.HOUR_OF_DAY, 12);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return this;
    }

    /**
     * установить поле календаря (день,месяц,год и тд)
     * @param type тип (константа в Calendar)
     * @param value значение
     * @return this
     */
    public DateBuilder set(int type,int value) {

        c.set(type, value);

        return this;
    }

    /**
     * Добавить к дате значение поля
     * @param type поле (константа в Calendar)
     * @param value значение
     * @return
     */
    public DateBuilder add(int type,int value) {

        c.add(type, value);

        return this;
    }
   
    /**
     * Получить новый инстанс билдера
     * @return билдер
     */
    public static DateBuilder getInstance() {
        return new DateBuilder();
    }
}
