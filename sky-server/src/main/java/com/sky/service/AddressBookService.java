package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    /**
     * 用户添加地址
     * @param addressBook
     */
    void insert(AddressBook addressBook);

    /**
     * 获取用户所有地址
     * @return
     */
    List<AddressBook> list();

    /**
     * 获取用户默认地址
     * @return
     */
    AddressBook getDefault();

    /**
     * 用户修改地址
     * @param addressBook
     */
    void update(AddressBook addressBook);

    /**
     * 根据id获取地址
     * @param id
     * @return
     */
    AddressBook getById(Integer id);

    /**
     * 根据id删除地址
     * @param id
     */
    void deleteById(Integer id);

    /**
     * 设置用户默认地址
     * @param id
     */
    void updateDefault(Long id);
}
