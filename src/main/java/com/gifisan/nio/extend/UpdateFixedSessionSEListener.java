package com.gifisan.nio.extend;

import com.gifisan.nio.component.SEListenerAdapter;
import com.gifisan.nio.component.Session;

public class UpdateFixedSessionSEListener extends SEListenerAdapter {

	private FixedSession fixedSession = null;
	
	public UpdateFixedSessionSEListener(FixedSession fixedSession) {
		this.fixedSession = fixedSession;
	}

	public void sessionOpened(Session session) {
		fixedSession.update(session);
	}
	
	public void sessionClosed(Session session) {
		
	}

	
}
