package jcf.sua.support.rad.service;

import java.util.List;
import java.util.Map;

import jcf.data.handler.StreamHandler;
import jcf.sua.support.rad.dao.RadSupportDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * RAD (Rapid Application Development) 지원을 위한 서비스 클래스
 *
 * @author nolang
 *
 */
@Service
public class RadSupportService {

	@Autowired
	private RadSupportDao dao;

	public void setDao(RadSupportDao dao) {
		this.dao = dao;
	}

	/**
	 *
	 * 클라이언트로 부터 전달된 sql을 실행하여 조회된 결과를 반환한다.
	 *
	 * @param sqlId
	 * @param param
	 * @return
	 */
	public List<?> select(String sqlId, Object param) {
		return dao.selectList(sqlId, param);
	}

	/**
	 *
	 * 클라이언트로 부터 전달된 sql을 실행하여 조회된 결과를 스트리밍 형태로 반환한다.
	 * 대용량 데이터 처리시 사용함.
	 *
	 * @param sqlId
	 * @param param
	 * @param streamHandler
	 */
	public void selectBySream(String sqlId, Object param, StreamHandler<Object> streamHandler)	{
		dao.selectListByStream(sqlId, param, streamHandler);
	}

	/**
	 *
	 * 전달된 데이터를 저장소에 추가하며, 다건 데이터의 일괄 처리를 지원한다.
	 *
	 * @param sqlId
	 * @param parameterList
	 */
	public void insert(String sqlId, List<Map<String, String>> parameterList) {
		dao.insertList(sqlId, parameterList);
	}

	/**
	 *
	 * 저장소에 존재하는 데이터를 수정하며, 다건 데이터의 일괄 처리를 지원한다.
	 *
	 * @param sqlId
	 * @param parameterList
	 */
	public void update(String sqlId, List<Map<String, String>> parameterList) {
		dao.updateList(sqlId, parameterList);
	}

	/**
	 *
	 * 저장소에 존재하는 데이터를 삭제하며, 다건 데이터의 일괄 처리를 지원한다.
	 *
	 * @param sqlId
	 * @param parameterList
	 */
	public void delete(String sqlId, List<Map<String, String>> parameterList) {
		dao.deleteList(sqlId, parameterList);
	}

}
