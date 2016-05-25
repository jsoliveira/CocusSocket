package br.com.cocus.cocussocket.dao;


import br.com.cocus.cocussocket.tools.JpaUtil;
import br.com.cocus.cocussocket.tools.OperadoresJPQL;
import com.uaihebert.uaicriteria.UaiCriteria;
import com.uaihebert.uaicriteria.UaiCriteriaFactory;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;


/**
 *
 * @author jsoliveira
 */
/**
 *
 * @author jsoliveira
 * @param <I> Tipo do ID
 * @param <T> Classe de Persistência
 */
public abstract class GenericDao<I, T extends Serializable> {

    private Class<T> clazz;
    EntityManager entityManager = JpaUtil.getEntityManager();

    public void setClazz(final Class<T> clazzToSet) {
        this.clazz = clazzToSet;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    /**
     *
     * @param id valor da pesquisa
     * @return retorna uma de entidades pesquisada
     */
    public T findOne(I id) {
        entityManager = JpaUtil.getEntityManager();
        return (T) entityManager.find(clazz, id);
    }

    public List<T> findOneList(I id) {

        List<T> ret = new ArrayList<>();
        ret.add(entityManager.find(clazz, id));

        return ret;
    }

    /**
     *
     *
     * @param orderBy Campo de ordenação
     * @param asc True- Ascendente False-Descendente
     * @return retorna uma de entidades pesquisada
     */
    public List<T> findAll(String orderBy, Boolean asc) {

        try {
            UaiCriteria easyCriteria = UaiCriteriaFactory.createQueryCriteria(JpaUtil.getEntityManager(), clazz);

            if (asc) {
                easyCriteria.orderByAsc(orderBy);
            } else {
                easyCriteria.orderByDesc(orderBy);
            }

            return easyCriteria.getResultList();
        } catch (Exception e) {
            System.out.println("Erro ao consultar todos =>" + e);
            return new ArrayList<>();
        }
    }


  
    /**
     *
     * @param jpql Consulta JPQL
     * @return retorna uma lita de entidades pesquisadas
     */
    public List<T> executeJPQL(String jpql) {

        try {

            entityManager = JpaUtil.getEntityManager();
            return entityManager.createQuery(jpql).getResultList();

        } catch (Exception e) {

            System.out.println("Erro ao executar jpql =>" + e);
            return new ArrayList<>();

        }
    }

    /**
     *
     * @param jpql -Executa o jpql retornando um unico valor que deve ser feito
     * o cast
     * @return -Retorna Objeto que foi pesquisado
     */
    public Object executeJPQLUnicoValor(String jpql) {

        try {

            entityManager = JpaUtil.getEntityManager();
            return entityManager.createQuery(jpql).getSingleResult();

        } catch (Exception e) {

            System.out.println("Erro ao executar jpql =>" + e);
            return null;

        }
    }
    
     /**
     *
     * @param atributo Atributo a ser pesquisado
     * @param valorPesq Valor a ser pesquisado no atributo
     * @param operador Operador logico da consulta
     * @param orderBy Campo de ordenação
     * @param asc True- Ascendente False-Descendente
     * @return -Retorna uma lista de entidades pesquisada
     */
    
    public List<T> buscarPorAtributo(String atributo, Object valorPesq, Integer operador, String orderBy, Boolean asc) {

        try {
            UaiCriteria easyCriteria = UaiCriteriaFactory.createQueryCriteria(JpaUtil.getEntityManager(), clazz);

            if (operador.equals(OperadoresJPQL.equals.getOperador())) {

                easyCriteria.andEquals(atributo, valorPesq);

            }

            if (operador.equals(OperadoresJPQL.notEquals.getOperador())) {

                easyCriteria.andNotEquals(atributo, valorPesq);

            }

            if (operador.equals(OperadoresJPQL.like.getOperador())) {

                easyCriteria.andStringLike(atributo, "%" + (String) valorPesq + "%");

            }

            if (operador.equals(OperadoresJPQL.notLike.getOperador())) {

                easyCriteria.andStringNotLike(atributo, "%" + (String) valorPesq + "%");

            }

            if (asc) {
                easyCriteria.orderByAsc(orderBy);
            } else {
                easyCriteria.orderByDesc(orderBy);
            }

            return easyCriteria.getResultList();
        } catch (Exception e) {
            System.out.println("Erro ao consultar todos =>" + e);
            return new ArrayList<>();
        }
    }

    public void save(final T entity) {

        try {
            entityManager = JpaUtil.getEntityManager();
            entityManager.getTransaction().begin();
            entityManager.persist(entity);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("Erro ao salvar =>" + e);
            if (entityManager.isOpen()) {
                entityManager.getTransaction().rollback();
            }
        } finally {

            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public void update(final T entity) {
        try {
            entityManager = JpaUtil.getEntityManager();
            entityManager.getTransaction().begin();
            entityManager.merge(entity);
            entityManager.getTransaction().commit();

        } catch (Exception e) {
            System.out.println("Erro ao alterar =>" + e);
            if (entityManager.isOpen()) {
                entityManager.getTransaction().rollback();
            }
        } finally {

            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

    public void delete(final T entity) {
        try {
            entityManager = JpaUtil.getEntityManager();
            entityManager.getTransaction().begin();
            entityManager.remove(entityManager.merge(entity));
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("Erro ao deletar =>" + e);
            if (entityManager.isOpen()) {
                entityManager.getTransaction().rollback();
            }
        } finally {

            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }
    }

}
