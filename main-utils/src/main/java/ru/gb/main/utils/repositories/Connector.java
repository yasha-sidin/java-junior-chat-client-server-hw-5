package ru.gb.main.utils.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.TimeZone;

public class Connector {
    private final StandardServiceRegistry registry;
    private final SessionFactory sessionFactory;

    public Connector() {
        registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    public Session getSession() {
        return sessionFactory.withOptions()
                .jdbcTimeZone(TimeZone.getTimeZone("UTC"))
                .openSession();
    }
}
