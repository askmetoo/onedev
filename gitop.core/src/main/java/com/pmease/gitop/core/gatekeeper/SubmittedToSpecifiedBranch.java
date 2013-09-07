package com.pmease.gitop.core.gatekeeper;

import com.google.common.base.Preconditions;
import com.pmease.commons.loader.AppLoader;
import com.pmease.commons.util.namedentity.EntityLoader;
import com.pmease.commons.util.namedentity.EntityMatcher;
import com.pmease.commons.util.namedentity.EntityPatternSet;
import com.pmease.commons.util.pattern.PatternSetMatcher;
import com.pmease.commons.util.pattern.WildcardPathMatcher;
import com.pmease.gitop.core.manager.BranchManager;
import com.pmease.gitop.core.model.MergeRequest;
import com.pmease.gitop.core.model.Repository;

public class SubmittedToSpecifiedBranch extends AbstractGateKeeper {

	private String branchPatterns;
	
	public String getBranchPatterns() {
		return branchPatterns;
	}

	public void setBranchPatterns(String branchPatterns) {
		this.branchPatterns = branchPatterns;
	}

	@Override
	public CheckResult check(MergeRequest request) {
		Repository repository = request.getDestination().getRepository();
		BranchManager branchManager = AppLoader.getInstance(BranchManager.class);
		EntityLoader entityLoader = branchManager.asEntityLoader(repository);
		EntityMatcher entityMatcher = new EntityMatcher(entityLoader, new WildcardPathMatcher());
		PatternSetMatcher patternSetMatcher = new PatternSetMatcher(entityMatcher);

		EntityPatternSet patternSet = EntityPatternSet.fromStored(getBranchPatterns(), entityLoader);

		if (patternSetMatcher.matches(getBranchPatterns(), request.getDestination().getName()))
			return accept("Target branch matches pattern '" + patternSet + "'.");
		else
			return reject("Target branch does not match pattern '" + patternSet + "'.");
	}

	@Override
	public Object trim(Object context) {
		Preconditions.checkArgument(context instanceof Repository);
		
		Repository repository = (Repository) context;
		BranchManager branchManager = AppLoader.getInstance(BranchManager.class);
		EntityLoader entityLoader = branchManager.asEntityLoader(repository);
		EntityPatternSet patternSet = EntityPatternSet.fromStored(getBranchPatterns(), entityLoader);
		patternSet.trim(repository);
		
		if (patternSet.getStored().isEmpty()) {
			return null;
		} else {
			setBranchPatterns(patternSet.toString());
			return this;
		}
	}

}
