package jcf.iam.admin.view.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.List;

import jcf.iam.admin.view.model.ViewResourcesWrapper;
import jcf.iam.core.authorization.service.model.ViewResources;
import jcf.iam.core.jdbc.authorization.ViewResourcesMapping;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/testApplicationContext-sua.xml" })
public class ViewResourcesServiceTest {

	@Autowired(required=false)
	private ViewResourcesService service;

	private ObjectMapper mapper = new ObjectMapper();

	@Test
	@Ignore
	public void 메뉴조회() throws IOException {
		List<ViewResourcesWrapper> list = service.getMenuTree(" ");

		int index = 0;

		JsonGenerator jsonGenerator = mapper.getJsonFactory().createJsonGenerator(System.out, JsonEncoding.UTF8);

		for (ViewResourcesWrapper view : list) {
			System.out.println(String.format("Seq={%d} Level={%d} Id={%s} pId={%s} Name={%s}", ++index,
					view.getMenuLevel(), view.getViewResource()
							.getViewResourceId(), view.getViewResource().getParentViewId(), view.getViewResource()
							.getViewResourceName()));
		}

		mapper.writeValue(jsonGenerator, list);
	}

	@Test
	@Rollback(value = true)
	public void 메뉴추가수정삭제조회() {
		ViewResources workView = new ViewResources();

		workView.setViewResourceId("TEST_VIEW");
		workView.setViewResourceName("테스트 화면");
		workView.setViewResourcePattern("/**/*");
		workView.setViewResourceSeq("000");
		workView.setViewResourceUrl("/testview.do");
		workView.setDescription("테스트 화면");
		workView.setParentViewId("");
		workView.setCreateUserId("nolang");
		workView.setLastModifyUserId("nolang");

		/*
		 * 일단 하나 추가
		 */
		service.insertViewResources(workView);

		/*
		 * 같은놈을 조회해서 비교해본다.
		 */
		ViewResourcesMapping searchView = service.getMenuDetails("TEST_VIEW");

		assertEquals(workView.getViewResourceId(), searchView.getViewResourceId());
		assertEquals(workView.getViewResourceName(), searchView.getViewResourceName());

		/*
		 * 그놈을 수정하고
		 */
		workView.setViewResourceName("수정된 테스트 화면");

		service.updateViewResources(workView);

		/*
		 * 다시 조회해서 비교 한후..
		 */
		searchView = service.getMenuDetails("TEST_VIEW");

		assertEquals(workView.getViewResourceId(), searchView.getViewResourceId());
		assertEquals(workView.getViewResourceName(), searchView.getViewResourceName());

		/*
		 * 지운다.
		 */
		service.deleteViewResources(workView);

		/*
		 * 지운후에는 암것도 없어야함.
		 */
		searchView = service.getMenuDetails("TEST_VIEW");

		assertNull(searchView);
	}
}
