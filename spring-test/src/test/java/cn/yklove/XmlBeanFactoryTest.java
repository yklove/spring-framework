package cn.yklove;

import cn.yklove.bean.A;
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
		Person person = (Person) beanFactory.getBean("personClient");
		System.out.println(person);
		Person person2 = (Person) beanFactory.getBean("personClient2");
		System.out.println(person);

//		A a = (A) beanFactory.getBean("a");
//		System.out.println(a);
//		long start = System.currentTimeMillis();
//
//		a.test(1);
//
//		System.out.println(System.currentTimeMillis() - start);
	}

}
