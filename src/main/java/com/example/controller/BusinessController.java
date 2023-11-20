package com.example.controller;


import cn.hutool.core.util.ObjectUtil;
import com.example.common.Result;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.Business;
import com.example.service.BusinessService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/business")
public class BusinessController {


    @Resource
    BusinessService businessService;

    @GetMapping("/selectAll")
    public Result selectAll(Business business){
        List<Business> businesses = businessService.selectAll(business);
        return Result.success(businesses);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Business business){
        if( ObjectUtil.isEmpty(business.getUsername()) || ObjectUtil.isEmpty(business.getPassword())){
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        businessService.add(business);
        return Result.success();
    }


    @PutMapping("update")
    public Result updateById(@RequestBody Business business){
        businessService.updateById(business);
        return Result.success();
    }

    @DeleteMapping("delete/{id}")
    public Result delet(@PathVariable Integer id){
        if(ObjectUtil.isEmpty(id)){
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        businessService.delete(id);
        return Result.success();

    }

    @DeleteMapping("delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        businessService.deleteBatch(ids);
        return Result.success();
    }


    @GetMapping("selectById/{id}")
    public Result selectById(@PathVariable Integer id){
        Business business = businessService.selectById(id);
        return Result.success(business);
    }

    @GetMapping("/selectPage")
    public Result selectPage(Business business,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestParam(defaultValue = "1") Integer pageNum){
        PageInfo<Business> pageInfo = businessService.selectPage(business,pageNum,pageSize);
        return Result.success(pageInfo);
    }

}
