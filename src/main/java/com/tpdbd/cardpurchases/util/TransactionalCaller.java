package com.tpdbd.cardpurchases.util;

import java.util.function.Consumer;

import jakarta.persistence.EntityManager;

/**
 * Aternative to @Transactional when the entity manager is needed
 * or when several transactions are used in the same function
 */
public class TransactionalCaller implements AutoCloseable {
    private EntityManager entityManager;

    public TransactionalCaller(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void call(Runnable runnable) {
        try {
            this.entityManager.getTransaction().begin();
            runnable.run();
            this.entityManager.getTransaction().commit();
        } 
        catch (Exception ex) {
            this.entityManager.getTransaction().rollback();
        }
    }

    public void call(Consumer<EntityManager> consumer) {
        try {
            this.entityManager.getTransaction().begin();
            consumer.accept(this.entityManager);
            this.entityManager.getTransaction().commit();
        } 
        catch (Exception ex) {
            this.entityManager.getTransaction().rollback();
        }
    }

    @Override
    public void close() throws Exception {
        this.entityManager.close();
    }
}