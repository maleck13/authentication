package me.web.authentication.dao;

import io.dropwizard.hibernate.AbstractDAO;
import me.web.authentication.core.Authenticate;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 * Created by craigbrookes on 13/03/15.
 */
public class AuthenticateDao extends AbstractDAO<Authenticate> {

  public AuthenticateDao(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public Authenticate findById(Long id) {
    return get(id);
  }

  public Authenticate createUpdate(Authenticate auth) {
    return persist(auth);
  }

  public Authenticate findByUserId(String id){
    Criteria criteria = this.criteria();
    criteria.add(Restrictions.eq("userid", id));
    return uniqueResult(criteria);
  }
  public Authenticate findUserByAuthToken(String token){
    Criteria criteria = this.criteria();
    criteria.add(Restrictions.eq("authtoken",token));
    return uniqueResult(criteria);
  }
}
