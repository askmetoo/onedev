package com.pmease.gitop.core.manager.impl;

import javax.inject.Provider;
import javax.inject.Singleton;

import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.pmease.commons.persistence.Transactional;
import com.pmease.commons.persistence.dao.DefaultGenericDao;
import com.pmease.commons.persistence.dao.GeneralDao;
import com.pmease.gitop.core.manager.VoteInvitationManager;
import com.pmease.gitop.core.model.MergeRequest;
import com.pmease.gitop.core.model.VoteInvitation;
import com.pmease.gitop.core.model.User;

@Singleton
public class DefaultVoteInvitationManager extends DefaultGenericDao<VoteInvitation> implements VoteInvitationManager {

	public DefaultVoteInvitationManager(GeneralDao generalDao, Provider<Session> sessionProvider) {
		super(generalDao, sessionProvider);
	}

	@Transactional
	@Override
	public VoteInvitation find(User reviewer, MergeRequest request) {
		return find(new Criterion[]{Restrictions.eq("reviewer", reviewer), Restrictions.eq("request", request)});
	}

	@Transactional
	@Override
	public void save(VoteInvitation voteInvitation) {
		if (voteInvitation.getId() == null) {
			voteInvitation.getRequest().getVoteInvitations().add(voteInvitation);
			voteInvitation.getReviewer().getVoteInvitations().add(voteInvitation);
		}
		super.save(voteInvitation);
	}

	@Transactional
	@Override
	public void delete(VoteInvitation voteInvitation) {
		voteInvitation.getRequest().getVoteInvitations().remove(voteInvitation);
		voteInvitation.getReviewer().getVoteInvitations().remove(voteInvitation);
		super.delete(voteInvitation);
	}

}
