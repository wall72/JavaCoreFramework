package jcf.sua;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import jcf.sua.sample.controller.SampleController;
import jcf.sua.sample.model.SampleModel;
import jcf.sua.sample.service.SampleService;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=TestGenericWebXmlContextLoader.class, locations={"classpath:/servletContext.xml"})
public class MciResponseAPITest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private SampleController controller;

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webApplicationContextSetup(this.wac).build();
	}

	@Test
	public void ExtJS응답_set메소드_사용() throws Exception{
		//given
		String header = "application/extJs+sua";
		controller.setSampleService(new SampleService() {
			public List<SampleModel> getSamples() {return null;}

			public SampleModel getSample() {
				SampleModel sampleModel = new SampleModel();
				sampleModel.setId("haibane");
				sampleModel.setName("jeado");
				sampleModel.setGrade(20);
				sampleModel.setBirthday(new Date(0));
				return sampleModel;
			}
		});
		mockMvc
		//when
			.perform(post("/sua/sample/set").header("Accept", header))
		//then
			.andExpect(content().string(containsString("{\"DS_01\":")))
			.andExpect(content().string(containsString("{")))
			.andExpect(content().string(containsString("\"grade\"")))
			.andExpect(content().string(containsString("20")))
			.andExpect(content().string(containsString("\"name\"")))
			.andExpect(content().string(containsString("\"jeado\"")))
			.andExpect(content().string(containsString("}}")));
	}

	@Test
	public void ExtJS응답_setList메소드_사용() throws Exception{
		//given
		String header = "application/extJs+sua";
		controller.setSampleService(new SampleService() {
			public List<SampleModel> getSamples() {
				SampleModel sampleModel = new SampleModel();
					sampleModel.setId("haibane");
					sampleModel.setName("jeado");
					sampleModel.setGrade(20);
					sampleModel.setBirthday(new Date(0));
				SampleModel sampleModel2 = new SampleModel();
					sampleModel2.setId("haibane");
					sampleModel2.setName("jeado");
					sampleModel2.setGrade(20);
					sampleModel2.setBirthday(new Date(0));
				List<SampleModel> sampleModels = new ArrayList<SampleModel>();
				sampleModels.add(sampleModel);
				sampleModels.add(sampleModel2);
				return sampleModels;
			}

			public SampleModel getSample() {
				return null;
			}
		});
		mockMvc
		//when
		.perform(post("/sua/sample/setList").header("Accept", header))
		//then
		.andExpect(content().string(containsString("{\"DS_01\":")))
		.andExpect(content().string(containsString("[{")))
		.andExpect(content().string(containsString("\"grade\"")))
		.andExpect(content().string(containsString("20")))
		.andExpect(content().string(containsString("\"name\"")))
		.andExpect(content().string(containsString("\"jeado\"")))
		.andExpect(content().string(containsString("}]}")));
	}

}

class TestGenericWebXmlContextLoader extends GenericWebXmlContextLoader {
	public TestGenericWebXmlContextLoader() {
		super("classpath:/applicaitonContext.xml", false);
	}
}
