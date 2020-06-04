package jcf.sua;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;

@Ignore
public class MciRequestAPITest {

	MockMvc mockMvc = MockMvcBuilders.xmlConfigSetup("classpath:/servletContext.xml").build();

	@Test
	public void ExtJS요청_MciRequest_get메소드_사용() throws Exception{
		//given
		String data = "{\"DS_01\" : { \"id\" : \"ha\", \"name\" : \"하\", \"grade\" : 10, \"birthday\" : \"2012/2/14\" } }";
		String header = "application/extJs+sua";

		mockMvc
		//when
			.perform(post("/sua/sample/get").body(data .getBytes()).header("Accept", header))

		//then
			.andExpect(status().isOk());
	}

	@Test
	public void ExtJS요청_MciRequest_getParam메소드_사용() throws Exception{
		//given
		String data = "{\"DS_01\" : { \"id\" : \"ha\", \"name\" : \"하\", \"grade\" : 10, \"birthday\" : \"2012/2/14\" } }";
		String header = "application/extJs+sua";
		String id = "haha";

		mockMvc
		//when
		.perform(post("/sua/sample/getParam").param("id", id).body(data .getBytes()).header("Accept", header))

		//then
		.andExpect(status().isOk());
	}

	@Test
	public void ExtJS요청_MciRequest_getList메소드_사용() throws Exception{
		//given
		String data = "{\"DS_01\" : [{ \"id\" : \"ha\", \"name\" : \"하\", \"grade\" : 10, \"birthday\" : \"2012/2/14\", \"rowStatus\" : \"insert\" }, { \"id\" : \"hani\", \"name\" : \"하니\", \"grade\" : 99, \"birthday\" : \"2012/2/14\", \"rowStatus\" : \"update\"}] }";
		String header = "application/extJs+sua";

		mockMvc
		//when
		.perform(post("/sua/sample/getGridData").body(data .getBytes()).header("Accept", header))

		//then
		.andExpect(status().isOk());
	}

	@Test
	public void JSON요청_MciRequest_getPARAM메소드_사용() throws Exception{
		//given
		String header = "application/json+sua";

		mockMvc
		//when
		.perform(post("/sua/sample/getParamModel").header("Accept", header))

		//then
		.andExpect(status().isOk());
	}


}

