package jcf.extproc.execution;

public class SampleMain {
	public static void main(String[] args) throws InterruptedException {
		for (int i = 0; i < 20; i ++ ) {
			Thread.sleep(100);
			System.out.println(i);
		}
	}
}
