package com.example.fitforlife.Model;

import com.github.sundeepk.compactcalendarview.domain.Event;

public class EventSessions extends Event {
Session session;


    public EventSessions(int color, long timeInMillis) {
        super(color, timeInMillis);
    }

    public EventSessions(int color, long timeInMillis, Session data) {
        super(color, timeInMillis, data);
    }

    public EventSessions(int color, long timeInMillis, Object data, Session session) {
        super(color, timeInMillis, data);
        this.session = session;
    }


    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
