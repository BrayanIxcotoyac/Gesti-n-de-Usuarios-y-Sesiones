import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class SesionDAO {

    public void insertarSesion(Sesion sesion) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(sesion);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error al insertar sesión: " + e.getMessage());
        }
    }

    public List<Sesion> obtenerSesiones() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Sesion", Sesion.class).list();
        } catch (HibernateException e) {
            System.err.println("Error al obtener sesiones: " + e.getMessage());
            return null;
        }
    }
}
