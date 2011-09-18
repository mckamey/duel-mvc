package org.duelengine.duel.mvc;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class ResultFilterContext {

	private final DuelMvcContext context;
	private ViewResult viewResult;

	@AssistedInject
	public ResultFilterContext(
			@Assisted ViewResult viewResult) {

		if (viewResult == null) {
			throw new NullPointerException("viewResult");
		}

		this.viewResult = viewResult;
		this.context = viewResult.getContext();
	}

	public DuelMvcContext getMvcContext() {
		return context;
	}

	public ViewResult getViewResult() {
		return viewResult;
	}

	public void setViewResult(ViewResult value) {
		if (value == null) {
			throw new NullPointerException("value");
		}

		viewResult = value;
	}
}
