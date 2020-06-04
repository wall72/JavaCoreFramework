package jcf.iam.core.authentication.oauth2.provider;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import jcf.iam.core.authentication.oauth2.provider.model.ExternalClientDetails;
import jcf.iam.core.jdbc.oauth2.ClientDetailsMapping;
import jcf.query.core.QueryExecutorWrapper;
import jcf.query.core.evaluator.SimpleORMQueryType;
import jcf.query.core.mapper.ObjectRelationMapper;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.oauth2.provider.ClientDetails;

/**
 *
 * @author nolang
 *
 * @param <Map>
 */
public class ExternalClientMappingFactoryBean<Map> implements FactoryBean<Map> {

	@Autowired
	private QueryExecutorWrapper queryExecutor;

	private ObjectRelationMapper resultMapper = new ObjectRelationMapper();

	private LinkedHashMap<String, ClientDetails> clientMapping = new LinkedHashMap<String, ClientDetails>();

	public void init() {
		synchronized (clientMapping) {
			loadExternalClientMapping();
		}
	}

	private void loadExternalClientMapping() {
		queryExecutor.queryForList(SimpleORMQueryType.SELECT, BeanUtils.instantiate(ExternalClientDetails.class), new RowMapper<ClientDetailsMapping>() {

			public ClientDetailsMapping mapRow(ResultSet rs, int rowNum) throws SQLException {
				ClientDetailsMapping client = resultMapper.mapper(rs, ExternalClientDetails.class);

				clientMapping.put(client.getClientId(), client);

				return null;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public Map getObject() throws Exception {
		synchronized (clientMapping) {
			return (Map) clientMapping;
		}
	}

	public Class<?> getObjectType() {
		return LinkedHashMap.class;
	}

	public boolean isSingleton() {
		return true;
	}
}
