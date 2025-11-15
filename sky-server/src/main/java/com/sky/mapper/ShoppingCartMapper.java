package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 查询记录
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);


    /**
     * 商品数量修改
     * @param shoppingCart
     */

    void updateNumber(ShoppingCart shoppingCart);

    /**
     * 添加商品
     * @param shoppingCart
     */
    @Insert("insert into shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time) VALUE " +
            "(#{name},#{image},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{createTime})")
    void insert(ShoppingCart shoppingCart);

    /**
     * 根据用户删除购物车
     * @param currentId
     */
    @Delete("delete  from shopping_cart where user_id = #{currentId}")
    void deleteByUserId(Long currentId);

    /**
     * 删除对应商品记录
     * @param shoppingCart
     */
    void delete(ShoppingCart shoppingCart);
}
