package com.epam.gymapp.repository;

import java.util.List;
import java.util.Optional;

/**
 * The Dao interface defines the basic CRUD operations for managing entities of type T.
 * It serves as a generic data access object (DAO) interface that can be implemented
 * for any entity type.
 *
 * @param <T> The type of entity managed by this DAO.
 */
public interface Dao<T> {
    
    Optional<T> get(Long id);

    Optional<T> getById(Long id);
    
    List<T> getAll();
    
    T save(T t);
    
    T update(Long id, T t);
    
    void delete(Long id);   
}