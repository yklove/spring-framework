package cn.yklove.bean;

/**
 * @author qinggeng
 */
public class C {

	private A a;

	public A getA() {
		return a;
	}

	public void setA(A a) {
		this.a = a;
	}

	public void test(int i){
		System.out.println(i);
		a.test(++i);
	}
}
