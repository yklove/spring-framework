package cn.yklove;

import cn.yklove.bean.Person;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * @author qinggeng
 */
public class XmlBeanFactoryTest {

	@Test
	public void testXmlBeanFactory(){
		BeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource("beans.xml"));
		Person person = (Person) beanFactory.getBean("person");
		System.out.println(person);
	}

}
