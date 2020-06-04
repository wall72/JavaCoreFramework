package jcf.iam.admin.view.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jcf.data.GridData;
import jcf.data.RowStatus;
import jcf.iam.admin.view.model.ViewResourcesWrapper;
import jcf.iam.core.IamCustomizerFactory;
import jcf.iam.core.authentication.userdetails.model.GrantedResourceAuthority;
import jcf.iam.core.common.exception.IamException;
import jcf.iam.core.jdbc.authorization.ViewResourcesMapping;
import jcf.query.core.QueryExecutorWrapper;
import jcf.query.core.evaluator.SimpleORMQueryType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * <pre>
 * JCFIAM Admin 콘솔이 활성화 되었을때 동작하며, 화면 처리와 관련한 연산을 수행한다.
 * <pre>
 *
 * @see jcf.iam.admin.view.controller.ViewResourcesController
 * @see jcf.iam.core.IamCustomizerFactory
 *
 * @author nolang
 *
 */
@Service
public class ViewResourcesService {

	private static final Logger logger = LoggerFactory.getLogger(ViewResourcesService.class);

	@Autowired
	private IamCustomizerFactory customizer;

	@Autowired
	private QueryExecutorWrapper queryExecutor;

	/**
	 *
	 * 하위 Menu Tree 조회
	 *
	 * @param parentViewId
	 * @return
	 */
	public List<ViewResourcesWrapper> getMenuTree(String parentViewId){
		List<ViewResourcesWrapper> menus = new ArrayList<ViewResourcesWrapper>();

		try {
			traverse(menus, parentViewId, 0);
		} catch (Exception e) {
			throw new IamException(e);
		}

		return menus;
	}

	public List<? extends ViewResourcesMapping> getMenuList(final ViewResourcesMapping viewMapping) {
		ViewResourcesMapping queryView = viewMapping;

		if(queryView == null){
			queryView = BeanUtils.instantiate(customizer.getCustomizer().getViewResourceClass());
		}

		return queryExecutor.queryForList(SimpleORMQueryType.SELECT, queryView, customizer.getCustomizer().getViewResourceClass());
	}

	/**
	 *
	 * VIEW RESOURCE (MENU) 세부정보 조회
	 *
	 * @param viewId
	 * @return
	 */
	public ViewResourcesMapping getMenuDetails(String viewId) {
		final ViewResourcesMapping queryView = BeanUtils.instantiate(customizer.getCustomizer().getViewResourceClass());

		queryView.setViewResourceId(viewId);

		return queryExecutor.queryForObject(SimpleORMQueryType.SELECT, queryView, customizer.getCustomizer().getViewResourceClass());
	}

	private void traverse(List<ViewResourcesWrapper> menus, String parentViewId, int level) throws Exception {
		List<? extends ViewResourcesMapping> viewList = getViewResources(parentViewId);

		for (ViewResourcesMapping view : viewList) {
			if(logger.isDebugEnabled()){
				logger.debug("[ViewResourcesService] traverse - MenuLevel={} MenuId={}", new Object[]{level, view.getViewResourceId()});
			}

//			if(hasRole(view.getViewResourceId()))	{
				menus.add(new ViewResourcesWrapper(level, view));

				traverse(menus, view.getViewResourceId(), level + 1);
//			}
		}
	}

	/**
	 *
	 * 화면에 대한 권한을 가지고 있는지 체크한다.
	 *
	 * @param viewResourceId
	 * @return
	 */
	private boolean hasRole(String viewResourceId) {
		Collection<GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();

		for (GrantedAuthority authority : authorities) {
			/*
			 * 화면접근권한만 체크한다.
			 */
			if(authority instanceof GrantedResourceAuthority)	{
				if(authority.getAuthority().startsWith(viewResourceId)){
					return true;
				}
			}
		}

		return false;
	}

	public List<? extends ViewResourcesMapping> getViewResources(String parentViewId)
			throws InstantiationException, IllegalAccessException {
		final ViewResourcesMapping queryView = BeanUtils.instantiate(customizer.getCustomizer().getViewResourceClass());

		queryView.setParentViewId(parentViewId);

		return queryExecutor.queryForList(SimpleORMQueryType.SELECT, queryView, customizer.getCustomizer().getViewResourceClass());
	}

	/**
	 *
	 * VIEW RESOURCE (MENU) 추가
	 *
	 * @param queryView
	 * @return
	 */
	public int insertViewResources(ViewResourcesMapping queryView){
		return queryExecutor.update(SimpleORMQueryType.INSERT, queryView);
	}

	/**
	 *
	 * VIEW RESOURCE (MENU) 수정
	 *
	 * @param queryView
	 * @return
	 */
	public int updateViewResources(ViewResourcesMapping queryView){
		return queryExecutor.update(SimpleORMQueryType.UPDATE, queryView);
	}

	/**
	 *
	 * VIEW RESOURCE (MENU) 삭제
	 *
	 * @param queryView
	 * @return
	 */
	public int deleteViewResources(ViewResourcesMapping queryView){
		return queryExecutor.update(SimpleORMQueryType.DELETE, queryView);
	}

	/**
	 *
	 * VIEW RESOURCE (MENU) 추가/수정/삭제
	 *
	 * @param gridData
	 * @return
	 */
	public int saveViewResources(GridData<? extends ViewResourcesMapping> gridData)	{
		List<? extends ViewResourcesMapping> list = gridData.getList();

		int count = 0;

		for (int i = 0; i < list.size(); ++i) {
			RowStatus rowStatus = gridData.getStatusOf(i);

			if (rowStatus == RowStatus.INSERT) {
				count += insertViewResources(list.get(i));
			} else if (rowStatus == RowStatus.UPDATE) {
				count += updateViewResources(list.get(i));
			} else if (rowStatus == RowStatus.DELETE) {
				count += deleteViewResources(list.get(i));
			}
		}

		return count;
	}
}
