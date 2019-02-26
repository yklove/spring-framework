package cn.yklove;

import cn.yklove.bean.Person;
import org.junit.Test;
import org.springframework.util.ClassUtils;

/**
 * @author qinggeng
 */
public class ClassUtilsTests {

	@Test
	public void testGetMethodCountForName(){
		int count = ClassUtils.getMethodCountForName(Person.class,"testMethod");
		System.out.println(count);
	}

}
