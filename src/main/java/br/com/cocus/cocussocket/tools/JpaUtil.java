package br.com.cocus.cocussocket.tools;

/**
 *
 * @author jsoliveira
 */


import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaUtil {

    private static final String PERSISTENCE_UNIT_NAME = "Persistence";

    private static final ThreadLocal<EntityManager> manager = new ThreadLocal<>();

    private static EntityManagerFactory factory;

    private JpaUtil() {
    }

    public static EntityManager getEntityManager() {
        if (JpaUtil.factory == null) {
            JpaUtil.factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        }
        
        EntityManager m = JpaUtil.manager.get();
        if (m == null || !m.isOpen()) {
            m = JpaUtil.factory.createEntityManager();
            JpaUtil.manager.set(m);
        }
        
        return m;
    }

    public static void closeEntityManager() {
        EntityManager m = JpaUtil.manager.get();
        if (m != null) {
            EntityTransaction t = m.getTransaction();
            if (t.isActive()) { 
               
                t.commit();
            }
            m.flush();
            m.close();
            JpaUtil.manager.set(null);
        }
    }

    public static void closeEntityManagerFactory() {
        closeEntityManager();
        JpaUtil.factory.close();
    }
}
