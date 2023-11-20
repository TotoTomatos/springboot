package com.example.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Admin;
import com.example.entity.Business;
import com.example.exception.CustomException;
import com.example.mapper.BusinessMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BusinessService {

  @Resource
  BusinessMapper businessMapper;

  /**
   * 控制器调用
   * 新增用户
   *
   * @param business
   */
  public void add(Business business) {
    Business dbBusiness = this.selectByUsername(business.getUsername());
    if (ObjectUtil.isNotEmpty(dbBusiness)) {
      throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
    }
    business.setRole(RoleEnum.BUSINESS.name());
    int i = businessMapper.insert(business);
  }

  /**
   * 内部调用
   * 通过username查找
   *
   * @param userName
   * @return
   */
  public Business selectByUsername(String userName) {
    Business business = new Business();
    business.setUsername(userName);
    List<Business> dbBusiness = this.selectAll(business);
    return dbBusiness.size() == 0 ? null : dbBusiness.get(0);
  }


  /**
   * 内部调用
   * 根据business内提供的内容查早
   *
   * @param business
   */
  public List<Business> selectAll(Business business) {
    List<Business> businesses = businessMapper.selectAll(business);
    return businesses;
  }

  /**控制器调用
   * 内部调用
   * 通过id查找
   * @param id
   * @return
   */
  public Business selectById(Integer id) {
    Business business = new Business();
    business.setId(id);
    List<Business> dbBusiness = this.selectAll(business);
    return dbBusiness.size() == 0 ? null : dbBusiness.get(0);
  }

  /**
   * 控制器调用
   * 方法更新数据
   *
   * @param business
   */
  public void updateById(Business business) {
    Business dbBusiness1 = this.selectById(business.getId());
    if (ObjectUtil.isEmpty(dbBusiness1)) {
      throw
          new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
    }
    Business dbUsername2 = this.selectByUsername(business.getUsername());
    if (ObjectUtil.isNotEmpty(dbUsername2) && !business.getId().equals(dbUsername2.getId())) {
      throw new CustomException(ResultCodeEnum.USER_EXIST_ERROR);
    }
    businessMapper.updateById(business);
  }

  /**
   * 控制器调用
   * 查询并分页
   *
   * @param business
   * @param pageNum
   * @param pageSize
   * @return
   */
  public PageInfo<Business> selectPage(Business business, Integer pageNum, Integer pageSize) {
    PageHelper.startPage(pageNum, pageSize);
    List<Business> businesses = businessMapper.selectAll(business);
    return PageInfo.of(businesses);
  }

  /**
   * 控制器调用
   * 根据id删除
   * @param id
   */
  public void delete(Integer id) {
    businessMapper.delete(id);
  }

  /**
   * 控制器调用
   * 根据id列表批量删除
   * @param ids
   */
  public void deleteBatch(List<Integer> ids) {
    for (Integer id : ids) {
      this.delete(id);
    }
  }

  /**
   * 控制器调用
   * 注册账户
   * @param account
   */
  public void register(Account account) {
    Business business = new Business();
    BeanUtils.copyProperties(account,business);
    this.add(business);
  }

  public Account login(Account account) {
    Account dbBusiness = this.selectByUsername(account.getUsername());
    if (ObjectUtil.isNull(dbBusiness)) {
      throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
    }
    if (!account.getPassword().equals(dbBusiness.getPassword())) {
      throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
    }
    // 生成token
    String tokenData = dbBusiness.getId() + "-" + RoleEnum.BUSINESS.name();
    String token = TokenUtils.createToken(tokenData, dbBusiness.getPassword());
    dbBusiness.setToken(token);
    return dbBusiness;
  }

  public void updatePassword(Account account) {
    Business dbBusiness = this.selectByUsername(account.getUsername());
    if (ObjectUtil.isNull(dbBusiness)) {
      throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
    }
    if (!account.getPassword().equals(dbBusiness.getPassword())) {
      throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
    }
    dbBusiness.setPassword(account.getNewPassword());
    this.updateById(dbBusiness);
  }

  /**
   * 检查商家的权限  看看是否可以新增数据
   */
  public void checkBusinessAuth() {
    Account currentUser = TokenUtils.getCurrentUser();  // 获取当前的用户信息
    if (RoleEnum.BUSINESS.name().equals(currentUser.getRole())) {   // 如果是商家 的话
      Business business = selectById(currentUser.getId());
      if (!"通过".equals(business.getStatus())) {   // 未审核通过的商家  不允许添加数据
        throw new CustomException(ResultCodeEnum.NO_AUTH);
      }
    }
  }


}
