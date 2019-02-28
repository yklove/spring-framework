package cn.yklove.bean;

/**
 * @author qinggeng
 */
public class A {

	private B b;

	public B getB() {
		return b;
	}

	public void setB(B b) {
		this.b = b;
	}

	public void test(int i) {
		System.out.println(i);
		b.test(++i);
	}
}
