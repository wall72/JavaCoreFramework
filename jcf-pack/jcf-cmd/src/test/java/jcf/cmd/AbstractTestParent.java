package jcf.cmd;

import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.rules.TestWatchman;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractTestParent {
	protected final Logger logger = LoggerFactory
			.getLogger(getClass());

 	@Rule
 	public TestRule watchman= new TestWatcher() {
 		
 		@Override
 		protected void failed(Throwable e, Description description) {
 			System.out.print("#TEST- ");
 			System.out.print(description.getMethodName());
 			System.out.print(" ");
 			System.out.println(e.getClass().getSimpleName());
 		}
 		
 		protected void succeeded(Description description) {
 			System.out.print("#TEST- ");
 			System.out.print(description.getMethodName());
 			System.out.print(" ");
 			System.out.println("성공!");
 		}
 	};

}
