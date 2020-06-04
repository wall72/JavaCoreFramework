package jcf.sua.mvc.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jcf.sua.dataset.DataSetWriter;
import jcf.sua.mvc.MciRequestContextHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.AbstractView;

/**
*
* 요청 처리중 생성된 데이터를 클라이언트로 전송한다.
*
* @author nolang
*
*/
public class MciView extends AbstractView {

	private static final Logger logger = LoggerFactory.getLogger(MciView.class);

	/*
	 * (non-Javadoc)
	 * @see org.springframework.web.servlet.view.AbstractView#renderMergedOutputModel(java.util.Map, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void renderMergedOutputModel(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		if(logger.isDebugEnabled()){
			logger.trace("[JCF-SUA] {} 응답 데이터 렌더링 작업을 수행합니다.", new Object[]{MciRequestContextHolder.get().getMciChannelType()});
		}

		if(!response.isCommitted())	{
			DataSetWriter writer = MciRequestContextHolder.get().getDataSetWriter();

			if (writer != null) {
				writer.write();
			}
		}
	}

}
