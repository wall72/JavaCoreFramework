package jcf.cmd.issue;

import java.util.ArrayList;
import java.util.List;

public class ChainedIssueHandler implements IssueHandler {

		List<IssueHandler> issueHandlers = new ArrayList<IssueHandler>();
		
		public void report(Issue issue) {
			for (IssueHandler issueHandler : issueHandlers) {
				issueHandler.report(issue);
			}
		}
	
		public void add(IssueHandler issueHandler) {
			issueHandlers.add(issueHandler);
		}

}
