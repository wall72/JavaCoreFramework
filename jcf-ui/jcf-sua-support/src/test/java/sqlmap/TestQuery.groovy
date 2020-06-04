package sqlmap

import org.junit.Ignore;

@Ignore
public class TestQuery {

	public static final String select = '''
			select user_id,
			       user_name,
			       password,
			       enabled,
			       create_date,
			       create_user_id,
			       last_modify_date,
			       last_modify_user_id
			  from jcfiam_users

		 #if (${userId} == $null || ${userId} == "")
			where user_id = :userId
		 #end
	'''

}
