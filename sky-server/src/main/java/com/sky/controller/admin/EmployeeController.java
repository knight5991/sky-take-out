package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("员工登录方法")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("员工退出方法")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     * @return
     */
    @PostMapping
    @ApiOperation("新增员工方法")
   public Result save(@RequestBody EmployeeDTO employeeDTO){
        employeeService.save(employeeDTO);
        log.info("添加员工：{}",employeeDTO.getUsername());
        return Result.success();
   }

    /**
     * 分页查询员工
     * @param employeePageQueryDTO
     * @return
     */
   @ApiOperation("分页查询员工方法")
   @GetMapping("/page")
    public  Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("分页查询员工:{}",employeePageQueryDTO);
        PageResult pageResult = employeeService.selectPage(employeePageQueryDTO);
        return  Result.success(pageResult);
   }

    /**
     * 启用或禁用员工
     * @param status
     * @param id
     * @return
     */
   @ApiOperation("启用，禁用员工方法")
   @PostMapping("/status/{status}")
   public Result stopOrOpen(@PathVariable int status, Long id){
       log.info("修改员工状态{},{}",status,id);
       employeeService.stopOrOpen(status,id);
        return Result.success();
   }

   @ApiOperation("根据id查询员工信息")
   @GetMapping("/{id}")
   public Result<Employee> getById(@PathVariable Long id){
       log.info("查询id为{}的员工信息",id);
       Employee  employ = employeeService.getById(id);
       return Result.success(employ);
   }

    /**
     * 更新员工信息方法
     * @return
     */
    @ApiOperation("更新员工信息方法")
    @PutMapping
   public Result update(@RequestBody EmployeeDTO employeeDTO){
        log.info("更新员工信息：{}",employeeDTO);
        employeeService.update(employeeDTO);
       return Result.success();
   }
}
