package jcf.query.core.evaluator;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import org.junit.Test;

public class CallMetaDataBuilderTest {

	@Test
	public void test_isCallStatement()	{
		String procedure = "{ CALL 	edu.SP_ADM0001_SRNUM_TEST1(#tableNm#, #outSrnum#) }";
		String function = "{? = CALL 	edu.SP_ADM0001_SRNUM_TEST1(#tableNm#, #outSrnum#) }";

		assertThat(CallMetaDataBuilder.isCallStatement(procedure), is(true));
		assertThat(CallMetaDataBuilder.isCallStatement(function), is(true));
	}

	@Test
	public void test_buildCallMataData_procedure()	{
		String procedure = "{ CALL 	edu.SP_ADM0001_SRNUM_TEST1(#tableNm#, #outSrnum#) }";

		CallMetaData callMetaData = CallMetaDataBuilder.buildCallMataData(procedure);

		assertNotNull(callMetaData);
		assertThat(callMetaData.getSchemaName(), is("edu"));
		assertThat(callMetaData.getProcedureName(), is("SP_ADM0001_SRNUM_TEST1"));
		assertThat(callMetaData.isFunction(), is(false));
	}

	@Test
	public void test_buildCallMataData_function()	{
		String function = "{? = CALL 	edu.SP_ADM0001_SRNUM_TEST1(#tableNm#, #outSrnum#) }";

		CallMetaData callMetaData = CallMetaDataBuilder.buildCallMataData(function);

		assertNotNull(callMetaData);
		assertThat(callMetaData.getSchemaName(), is("edu"));
		assertThat(callMetaData.getProcedureName(), is("SP_ADM0001_SRNUM_TEST1"));
		assertThat(callMetaData.isFunction(), is(true));
	}
}
