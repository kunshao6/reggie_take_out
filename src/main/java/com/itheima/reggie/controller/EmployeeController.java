package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request,@RequestBody Employee employee) {
        //将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //根据页面提交的用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        //如果未查找到，则返回登录失败
        if (emp == null) {
            return R.error("登录失败");
        }
        //密码对比，如果对比失败则返回登录失败
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败");
        }
        //查看员工状态，如果已禁用状态，则返回账号已禁用
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }
        //登录成功，将员工id存入Session并返回成功结果
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
        }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
        public R<String> logout(HttpServletRequest request){
        //清理Session中保存的当前员工的id
            request.getSession().removeAttribute("employee");
            return R.success("退出成功");
        }

    }

