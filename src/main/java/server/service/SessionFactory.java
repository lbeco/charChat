package server.service;

public class SessionFactory {
    private static Session session = new SessionImpl();

    public static Session getSession() {
        return session;
    }
}
