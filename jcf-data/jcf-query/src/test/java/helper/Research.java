package helper;

import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.InternalContextAdapterImpl;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.junit.Ignore;
import org.junit.Test;

import freemarker.core.Environment;
import freemarker.core.TemplateElement;
import freemarker.template.Template;

public class Research {

	@Test
//	@Ignore
	public void Velocity() throws Exception {
		Velocity.init();

		VelocityContext context = new VelocityContext();

		context.put("date", new Date());
		context.put("vo", new ValueObject("test value"));

		StringWriter writer = new StringWriter();

		String instring = "$date;\nTime      $date.getHours():$date.getMinutes():$date.getSeconds()\nDate      $date.getDate()/$date.getMonth()/$date.getYear()\n $vo.getValue()\n$vo.value";

		boolean result = Velocity.evaluate(context, writer, null, instring);

		System.out.println(writer.getBuffer().toString());
	}

	public static class ValueObject {
		private String value;

		public ValueObject(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	@Test
	@Ignore
	public void SimpleNodeParser() throws Exception {
		Velocity.init();
		VelocityContext context = new VelocityContext();

		context.put("date", new Date());
		context.put("vo", new ValueObject("test value"));

		String instring = "$date;\nTime      $date.getHours():$date.getMinutes():$date.getSeconds()\nDate      $date.getDate()/$date.getMonth()/$date.getYear()\n $vo.getValue()\n$vo.value";
		BufferedReader reader = new BufferedReader(new StringReader(instring));

		SimpleNode node = RuntimeSingleton.parse(reader, null);

		InternalContextAdapterImpl ica = new InternalContextAdapterImpl(context);

		node.init(ica, RuntimeSingleton.getRuntimeServices());

		int tokenCount = node.jjtGetNumChildren();

		for (int i = 0; i < tokenCount; ++i) {
			StringWriter writer = new StringWriter();

			int nodeType = node.jjtGetChild(i).getType();

			if (nodeType != 14) {
				continue;
			}

			String str = node.jjtGetChild(i).literal();
			System.out.println(str + " - " + node.jjtGetChild(i).value(ica));

			node.jjtGetChild(i).render(ica, writer);

			// System.out.println(writer.toString());
		}
	}

//	@Test
	public void f() throws Exception {
		StringReader reader = new StringReader(
				"Test Tempalate.. <#if user == 'freemarker'> ${user1!\"Null Value\"} - This is a FreemMarker test<#if user=='freemarker'>zzzz</#if></#if>${user}");
		Map<String, String> map = new HashMap<String, String>();
		map.put("user", "freemarker111");

		OutputStreamWriter out = new OutputStreamWriter(System.out);

		Template template = new Template("test", reader);

		// template.dump(System.out);
		// template.process(map, out);
		//
		// out.flush();

		Environment env = template.createProcessingEnvironment(map, out);

		// System.out.println(env.getVariable("user"));

		TemplateElement element = template.getRootTreeNode();

		traverse(element.children(), env, map, out);

		out.flush();
//
//		TemplateSequenceModel model = element.getChildNodes();
//
//		for (int i = 0; i < model.size(); ++i) {
//			TemplateModel tm = model.get(i);
//
//
//		}

	}

	void traverse(Enumeration e, Environment env, Map<String, String> map,
			OutputStreamWriter out) throws Exception {
		while (e.hasMoreElements()) {
			TemplateElement element = (TemplateElement) e.nextElement();

			traverse(element.children(), env, map, out);


			Method method = env.getClass().getDeclaredMethod(
					"renderElementToString", TemplateElement.class);
			method.setAccessible(true);
			System.out.println(method.invoke(env, element));

		}
	}


//	@Test
	public void  fm() throws Exception	{
		StringBuilder sqlTemplate = new StringBuilder();

		sqlTemplate.append("select ");
		sqlTemplate.append(" * ");
		sqlTemplate.append(" from table ");
		sqlTemplate.append("where col1 = ${col1}");
		sqlTemplate.append("  and col2 = ${col2}");
		sqlTemplate.append(" <#if col3 == 'test column'> and col3 = #{col4} </#if> ");

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("col1", "column1");
		map.put("col2", "column2");
		map.put("col3", "test column");
		map.put("col4", 1);

		Template template = new Template("sql", new StringReader(sqlTemplate.toString()), null);

		OutputStreamWriter out = new OutputStreamWriter(System.out);

		template.process(map, out);

		out.flush();

	}

}
