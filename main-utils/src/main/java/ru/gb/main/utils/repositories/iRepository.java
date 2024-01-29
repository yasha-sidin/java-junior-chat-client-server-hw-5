package ru.gb.main.utils.repositories;

import java.util.List;
import java.util.Optional;

public interface iRepository<T> {
    boolean insertData(T... data);
    boolean insertData(List<T> data);
    boolean updateData(T... data);
    Optional<T> readData(long id, Class<T> clazz);
    Optional<List<T>> readAllData(Class<T> clazz);
    boolean dropData(T... data);
}
