package jcf.iam.admin.view.model;

import jcf.iam.core.jdbc.authorization.ViewResourcesMapping;

/**
 *
 * @author nolang
 *
 */
public class ViewResourcesWrapper {

	private int menuLevel;

	private ViewResourcesMapping viewResource;

	public ViewResourcesWrapper(int level, ViewResourcesMapping view) {
		this.menuLevel = level;
		this.viewResource = view;
	}

	public int getMenuLevel() {
		return menuLevel;
	}

	public void setMenuLevel(int menuLevel) {
		this.menuLevel = menuLevel;
	}

	public ViewResourcesMapping getViewResource() {
		return viewResource;
	}

	public void setViewResource(ViewResourcesMapping viewResource) {
		this.viewResource = viewResource;
	}

}
