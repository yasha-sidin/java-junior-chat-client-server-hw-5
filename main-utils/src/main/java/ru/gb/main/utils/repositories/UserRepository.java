package ru.gb.main.utils.repositories;

import org.hibernate.Session;
import org.hibernate.query.Query;
import ru.gb.main.utils.entities.User;
import ru.gb.main.utils.logging.Logger;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.util.Optional;

public class UserRepository extends DatabaseRepository<User> {
    private final Logger logger;
    public UserRepository(Logger logger) {
        super();
        this.logger = logger;
    }

    public Optional<User> readUserByLogin(String login) {
        Optional<User> optional = Optional.empty();
        try (Session session = connector.getSession()) {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<User> criteria = builder.createQuery(User.class);
            Root<User> root = criteria.from(User.class);
            ParameterExpression<String> nameParam = builder.parameter(String.class);
            criteria.select(root)
                    .where(builder.equal(root.get("login"), nameParam));

            Query<User> query = session.createQuery(criteria);
            query.setParameter(nameParam, login);
            optional = Optional.of(query.getSingleResult());
        } catch (Exception e) {
            logger.log(e.getMessage());
        }
        return optional;
    }
}
