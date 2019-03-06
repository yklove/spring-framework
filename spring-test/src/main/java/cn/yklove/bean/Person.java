package cn.yklove.bean;

/**
 * @author qinggeng on 18-12-31.
 */

public class Person extends PersonSuper{

	private String name;

	private String age;

	private String test;

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

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public Person() {
		System.out.println("bean初始化");
	}

	public Person(String name, String age) {
		this.name = name;
		this.age = age;
	}

	public Person(String name) {
		this.name = name;
	}

	@Override
	public void testMethod() {
		super.testMethod();
	}
}
