package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {
    private final AddressBookMapper addressBookMapper;

    public AddressBookServiceImpl(AddressBookMapper addressBookMapper) {
        this.addressBookMapper = addressBookMapper;
    }

    /**
     * 用户添加地址
     * @param addressBook
     */
    public void insert(AddressBook addressBook) {
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        addressBook.setIsDefault(0);
        addressBookMapper.insert(addressBook);
    }

    /**
     * 根据用户id获取用户所有地址
     * @return
     */
    public List<AddressBook> list() {
        Long userId = BaseContext.getCurrentId();
        List<AddressBook> list = addressBookMapper.list(userId);
        return list;
    }

    /**
     * 获取用户默认地址
     * @return
     */
    public AddressBook getDefault() {
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = addressBookMapper.getDefault(userId);
        return addressBook;
    }


    /**
     * 用户修改地址
     * @param addressBook
     */
    public void update(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    /**
     * 根据id获取地址
     * @param id
     * @return
     */
    public AddressBook getById(Integer id) {
        AddressBook addressBook = addressBookMapper.getById(id);
        return addressBook;
    }

    /**
     *根据id删除地址
     * @param id
     */
    public void deleteById(Integer id) {
        addressBookMapper.deleteById(id);
    }

    /**
     * 设置用户默认地址
     * @param id
     */
    public void updateDefault(Long id) {
        Long userId = BaseContext.getCurrentId();
        AddressBook addressBook = addressBookMapper.getDefault(userId);
        if (addressBook != null) {
            addressBook.setIsDefault(0);
            addressBookMapper.updateDefault(addressBook);
        }

        addressBook = AddressBook.builder()
                .isDefault(1)
                .id(id)
                .build();
        addressBookMapper.updateDefault(addressBook);
    }
}
