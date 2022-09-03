package com.atguigu.gmall.search.repository;

import com.atguigu.gmall.search.bean.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Connor
 * @date 2022/9/3
 */
@Repository
public interface PersonRepository extends CrudRepository<Person, Integer> {
    /**
     * 查找该地址的所有人
     *
     * @param address
     * @return
     */
    List<Person> findAllByAddress(String address);

    /**
     * 根据id查找
     *
     * @param id
     * @return
     */
    List<Person> findAllById(Long id);
}
