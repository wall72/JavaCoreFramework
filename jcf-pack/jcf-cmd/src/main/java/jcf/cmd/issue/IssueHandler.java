package jcf.cmd.issue;


public interface IssueHandler {

	/**
	 * 이슈 상태 체크
	 * <ul>
	 * <li>이슈가 열려있는 경우 : 이슈 카운트 증가</li>
	 * <li>이슈가 없거나 닫혀있는 경우 : 알람 전송 및 이슈 상태 변경 또는 생성(열린)</li>
	 * </ul>
	 * @param issue
	 */
	void report(Issue issue);
	
}
