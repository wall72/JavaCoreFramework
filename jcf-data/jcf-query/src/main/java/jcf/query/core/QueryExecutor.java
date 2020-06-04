package jcf.query.core;

import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import jcf.data.handler.StreamHandler;
import jcf.query.TemplateEngineType;
import jcf.query.core.evaluator.CallMetaData;
import jcf.query.core.evaluator.QueryMetaData;
import jcf.query.core.evaluator.SqlParameterSourceBuilder;
import jcf.query.core.evaluator.SubQuery;
import jcf.query.core.evaluator.adapter.ParameterMappingAdapter;
import jcf.query.core.handler.ParameterExceptionHandler;
import jcf.query.core.handler.event.QueryEventPublisher;
import jcf.query.core.mapper.CamelCaseMapRowMapper;
import jcf.query.core.mapper.IBatisResultMapper;
import jcf.query.core.mapper.ObjectRelationMappingRowMapper;
import jcf.query.core.mapper.ParameterizedBeanRowMapper;
import jcf.query.core.mapper.PrimitiveTypeRowMapper;
import jcf.query.exception.MappingRowPopulationException;
import jcf.query.exception.StatementEvaluateException;
import jcf.query.util.QueryUtils;
import jcf.query.web.CommonVariableHolder;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.CallableStatementCreatorFactory;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.jdbc.core.metadata.CallMetaDataContext;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 *
 * @author nolang
 *
 */
public class QueryExecutor implements InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(QueryExecutor.class);

	private DataSource dataSource;
	private NamedParameterJdbcTemplate jdbcTemplate;
	private ParameterExceptionHandler exceptionHandler;
	private StreamHandler<Map<String, Object>> streamHandler;
	private QueryTemplate queryTemplate;
	private boolean userPropertyName = true;
	private int fetchSize = 0;

	private Map<String, CallMetaDataContext> procedureCache = new HashMap<String, CallMetaDataContext>();

	@Autowired(required = false)
	private QueryEventPublisher eventPublisher;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource) {

			/*
			 * (non-Javadoc)
			 * @see org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate#getPreparedStatementCreator(java.lang.String, org.springframework.jdbc.core.namedparam.SqlParameterSource)
			 */
			@Override
			protected PreparedStatementCreator getPreparedStatementCreator(String sql, SqlParameterSource paramSource) {
				ParsedSql parsedSql = getParsedSql(sql);
				String sqlToUse = NamedParameterUtils.substituteNamedParameters(parsedSql, paramSource);
				Object[] params = NamedParameterUtils.buildValueArray(parsedSql, paramSource, null);
				List<SqlParameter> declaredParameters = NamedParameterUtils.buildSqlParameterList(parsedSql, paramSource);

				PreparedStatementCreatorFactory pscf = new PreparedStatementCreatorFactory(sqlToUse, declaredParameters);

				pscf.setResultSetType(ResultSet.TYPE_SCROLL_INSENSITIVE);

				return pscf.newPreparedStatementCreator(params);
			}

		};
	}

	public void setQueryTemplate(QueryTemplate queryTemplate) {
		this.queryTemplate = queryTemplate;
	}

	public void setExceptionHandler(ParameterExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	public void setStreamHandler(StreamHandler<Map<String, Object>> streamHandler) {
		this.streamHandler = streamHandler;
	}

	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	/**
	 *
	 * @param statementTemplate
	 * @param parameter
	 */
	public void queryForStream(Object statementTemplate, Object parameter) {
		queryForStream(statementTemplate, parameter, streamHandler);
	}

	/**
	 *
	 * @param statementTemplate
	 * @param parameter
	 * @param streamHandler
	 */
	public void queryForStream(Object statementTemplate, Object parameter, @SuppressWarnings("rawtypes") final StreamHandler streamHandler) {
		if (streamHandler == null) {
			logger.debug("StreamHandler가 등록되어 있지 않습니다.");
			return;
		}

		long startTime = System.currentTimeMillis();
		Exception exception = null;

		final QueryMetaData metadata = queryTemplate.getQuery(statementTemplate, parameter);

		Class<?> resultClass = metadata.getResultClass();

		if(resultClass == null){
			resultClass = GenericTypeResolver.resolveTypeArgument(streamHandler.getClass(), streamHandler.getClass().getSuperclass());
		}

		final RowMapper<?> rowMapper = getRowMapper(metadata, resultClass);

		streamHandler.open();

		try {
			jdbcTemplate.query(metadata.getStatement(), metadata.getSqlParameterSource(), new RowCallbackHandler() {

				@SuppressWarnings("unchecked")
				public void processRow(ResultSet rs) throws SQLException {
					streamHandler.handleRow(rowMapper.mapRow(rs, 0));
				}
			});
		} finally {
			streamHandler.close();

			if(!isWithoutEvent() && eventPublisher != null) 	{
				eventPublisher.publishEvent(metadata.getStatement(), parameter, startTime, System.currentTimeMillis(), exception);
			}

			CommonVariableHolder.clearCommand();
		}
	}

	/**
	 *
	 * @param <T>
	 * @param statementTemplate
	 * @param parameter
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> queryForList(Object statementTemplate, Object parameter, Class<T> clazz) {
		long startTime = System.currentTimeMillis();
		List<T> list = null;
		Exception exception = null;

		QueryMetaData metadata = queryTemplate.getQuery(statementTemplate, parameter);

		try {
			list = (List<T>) jdbcTemplate.query(metadata.getStatement(), metadata.getSqlParameterSource(), getRowMapper(metadata, clazz));
		} catch(DataAccessException e) {
			exception = e;
			throw e;
		} finally {
			if(!isWithoutEvent() && eventPublisher != null) 	{
				eventPublisher.publishEvent(metadata.getStatement(), parameter, startTime, System.currentTimeMillis(), exception);
			}

			CommonVariableHolder.clearCommand();
		}

		return list;
	}

	/**
	 *
	 * @param statementTemplate
	 * @param parameter
	 * @return
	 */
	@Deprecated
	public List<Object> queryForList(Object statementTemplate, Object parameter) {
		long startTime = System.currentTimeMillis();
		List<Object> list = null;
		Exception exception = null;

		QueryMetaData metadata = queryTemplate.getQuery(statementTemplate, parameter);

		try {
			list = (List<Object>) jdbcTemplate.query(metadata.getStatement(), metadata.getSqlParameterSource(), getRowMapper(metadata, metadata.getResultClass()));
		} catch(DataAccessException e) {
			exception = e;
			throw e;
		} finally {
			if(!isWithoutEvent() && eventPublisher != null) 	{
				eventPublisher.publishEvent(metadata.getStatement(), parameter, startTime, System.currentTimeMillis(), exception);
			}

			CommonVariableHolder.clearCommand();
		}

		return list;
	}

	/**
	 *
	 * @param <T>
	 * @param statementTemplate
	 * @param parameter
	 * @param skipRows
	 * @param maxRows
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> queryForList(Object statementTemplate, Object parameter, final int skipRows, final int maxRows, Class<T> clazz) {
		long startTime = System.currentTimeMillis();
		Exception exception = null;

		QueryMetaData metadata = queryTemplate.getQuery(statementTemplate, parameter);

		List<T> list = null;

		try {
			final RowMapper<T> rowMapper = (RowMapper<T>) getRowMapper(metadata, clazz);

			list = jdbcTemplate.query(metadata.getStatement(), metadata.getSqlParameterSource(), new ResultSetExtractor<List<T>>() {
				public List<T> extractData(ResultSet rs) throws SQLException, DataAccessException {
					rs.absolute(skipRows);

					int rowCount = 0;

					List<T> innerList = new ArrayList<T>();

					while(rowCount < maxRows)	{
						innerList.add(rowMapper.mapRow(rs, rowCount++ + skipRows));
					}

					return innerList;
				}
			});
		} catch(DataAccessException e) {
			exception = e;
			throw e;
		} finally {
			if(!isWithoutEvent() && eventPublisher != null) 	{
				eventPublisher.publishEvent(metadata.getStatement(), parameter, startTime, System.currentTimeMillis(), exception);
			}

			CommonVariableHolder.clearCommand();
		}

		return list;
	}
	/**
	 *
	 * @param <T>
	 * @param statementTemplate
	 * @param parameter
	 * @param skipRows
	 * @param maxRows
	 * @param clazz
	 * @return
	 */
	@Deprecated
	public List<Object> queryForList(Object statementTemplate, Object parameter, final int skipRows, final int maxRows) {

		long startTime = System.currentTimeMillis();
		Exception exception = null;
		List<Object> list = null;

		QueryMetaData metadata = queryTemplate.getQuery(statementTemplate, parameter);

		try {
			final RowMapper<?> rowMapper = getRowMapper(metadata, metadata.getResultClass());

			list = jdbcTemplate.query(metadata.getStatement(), metadata.getSqlParameterSource(), new ResultSetExtractor<List<Object>>() {
				public List<Object> extractData(ResultSet rs) throws SQLException, DataAccessException {
					rs.absolute(skipRows);

					int rowCount = 0;

					List<Object> innerList = new ArrayList<Object>();

					while(rowCount < maxRows)	{
						innerList.add(rowMapper.mapRow(rs, rowCount++ + skipRows));
					}

					return innerList;
				}
			});
		} catch(DataAccessException e) {
			exception = e;
			throw e;
		} finally {
			if(!isWithoutEvent() && eventPublisher != null) 	{
				eventPublisher.publishEvent(metadata.getStatement(), parameter, startTime, System.currentTimeMillis(), exception);
			}

			CommonVariableHolder.clearCommand();
		}

		return list;
	}

	/**
	 *
	 * @param statementTemplate
	 * @param parameter
	 * @return
	 */
	public List<Map<String, Object>> queryForMapList(Object statementTemplate, Object parameter) {
		return queryForMapList(statementTemplate, parameter, userPropertyName);
	}

	/**
	 *
	 * @param statementTemplate
	 * @param parameter
	 * @param usePropertyName
	 * @return
	 */
	@Deprecated
	public List<Map<String, Object>> queryForMapList(Object statementTemplate, Object parameter, boolean usePropertyName) {

		long startTime = System.currentTimeMillis();
		List<Map<String, Object>> list = null;
		Exception exception = null;

		QueryMetaData metadata = queryTemplate.getQuery(statementTemplate, parameter);

		try {
			list = jdbcTemplate.query(metadata.getStatement(), metadata.getSqlParameterSource(), getRowMapper(usePropertyName));
		} catch(DataAccessException e) {
			exception = e;
			throw e;
		} finally {
			if(!isWithoutEvent() && eventPublisher != null) 	{
				eventPublisher.publishEvent(metadata.getStatement(), parameter, startTime, System.currentTimeMillis(), exception);
			}

			CommonVariableHolder.clearCommand();
		}

		return list;
	}

	/**
	 *
	 * @param <T>
	 * @param statementTemplate
	 * @param parameter
	 * @param rowMapper
	 * @return
	 */
	public <T> List<T> queryForList(Object statementTemplate, Object parameter, RowMapper<T> rowMapper) {
		long startTime = System.currentTimeMillis();
		Exception exception = null;

		List<T> list = null;

		QueryMetaData metadata = queryTemplate.getQuery(statementTemplate, parameter);

		try {
			list = jdbcTemplate.query(metadata.getStatement(), metadata.getSqlParameterSource(), rowMapper);
		} catch(DataAccessException e) {
			exception = e;
			throw e;
		} finally {
			if(!isWithoutEvent() && eventPublisher != null) 	{
				eventPublisher.publishEvent(metadata.getStatement(), parameter, startTime, System.currentTimeMillis(), exception);
			}

			CommonVariableHolder.clearCommand();
		}

		return list;
	}


	/**
	 *
	 * @param statementTemplate
	 * @param parameter
	 * @param skipRows
	 * @param maxRows
	 * @param usePropertyName
	 * @return
	 */
	@Deprecated
	public List<Map<String, Object>> queryForMapList(Object statementTemplate, Object parameter, final int skipRows, final int maxRows, boolean usePropertyName) {
		long startTime = System.currentTimeMillis();
		Exception exception = null;

		List<Map<String, Object>> list = null;

		QueryMetaData metadata = queryTemplate.getQuery(statementTemplate, parameter);

		try {
			final RowMapper<Map<String, Object>> rowMapper = getRowMapper(usePropertyName);

			list = jdbcTemplate.query(metadata.getStatement(), metadata.getSqlParameterSource(), new ResultSetExtractor<List<Map<String, Object>>>() {
				public List<Map<String, Object>> extractData(ResultSet rs) throws SQLException, DataAccessException {
					if (skipRows > 0) rs.absolute(skipRows);

					int rowCount = 0;

					List<Map<String, Object>> innerList = new ArrayList<Map<String, Object>>();

					while(rowCount - skipRows < maxRows)	{
						innerList.add(rowMapper.mapRow(rs, rowCount++ + skipRows));
					}

					return innerList;
				}
			});
		} catch(DataAccessException e) {
			exception = e;
			throw e;
		} finally {
			if(!isWithoutEvent() && eventPublisher != null) 	{
				eventPublisher.publishEvent(metadata.getStatement(), parameter, startTime, System.currentTimeMillis(), exception);
			}

			CommonVariableHolder.clearCommand();
		}

		return list;
	}

	/**
	 *
	 * @param <T>
	 * @param statementTemplate
	 * @param parameter
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T queryForObject(Object statementTemplate, Object parameter,	Class<T> clazz) {

		long startTime = System.currentTimeMillis();
		Exception exception = null;

		T result = null;

		QueryMetaData metadata = queryTemplate.getQuery(statementTemplate, parameter);

		try	{
			result = (T) jdbcTemplate.queryForObject(metadata.getStatement(), metadata.getSqlParameterSource(), getRowMapper(metadata, clazz));
		} catch(EmptyResultDataAccessException e)	{
			logger.debug("조회된 데이터가 없습니다. - {}", ExceptionUtils.getRootCauseMessage(e));
		} catch(DataAccessException e) {
			exception = e;
			throw e;
		} finally {
			if(!isWithoutEvent() && eventPublisher != null) 	{
				eventPublisher.publishEvent(metadata.getStatement(), parameter, startTime, System.currentTimeMillis(), exception);
			}

			CommonVariableHolder.clearCommand();
		}

		return result;
	}

	/**
	 *
	 * @param <T>
	 * @param statementTemplate
	 * @param parameter
	 * @param rowMapper
	 * @return
	 */
	public <T> T queryForObject(Object statementTemplate, Object parameter,	RowMapper<T> rowMapper) {

		long startTime = System.currentTimeMillis();
		Exception exception = null;

		T result = null;

		QueryMetaData metadata = queryTemplate.getQuery(statementTemplate, parameter);

		try	{
			result = (T) jdbcTemplate.queryForObject(metadata.getStatement(), metadata.getSqlParameterSource(), rowMapper);
		} catch(EmptyResultDataAccessException e)	{
			logger.debug("조회된 데이터가 없습니다. - {}", ExceptionUtils.getRootCauseMessage(e));
		} catch(DataAccessException e) {
			exception = e;
			throw e;
		} finally {
			if(!isWithoutEvent() && eventPublisher != null) 	{
				eventPublisher.publishEvent(metadata.getStatement(), parameter, startTime, System.currentTimeMillis(), exception);
			}

			CommonVariableHolder.clearCommand();
		}

		return result;
	}

	/**
	 *
	 * iBatis의 ResultMap을 사용하여 조회함
	 *
	 * @param statementTemplate
	 * @param parameter
	 * @return
	 */
	@Deprecated
	public Object queryForObject(Object statementTemplate, Object parameter) {

		long startTime = System.currentTimeMillis();
		Exception exception = null;

		Object result = null;

		QueryMetaData metadata = queryTemplate.getQuery(statementTemplate, parameter);

		try	{
			result = jdbcTemplate.queryForObject(metadata.getStatement(), metadata.getSqlParameterSource(), getRowMapper(metadata, metadata.getResultClass()));
		} catch(EmptyResultDataAccessException e)	{
			logger.debug("조회된 데이터가 없습니다. - {}", ExceptionUtils.getRootCauseMessage(e));
		} catch(DataAccessException e) {
			exception = e;
			throw e;
		} finally {
			if(!isWithoutEvent() && eventPublisher != null) 	{
				eventPublisher.publishEvent(metadata.getStatement(), parameter, startTime, System.currentTimeMillis(), exception);
			}

			CommonVariableHolder.clearCommand();
		}

		return result;
	}

	/**
	 *
	 * @param statementTemplate
	 * @param parameter
	 * @return
	 */
	public Map<String, Object> queryForMap(Object statementTemplate, Object parameter) {
		return queryForMap(statementTemplate, parameter, userPropertyName);
	}

	/**
	 *
	 * @param statementTemplate
	 * @param parameter
	 * @param usePropertyName
	 * @return
	 */
	@Deprecated
	public Map<String, Object> queryForMap(Object statementTemplate, Object parameter, boolean usePropertyName) {

		long startTime = System.currentTimeMillis();
		Exception exception = null;

		Map<String, Object> result = null;

		QueryMetaData metadata = queryTemplate.getQuery(statementTemplate, parameter);

		try	{
			result = jdbcTemplate.queryForObject(metadata.getStatement(), metadata.getSqlParameterSource(), getRowMapper(usePropertyName));
		} catch(EmptyResultDataAccessException e)	{
			logger.debug("조회된 데이터가 없습니다. - {}", ExceptionUtils.getRootCauseMessage(e));
		} catch(DataAccessException e) {
			exception = e;
			throw e;
		} finally {
			if(!isWithoutEvent() && eventPublisher != null) 	{
				eventPublisher.publishEvent(metadata.getStatement(), parameter, startTime, System.currentTimeMillis(), exception);
			}

			CommonVariableHolder.clearCommand();
		}

		return result;
	}
	
	/**
     * 
     * @param statementTemplate
     * @param parameter
     * @param userStreamHandler
     */
    public void queryForMapWithBlobStream(Object statementTemplate, Object parameter, final StreamHandler userStreamHandler) {

        long startTime = System.currentTimeMillis();
        Exception exception = null;

        final QueryMetaData metadata = queryTemplate.getQuery(statementTemplate, parameter);

        userStreamHandler.open();

        try {
            jdbcTemplate.queryForObject(metadata.getStatement(), metadata.getSqlParameterSource(), new CamelCaseMapRowMapper() {

                @Override
                public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
                    Map<String, Object> row = super.mapRow(rs, rowNum);
                    userStreamHandler.handleRow(row);
                    return null;
                }

                @Override
                protected Object getColumnValue(ResultSet rs, int index) throws SQLException {
                    Object obj = rs.getObject(index);
                    String className = null;
                    if (obj != null) {
                        className = obj.getClass().getName();
                    }
                    if (obj instanceof Blob) {
                        obj = ((Blob) obj).getBinaryStream();
                    } else if (obj instanceof Clob) {
                        obj = rs.getString(index);
                    } else if (className != null && ("oracle.sql.TIMESTAMP".equals(className) || "oracle.sql.TIMESTAMPTZ".equals(className))) {
                        obj = rs.getTimestamp(index);
                    } else if (className != null && className.startsWith("oracle.sql.DATE")) {
                        String metaDataClassName = rs.getMetaData().getColumnClassName(index);
                        if ("java.sql.Timestamp".equals(metaDataClassName) || "oracle.sql.TIMESTAMP".equals(metaDataClassName)) {
                            obj = rs.getTimestamp(index);
                        } else {
                            obj = rs.getDate(index);
                        }
                    } else if (obj != null && obj instanceof java.sql.Date) {
                        if ("java.sql.Timestamp".equals(rs.getMetaData().getColumnClassName(index))) {
                            obj = rs.getTimestamp(index);
                        }
                    }
                    return obj;
                }
            });
        } finally {
            userStreamHandler.close();

            if (!isWithoutEvent() && eventPublisher != null) {
                eventPublisher.publishEvent(metadata.getStatement(), parameter, startTime, System.currentTimeMillis(), exception);
            }

            CommonVariableHolder.clearCommand();
        }
    }

	/**
	 *
	 * @param statementTemplate
	 * @param parameter
	 * @return
	 */
	public SqlRowSet queryForRowSet(Object statementTemplate, Object parameter) {

		long startTime = System.currentTimeMillis();
		Exception exception = null;

		QueryMetaData metadata = queryTemplate.getQuery(statementTemplate, parameter);

		try	{
			return jdbcTemplate.queryForRowSet(metadata.getStatement(), metadata.getSqlParameterSource());
		} catch(DataAccessException e) {
			exception = e;
			throw e;
		} finally {
			if(!isWithoutEvent() && eventPublisher != null) 	{
				eventPublisher.publishEvent(metadata.getStatement(), parameter, startTime, System.currentTimeMillis(), exception);
			}

			CommonVariableHolder.clearCommand();
		}
	}
	
	/**
	 * 
	 * @param statementTemplate
	 * @param parameter
	 * @param resultSetExtractor
	 * @return
	 */
	public <T> T queryToResultSet(Object statementTemplate, Object parameter, ResultSetExtractor<T> resultSetExtractor) {
        QueryMetaData metadata = queryTemplate.getQuery(statementTemplate, parameter);

        return this.jdbcTemplate.query(metadata.getStatement(), metadata.getSqlParameterSource(), resultSetExtractor);
    }

	/**
	 *
	 * @param statementTemplate
	 * @param parameter
	 * @return
	 */
	public Integer queryForInt(Object statementTemplate, Object parameter){

		long startTime = System.currentTimeMillis();
		Exception exception = null;

		QueryMetaData metadata = queryTemplate.getQuery(statementTemplate, parameter);

		try	{
			return jdbcTemplate.queryForInt(metadata.getStatement(), metadata.getSqlParameterSource());
		} catch(DataAccessException e) {
			exception = e;
			throw e;
		} finally {
			if(!isWithoutEvent() && eventPublisher != null) 	{
				eventPublisher.publishEvent(metadata.getStatement(), parameter, startTime, System.currentTimeMillis(), exception);
			}

			CommonVariableHolder.clearCommand();
		}
	}

	/**
	 *
	 * @param statementTemplate
	 * @param parameter
	 * @return
	 */
	public Long queryForLong(Object statementTemplate, Object parameter){

		long startTime = System.currentTimeMillis();
		Exception exception = null;

		QueryMetaData metadata = queryTemplate.getQuery(statementTemplate, parameter);

		try	{
			return jdbcTemplate.queryForLong(metadata.getStatement(), metadata.getSqlParameterSource());
		} catch(DataAccessException e) {
			exception = e;
			throw e;
		} finally {
			if(!isWithoutEvent() && eventPublisher != null) 	{
				eventPublisher.publishEvent(metadata.getStatement(), parameter, startTime, System.currentTimeMillis(), exception);
			}

			CommonVariableHolder.clearCommand();
		}
	}

	/**
	 *
	 * @param statementTemplate
	 * @param parameter
	 * @return
	 */
	public Integer update(Object statementTemplate, Object parameter) {

		long startTime = System.currentTimeMillis();
		Exception exception = null;

		QueryMetaData metadata = queryTemplate.getQuery(statementTemplate, parameter);

		try	{
			SubQuery subQuery = metadata.getSubQuery();

			if(subQuery != null && !subQuery.isRunAfterSQL())	{
				SqlParameterSourceBuilder.addSqlParameter(metadata.getSqlParameterSource(), subQuery.getKeyProperty(),
						jdbcTemplate.getJdbcOperations().queryForObject(subQuery.getMetaData().getStatement(), subQuery.getMetaData().getResultClass(), subQuery.getMetaData().getSqlParameterSource()));
			}

			Integer result = jdbcTemplate.update(metadata.getStatement(), metadata.getSqlParameterSource());

			if(subQuery != null && subQuery.isRunAfterSQL())	{
				jdbcTemplate.getJdbcOperations().queryForObject(subQuery.getMetaData().getStatement(), subQuery.getMetaData().getResultClass(), subQuery.getMetaData().getSqlParameterSource());
			}

			return result;

		} catch(DataAccessException e) {
			exception = e;
			throw e;
		} finally {
			if(!isWithoutEvent() && eventPublisher != null) 	{
				eventPublisher.publishEvent(metadata.getStatement(), parameter, startTime, System.currentTimeMillis(), exception);
			}

			CommonVariableHolder.clearCommand();
		}
	}
	
	/**
	 * 
	 * @param statementTemplate
	 * @param parameter
	 * @param keyHolder
	 * @param keyColumnNames
	 * @return
	 */
	public Integer update(Object statementTemplate, Object parameter, KeyHolder keyHolder, String[] keyColumnNames) {
		long startTime = System.currentTimeMillis();
        Exception exception = null;

        QueryMetaData metadata = queryTemplate.getQuery(statementTemplate, parameter);

        try {
            SubQuery subQuery = metadata.getSubQuery();

            if(subQuery != null && !subQuery.isRunAfterSQL())	{
				SqlParameterSourceBuilder.addSqlParameter(metadata.getSqlParameterSource(), subQuery.getKeyProperty(),
						jdbcTemplate.getJdbcOperations().queryForObject(subQuery.getMetaData().getStatement(), subQuery.getMetaData().getResultClass(), subQuery.getMetaData().getSqlParameterSource()));
			}

            Integer result = jdbcTemplate.getJdbcOperations().update(metadata.getStatement(), metadata.getSqlParameterSource(), keyHolder, keyColumnNames);

            if(subQuery != null && subQuery.isRunAfterSQL())	{
				jdbcTemplate.getJdbcOperations().queryForObject(subQuery.getMetaData().getStatement(), subQuery.getMetaData().getResultClass(), subQuery.getMetaData().getSqlParameterSource());
			}

            return result;

        } catch (DataAccessException e) {
            exception = e;
            throw e;
        } finally {
            if (!isWithoutEvent() && eventPublisher != null) {
                eventPublisher.publishEvent(metadata.getStatement(), parameter, startTime, System.currentTimeMillis(), exception);
            }

            CommonVariableHolder.clearCommand();
        }
	}

	/**
	 *
	 * @param statementTemplate
	 * @param parameters
	 * @return
	 */
	public int[] batchUpdate(Object statementTemplate, Object[] parameters){
		return batchUpdate(statementTemplate, CollectionUtils.arrayToList(parameters));
	}

	/**
	 *
	 * @param statementTemplate
	 * @param parameters
	 * @return
	 */
	public int[] batchUpdate(Object statementTemplate, List<?> parameters){
		long startTime = System.currentTimeMillis();
		Exception exception = null;

		SqlParameterSource[] parameterSources = new SqlParameterSource[parameters.size()];
		QueryMetaData metadata = null;

		for (int i = 0; i < parameters.size(); ++i) {
			if(i == 0)	{
				metadata = queryTemplate.getQuery(statementTemplate, parameters.get(i));
			}

			parameterSources[i] = SqlParameterSourceBuilder.getSqlParameterSource(parameters.get(i));
		}

		try	{
			return jdbcTemplate.batchUpdate(metadata.getStatement(), parameterSources);
		} catch(DataAccessException e) {
			exception = e;
			throw e;
		} finally {
			if(!isWithoutEvent() && eventPublisher != null) 	{
				eventPublisher.publishEvent(metadata.getStatement(), parameters, startTime, System.currentTimeMillis(), exception);
			}

			CommonVariableHolder.clearCommand();
		}
	}

	/**
     * 
     * @param statementTemplate
     * @param parameters
     * @param batchSize
     * @return
     */
    public int[] batchUpdate(Object statementTemplate, Object[] parameters, int batchSize) {
        return batchUpdate(statementTemplate, CollectionUtils.arrayToList(parameters), batchSize);
    }

    /**
     * 
     * @param statementTemplate
     * @param parameters
     * @param batchSize
     * @return
     */
    public int[] batchUpdate(Object statementTemplate, List<?> parameters, final int batchSize) {
        long startTime = System.currentTimeMillis();
        Exception exception = null;

        final List<SqlParameterSource> parameterSources = new ArrayList<SqlParameterSource>(parameters.size());

        QueryMetaData metadata = null;

        for (int i = 0; i < parameters.size(); ++i) {
            if (i == 0) {
                metadata = queryTemplate.getQuery(statementTemplate, parameters.get(i));
            }

            parameterSources.add(SqlParameterSourceBuilder.getSqlParameterSource(parameters.get(i)));
        }

        final ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(metadata.getStatement());

        String sqlToUse = NamedParameterUtils.substituteNamedParameters(metadata.getStatement(), parameterSources.get(0));

        int[] iret = new int[0];

        try {

            int[][] irets = jdbcTemplate.getJdbcOperations().batchUpdate(sqlToUse, parameterSources, batchSize, new ParameterizedPreparedStatementSetter<SqlParameterSource>() {

                public void setValues(PreparedStatement ps, SqlParameterSource parameterSource) throws SQLException {
                    Object[] values = NamedParameterUtils.buildValueArray(parsedSql, parameterSource, null);
                    int[] columnTypes = NamedParameterUtils.buildSqlTypeArray(parsedSql, parameterSource);
                    int colIndex = 0;
                    for (Object value : values) {
                        colIndex++;
                        if (value instanceof SqlParameterValue) {
                            SqlParameterValue paramValue = (SqlParameterValue) value;
                            StatementCreatorUtils.setParameterValue(ps, colIndex, paramValue, paramValue.getValue());
                        } else {
                            int colType;
                            if (columnTypes == null || columnTypes.length < colIndex) {
                                colType = SqlTypeValue.TYPE_UNKNOWN;
                            } else {
                                colType = columnTypes[colIndex - 1];
                            }
                            StatementCreatorUtils.setParameterValue(ps, colIndex, colType, value);
                        }
                    }
                }

            });

            for (int[] ret : irets) {
                iret = ArrayUtils.addAll(iret, ret);
            }

            return iret;

        } catch (DataAccessException e) {
            exception = e;
            throw e;
        } finally {
            if (!isWithoutEvent() && eventPublisher != null) {
                eventPublisher.publishEvent(metadata.getStatement(), parameters, startTime, System.currentTimeMillis(), exception);
            }

            CommonVariableHolder.clearCommand();
        }
    }


	/**
	 *
	 * @param <T>
	 * @param statementTemplate
	 * @param parameter
	 * @param action
	 * @return
	 * @throws DataAccessException
	 */
	@Deprecated
	public <T> T execute(Object statementTemplate, Object parameter, PreparedStatementCallback<T> action) throws DataAccessException {
		QueryMetaData metadata = queryTemplate.getQuery(statementTemplate, parameter);
		return jdbcTemplate.execute(metadata.getStatement(), metadata.getSqlParameterSource(), action);
	}

	/**
	 *
	 * @param statementTemplate
	 * @param parameter
	 * @return
	 */
	public Map<String, Object> executeCallStatement(Object statementTemplate, Object parameter) {

		final QueryMetaData metadata = queryTemplate.getQuery(statementTemplate, parameter);

		if (metadata.getTemplateType() == TemplateEngineType.IBATIS) {

			final ParameterMappingAdapter[] mapping = metadata.getParameterMappings();

			return jdbcTemplate.getJdbcOperations().execute((String) metadata.getStatementTemplate(), new CallableStatementCallback<Map<String, Object>>() {
						public Map<String, Object> doInCallableStatement(CallableStatement cs) throws SQLException,	DataAccessException {

							if(mapping != null)	{
								for (int i = 0; i < mapping.length; ++i) {
									if(mapping[i].getMode().equals(ParameterMappingAdapter.MODE_IN) || mapping[i].getMode().equals(ParameterMappingAdapter.MODE_INOUT))	{
										cs.setObject(i + 1, metadata.getSqlParameterSource().getValue(mapping[i].getPropertyName()), mapping[i].getJdbcType());
									}

									if(mapping[i].getMode().equals(ParameterMappingAdapter.MODE_OUT) || mapping[i].getMode().equals(ParameterMappingAdapter.MODE_INOUT)) {
										cs.registerOutParameter(i + 1, mapping[i].getJdbcType());
									}
								}
							}

							boolean hasResultSet = cs.execute();

							Map<String, Object> result = new HashMap<String, Object>();

							if(hasResultSet){

							} else {
								if(mapping != null)	{
									for (int i = 0; i < mapping.length; ++i) {
										if(mapping[i].getMode().equals(ParameterMappingAdapter.MODE_OUT) || mapping[i].getMode().equals(ParameterMappingAdapter.MODE_INOUT)) {
											result.put(mapping[i].getPropertyName(), cs.getObject(i + 1));
										}
									}
								}
							}

							return result;
						}
			});
		} else {
			CallMetaData callMetaData = metadata.getCallMetaData();

			if(callMetaData == null)	{
				throw new StatementEvaluateException("Stored procedure compile error. Call String is " + metadata.getStatement());
			}

			CallMetaDataContext callMetaDataContext = null;

			synchronized (CallMetaDataContext.class) {
				if(procedureCache.containsKey(callMetaData.getSchemaName() + callMetaData.getProcedureName()))	{
					callMetaDataContext = procedureCache.get(callMetaData.getSchemaName() + callMetaData.getProcedureName());

				} else {
					callMetaDataContext = new CallMetaDataContext();

					callMetaDataContext.setSchemaName(callMetaData.getSchemaName());
					callMetaDataContext.setProcedureName(callMetaData.getProcedureName());
					callMetaDataContext.setFunction(callMetaData.isFunction());
					callMetaDataContext.initializeMetaData(dataSource);
					callMetaDataContext.processParameters(new ArrayList<SqlParameter>());

					procedureCache.put(callMetaData.getSchemaName() + callMetaData.getProcedureName(), callMetaDataContext);
				}
			}

			String callString = callMetaDataContext.createCallString();

			if (logger.isDebugEnabled()) {
				logger.debug("Compiled stored procedure. Call string is [" + callString + "]");
			}

			CallableStatementCreatorFactory callableStatementFactory = new CallableStatementCreatorFactory(callString, callMetaDataContext.getCallParameters());
			callableStatementFactory.setNativeJdbcExtractor(((JdbcTemplate) jdbcTemplate.getJdbcOperations()).getNativeJdbcExtractor());

			Map<String, Object> matchedCallParams = callMetaDataContext.matchInParameterValuesWithCallParameters(metadata.getSqlParameterSource());

			CallableStatementCreator csc = callableStatementFactory.newCallableStatementCreator(matchedCallParams);

			if (logger.isDebugEnabled()) {
				logger.debug("The following parameters are used for call " + callString + " with: " + matchedCallParams);
				int i = 1;

				for (SqlParameter p : callMetaDataContext.getCallParameters()) {
					logger.debug(i++ + ": " +  p.getName() + " SQL Type "+ p.getSqlType() + " Type Name " + p.getTypeName() + " " + p.getClass().getName());
				}
			}

			return jdbcTemplate.getJdbcOperations().call(csc, callMetaDataContext.getCallParameters());
		}
	}



	/**
	 *
	 * @param <T>
	 * @param statementTemplate
	 * @param parameter
	 * @param clazz
	 * @return
	 */
	@Deprecated
	public <T> T executeProcedure(Object statementTemplate, Object parameter, Class<T> clazz) {
		return new SimpleJdbcCall(dataSource).withProcedureName((String) statementTemplate).executeObject(clazz, SqlParameterSourceBuilder.getSqlParameterSource(parameter));
	}

	/**
	*
	* @param schema
	* @param packageName
	* @param procedureName
	* @param parameter
	* @param clazz
	* @return
	*/
	public <T> T executeProcedure(String schema, String packageName, String procedureName, Object parameter, Class<T> clazz) {
		SimpleJdbcCall jdbcCall = new SimpleJdbcCall(dataSource);
		if(StringUtils.hasText(schema)){
			jdbcCall = jdbcCall.withSchemaName(schema);
		}

		if(StringUtils.hasText(packageName)){
			jdbcCall = jdbcCall.withCatalogName(packageName);
		}
		return  jdbcCall.withProcedureName(procedureName).executeObject(clazz, SqlParameterSourceBuilder.getSqlParameterSource(parameter));
	}

	/**
	 *
	 * @param statementTemplate
	 * @param parameter
	 * @return
	 */
	@Deprecated
	public Map<String, Object> executeProcedure(Object statementTemplate, Object parameter) {
		return new SimpleJdbcCall(dataSource).withProcedureName((String) statementTemplate).execute(SqlParameterSourceBuilder.getSqlParameterSource(parameter));
	}

	/**
	 *
	 * @param schema
	 * @param packageName
	 * @param procedureName
	 * @param parameter
	 * @return
	 */

	public Map<String, Object> executeProcedure(String schema, String packageName, String procedureName, Object parameter) {
		SimpleJdbcCall jdbcCall = new SimpleJdbcCall(dataSource);
		if(StringUtils.hasText(schema)){
			jdbcCall = jdbcCall.withSchemaName(schema);
		}

		if(StringUtils.hasText(packageName)){
			jdbcCall = jdbcCall.withCatalogName(packageName);
		}
		return jdbcCall.withProcedureName(procedureName).execute(SqlParameterSourceBuilder.getSqlParameterSource(parameter));
	}
	/**
	 *
	 * @param statementTemplate
	 * @param parameter
	 * @param clazz
	 * @return
	 */
	@Deprecated
	public <T> T executeFunction(Object statementTemplate, Object parameter, Class<T> clazz) {
		return new SimpleJdbcCall(dataSource).withFunctionName((String) statementTemplate).executeObject(clazz, SqlParameterSourceBuilder.getSqlParameterSource(parameter));
	}

	/**
	 *
	 * @param schema
	 * @param packageName
	 * @param functionName
	 * @param parameter
	 * @param clazz
	 * @return
	 */
	public <T> T executeFunction(String schema, String packageName, String functionName, Object parameter, Class<T> clazz) {
		SimpleJdbcCall jdbcCall = new SimpleJdbcCall(dataSource);

		if(StringUtils.hasText(schema)){
			jdbcCall = jdbcCall.withSchemaName(schema);
		}

		if(StringUtils.hasText(packageName)){
			jdbcCall = jdbcCall.withCatalogName(packageName);
		}

		return jdbcCall.withFunctionName(functionName).executeObject(clazz, SqlParameterSourceBuilder.getSqlParameterSource(parameter));
	}


	/**
	 *
	 * @param statementTemplate
	 * @param parameter
	 * @return
	 */
	@Deprecated
	public Map<String, Object> executeFunction(Object statementTemplate, Object parameter) {
		return new SimpleJdbcCall(dataSource).withFunctionName((String) statementTemplate).execute(SqlParameterSourceBuilder.getSqlParameterSource(parameter));
	}


	/**
	 *
	 * @param schema
	 * @param packageName
	 * @param functionName
	 * @param parameter
	 * @return
	 */
	public Map<String, Object> executeFunction(String schema, String packageName, String functionName, Object parameter) {
		SimpleJdbcCall jdbcCall = new SimpleJdbcCall(dataSource);

		if(StringUtils.hasText(schema)){
			jdbcCall = jdbcCall.withSchemaName(schema);
		}

		if(StringUtils.hasText(packageName)){
			jdbcCall = jdbcCall.withCatalogName(packageName);
		}

		return jdbcCall.withFunctionName(functionName).execute(SqlParameterSourceBuilder.getSqlParameterSource(parameter));
	}


	private <T> RowMapper<?> getRowMapper(QueryMetaData metadata, final Class<T> clazz){
		RowMapper<?> rowMapper = null;

		if(clazz == null || QueryUtils.isAssignableFromMap(clazz))	{
			rowMapper = new CamelCaseMapRowMapper();
		} else {
			switch (metadata.getTemplateType()) {
				case SIMPLE_ORM :
					rowMapper = new ObjectRelationMappingRowMapper<T>(clazz);
					break;
				case IBATIS :

					if(!clazz.isAssignableFrom(metadata.getResultClass()))	{
					throw new MappingRowPopulationException("StatementId("
							+ metadata.getStatementTemplate()
							+ ") 의 Return type이 일치하지 않습니다. - Type Parameter={"
							+ clazz.getName() + "}, IBatis ResultClass={"
							+ metadata.getResultClass() + "}");
					}

					rowMapper = new IBatisResultMapper<T>(metadata.getResultMappings(), clazz);
					break;
				default :
					if (QueryUtils.isPrimitiveType(clazz)) {
						rowMapper = new PrimitiveTypeRowMapper<T>(clazz);
					} else {
						rowMapper = ParameterizedBeanRowMapper.newInstance(clazz);
					}
			}
		}

		return rowMapper;
	}

	private RowMapper<Map<String, Object>> getRowMapper(boolean usePropertyName){
		RowMapper<Map<String, Object>> rowMapper = null;

		if(usePropertyName){
			rowMapper = new CamelCaseMapRowMapper();
		} else {
			rowMapper = new ColumnMapRowMapper();
		}

		return rowMapper;
	}

	private boolean isWithoutEvent()	{
		return CommonVariableHolder.isWithoutEvent() || !logger.isDebugEnabled();
	}

	public void afterPropertiesSet() throws Exception {
		if (jdbcTemplate == null || queryTemplate == null) {
			throw new BeanInitializationException("");
		}

		((JdbcTemplate)this.jdbcTemplate.getJdbcOperations()).setFetchSize(fetchSize);
	}
}