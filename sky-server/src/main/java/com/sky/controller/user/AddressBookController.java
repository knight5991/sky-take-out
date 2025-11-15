package com.sky.controller.user;


import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userAddressBookController")
@RequestMapping("/user/addressBook")
@Api(tags = "c端用户地址接口")
@Slf4j
public class AddressBookController {

    private final AddressBookService addressBookService;

    public AddressBookController(AddressBookService addressBookService) {
        this.addressBookService = addressBookService;
    }

    /**
     * 用户添加地址
     * @param addressBook
     * @return
     */
    @PostMapping
    @ApiOperation("用户添加地址")
    public Result add(@RequestBody AddressBook addressBook){
        addressBookService.insert(addressBook);
        return Result.success();
    }

    /**
     * 获取用户所有地址
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("获取用户所有地址")
    public Result<List<AddressBook>> list(){
        List<AddressBook> list = addressBookService.list();
        return Result.success(list);
    }

    /**
     * 获取用户默认地址
     * @return
     */
    @GetMapping("/default")
    @ApiOperation("获取用户默认地址")
    public Result<AddressBook> getDefault(){
        AddressBook addressBook = addressBookService.getDefault();
        if (addressBook == null)
            return Result.error("没有查询到默认地址");
        return Result.success(addressBook);
    }

    /**
     * 用户修改地址
     * @param addressBook
     * @return
     */
    @PutMapping
    @ApiOperation("用户修改地址")
    public Result update(@RequestBody AddressBook addressBook){
        addressBookService.update(addressBook);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id获取地址")
    public Result<AddressBook> getById(@PathVariable Integer id){
        AddressBook addressBook = addressBookService.getById(id);
        return Result.success(addressBook);
    }

    /**
     * 删除地址
     * @param id
     * @return
     */
    @DeleteMapping
    @ApiOperation("根据id删除地址记录")
    public Result deleteById(Integer id){
        addressBookService.deleteById(id);
        return Result.success();
    }

    /**
     * 根据id设置默认地址
     * @param
     * @return
     */
    @PutMapping("/default")
    @ApiOperation("设置用户默认地址")
    public Result updateDefault(@RequestBody AddressBook addressBook){
        addressBookService.updateDefault(addressBook.getId());
        return Result.success();
    }
}
