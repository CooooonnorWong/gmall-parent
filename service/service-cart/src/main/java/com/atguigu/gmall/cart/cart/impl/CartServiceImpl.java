package com.atguigu.gmall.cart.cart.impl;

import com.atguigu.gmall.cart.cart.CartService;
import com.atguigu.gmall.common.utils.AuthUtils;
import com.atguigu.gmall.model.vo.user.UserAuthInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Connor
 * @date 2022/9/8
 */
@Service
@Slf4j
public class CartServiceImpl implements CartService {
    @Override
    public void addCart(Long skuId, Integer skuNum) {
        UserAuthInfo authInfo = AuthUtils.currentAuthInfo();
        Long userId = authInfo.getUserId();
        String userTempId = authInfo.getUserTempId();
        log.info("用户Id: {}, 临时用户Id: {}, 商品Id: {}, 商品数量: {}", userId, userTempId, skuId, skuNum);

    }
}
