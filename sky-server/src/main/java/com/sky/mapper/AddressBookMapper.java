package com.sky.mapper;

import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AddressBookMapper {
    /**
     * 用户添加地址
     * @param addressBook
     */
    @Insert("insert into address_book (user_id, consignee, sex, phone, province_code, province_name, city_code, city_name, district_code, district_name, detail, label, is_default) " +
            "VALUE (#{userId},#{consignee},#{sex},#{phone},#{provinceCode},#{provinceName},#{cityCode},#{cityName},#{districtCode},#{districtName},#{detail},#{label},#{isDefault})")
    void insert(AddressBook addressBook);

    /**
     * 根据用户id获取所有地址
     * @param userId
     * @return
     */
    @Select("select * from  address_book where user_id = #{userId}")
    List<AddressBook> list(Long userId);

    /**
     * 根据用户id获取用户默认地址
     * @param userId
     * @return
     */
    @Select("select * from address_book where user_id = #{userId} and is_default = 1")
    AddressBook getDefault(Long userId);

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
    @Select("select * from address_book where id = #{id}")
    AddressBook getById(Integer id);

    /**
     * 根据id删除地址
     * @param id
     */
    @Delete("delete from address_book  where id = #{id}")
    void deleteById(Integer id);

    /**
     * 用户设置默认地址
     * @param addressBook
     */
    @Update("update address_book set is_default = #{isDefault} where id = #{id} ")
    void updateDefault(AddressBook addressBook);
}
