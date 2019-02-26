package cn.yklove.bean;

/**
 * @author qinggeng on 18-12-31.
 */

public class Person extends PersonSuper{

	private String name;

	private String age;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public Person() {
		System.out.println("bean初始化");
	}

	@Override
	public void testMethod() {
		super.testMethod();
	}
}
