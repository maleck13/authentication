package me.web.authentication.dao;

import io.dropwizard.hibernate.AbstractDAO;
import me.web.authentication.core.Authentication;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;


public class AuthenticateDao extends AbstractDAO<Authentication> {

  public AuthenticateDao(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public Authentication findById(Long id) {
    return get(id);
  }

  public Authentication createUpdate(Authentication auth) {
    try {
      return persist(auth);
    }catch (ConstraintViolationException e){
      this.currentSession().clear();
      throw e;
    }
  }

  public Authentication findByUserId(String id){
    Criteria criteria = this.criteria();
    criteria.add(Restrictions.eq("userid", id));
    return uniqueResult(criteria);
  }
  public Authentication findUserByAuthToken(String token){
    Criteria criteria = this.criteria();
    criteria.add(Restrictions.eq("authtoken",token));
    return uniqueResult(criteria);
  }
}
