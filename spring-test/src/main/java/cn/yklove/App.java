package cn.yklove;

import cn.yklove.bean.Person;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * @author qinggeng on 19-1-2.
 */
public class App {

	public static void main(String[] args){
		BeanFactory beanFactory = new XmlBeanFactory(new ClassPathResource("beans.xml"));
		Person person = (Person) beanFactory.getBean("person");
		System.out.println(person.getName());
	}

}
