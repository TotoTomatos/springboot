package com.example.service;

import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Category;
import com.example.mapper.CategoryMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 商品分类业务处理
 **/
@Service
public class CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private BusinessService businessService;
    /**
     * 新增
     */
    public void add(Category category) {
        businessService.checkBusinessAuth();
        Account currentUser = TokenUtils.getCurrentUser();
        if(RoleEnum.BUSINESS.name().equals(currentUser.getRole())){
            category.setBusinessId(currentUser.getId());
        }
        categoryMapper.insert(category);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        businessService.checkBusinessAuth();
        categoryMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        businessService.checkBusinessAuth();
        for (Integer id : ids) {
            categoryMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Category category) {
        categoryMapper.updateById(category);
    }

    /**
     * 根据ID查询
     */
    public Category selectById(Integer id) {
        return categoryMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Category> selectAll(Category category) {
        Account currentUser = TokenUtils.getCurrentUser();
        String role = currentUser.getRole();
        if(RoleEnum.BUSINESS.name().equals(role)){//商家只能查询到自己的商品
            category.setBusinessId(currentUser.getId());//将商家id加入作为限制条件
        }
        return categoryMapper.selectAll(category);
    }

    /**
     * 分页查询
     */
    public PageInfo<Category> selectPage(Category category, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        String role = currentUser.getRole();
        if(RoleEnum.BUSINESS.name().equals(role)){//商家只能查询到自己的商品
            category.setBusinessId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Category> list = categoryMapper.selectAll(category);
        return PageInfo.of(list);
    }

}