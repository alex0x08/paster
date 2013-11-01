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
package uber.megashare.model.xml;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;
import uber.megashare.base.LoggedClass;

/**
 *
 * @author aachernyshev
 */
public class AbstractXmlObjectType<T extends XmlCloneable> extends LoggedClass implements UserType {

    protected Class<T> typeClass;
    protected JAXBContext context;
    protected Marshaller marshaller;
    protected Unmarshaller unmarshaller;

    private final Object lockM = new Object(),lockU = new Object(); 
    
    protected AbstractXmlObjectType(Class<T> type) {
        this.typeClass = type;
        try {
            context = JAXBContext.newInstance(typeClass);
            marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                    Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
          
            unmarshaller = context.createUnmarshaller();
           // unmarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @see org.hibernate.usertype.UserType#assemble(java.io.Serializable,
     * Object)
     */
    @Override
    public Object assemble(Serializable cached, @SuppressWarnings("unused") Object owner) {
        return cached;
    }

    /**
     * @see org.hibernate.usertype.UserType#deepCopy(Object)
     */
    @Override
    public Object deepCopy(Object value) {
        if (value == null) {
            return null;
        }

        if (!(value.getClass().isAssignableFrom(typeClass))) {
            throw new UnsupportedOperationException("can't convert " + value.getClass());
        }
        return ((T) value).clone();
    }

    /**
     * @see org.hibernate.usertype.UserType#disassemble(Object)
     */
    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        
       // System.out.println("_dissasemble "+value);
        if (value==null) {
            return null;
        }
        if (!(value.getClass().isAssignableFrom(typeClass))) {
            throw new UnsupportedOperationException("can't convert " + value.getClass());
        }
        try {
            return marshall((T) value);
        } catch (JAXBException ex) {
            throw new UnsupportedOperationException(ex);
        }
    }

    /**
     * @see org.hibernate.usertype.UserType#equals(Object, Object)
     */
    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return x.equals(y);
    }

    /**
     * @see org.hibernate.usertype.UserType#hashCode(Object)
     */
    @Override
    public int hashCode(Object value) throws HibernateException {
        return value.hashCode();
    }

    /**
     * @see org.hibernate.usertype.UserType#isMutable()
     */
    @Override
    public boolean isMutable() {
        return true;
    }

    /**
     * @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet,
     * String[], Object)
     */
    @Override
    public Object nullSafeGet(ResultSet rs, String[] names,SessionImplementor si,  @SuppressWarnings("unused") Object owner)
            throws HibernateException, SQLException {
        
        //System.out.println("_nullSafeGet ");
        
        if (rs.wasNull()) {
        return null;
    }
        
        // assume that we only map to one column, so there's only one column name
        java.sql.Clob value = rs.getClob(names[0]);
        if (value == null) {
            return null;
        }
      
        try {
            return unmarshall(value.getCharacterStream());
        } catch (JAXBException|IOException ex) {
            throw new HibernateException(ex);
        }
    }
    

    /**
     * @see
     * org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement,
     * Object, int)
     */
    @Override
    public void nullSafeSet(PreparedStatement stmt, Object value, int index,SessionImplementor si)
            throws HibernateException, SQLException {
        
        
       // System.out.println("_nullSafeSet ");
        
            if (value == null) {
                stmt.setNull(index, Types.CLOB);
                return;
            }

            if (!(value.getClass().isAssignableFrom(typeClass))) {
                throw new UnsupportedOperationException("can't convert " + value.getClass());
            }

            String xml;
            try {
                xml = marshall((T) value);
               // System.out.println("xml="+xml);
            } catch (JAXBException ex) {
                throw new UnsupportedOperationException(ex);
            }

            stmt.setClob(index, new StringReader(xml));
    }

    /**
     * @see org.hibernate.usertype.UserType#replace(Object, Object, Object)
     */
    @Override
    public Object replace(Object original,
            @SuppressWarnings("unused") Object target, @SuppressWarnings("unused") Object owner) {
        return original;
    }

    /**
     * @see org.hibernate.usertype.UserType#returnedClass()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Class returnedClass() {
        return typeClass;
    }

    /**
     * @see org.hibernate.usertype.UserType#sqlTypes()
     */
    @Override
    public int[] sqlTypes() {
        return new int[]{Types.CLOB};
    }

    /**
     * Сериализация в xml
     *
     * @return xml-строка
     * @throws JAXBException
     */
    public String marshall(T obj) throws JAXBException {
        StringWriter wr = new StringWriter();
        try {
            synchronized(lockM) {
                marshaller.marshal(obj, wr);
            }
            return wr.getBuffer().toString();

        } finally {
            try {
                wr.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * Десериализация из потока
     *
     * @param in поток с данными
     * @return десериализованный объект
     * @throws JAXBException
     */
    public T unmarshall(Reader in) throws JAXBException, IOException {
        synchronized(lockU) {            
            return (T) unmarshaller.unmarshal(in);       
        }
    }
   
}
