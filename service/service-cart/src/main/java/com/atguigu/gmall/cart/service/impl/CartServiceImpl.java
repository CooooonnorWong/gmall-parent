package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.common.utils.AuthUtils;
import com.atguigu.gmall.feign.product.ProductFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.vo.user.UserAuthInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Connor
 * @date 2022/9/8
 */
@Service
@Slf4j
public class CartServiceImpl implements CartService {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private ProductFeignClient productFeignClient;
    @Autowired
    private ExecutorService executor;

    /**
     * 将商品添加到购物车
     * 并自动延期
     *
     * @param skuId
     * @param skuNum
     * @return
     */
    @Override
    public SkuInfo addToCart(Long skuId, Integer skuNum) {
        UserAuthInfo authInfo = AuthUtils.currentAuthInfo();
        SkuInfo skuInfo = addItemToCart(skuId, skuNum, buildCartKey());
        if (authInfo.getUserId() == null) {
            //一直未登录,维护临时购物车,自动延期
            redisTemplate.expire(buildCartKey(), 90, TimeUnit.DAYS);
        }
        return skuInfo;
    }

    /**
     * 将商品添加到购物车
     *
     * @param skuId
     * @param skuNum
     * @param cartKey
     * @return
     */
    @Override
    public SkuInfo addItemToCart(Long skuId, Integer skuNum, String cartKey) {
        //获取和该购物车key绑定的hash操作
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);
        Long cartSize = hashOps.size();
        if (!hashOps.hasKey(skuId.toString())) {
            //说明是第一次向购物车添加该物品
            if (cartSize + 1 > SysRedisConst.CART_ITEMS_LIMIT) {
                throw new GmallException(ResultCodeEnum.CART_OVERFLOW);
            }
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId).getData();
            CompletableFuture.supplyAsync(() -> {
                        CartInfo item = convertSkuInfo2CartInfo(skuInfo);
                        item.setSkuNum(skuNum);
                        return item;
                    })
                    .thenAccept(item -> hashOps.put(skuId.toString(), Jsons.toStr(item)))
                    .join();
            return skuInfo;
        } else {
            CartInfo item = getItemFromCart(skuId, cartKey);
            item.setSkuNum(item.getSkuNum() + skuNum > SysRedisConst.CART_ITEM_NUM_LIMIT ?
                    SysRedisConst.CART_ITEM_NUM_LIMIT :
                    item.getSkuNum() + skuNum);
            item.setSkuPrice(productFeignClient.getRealTimePrice(skuId).getData());
            item.setCartPrice(item.getSkuPrice().multiply(new BigDecimal(item.getSkuNum().toString())));
            item.setUpdateTime(new Date());
            hashOps.put(skuId.toString(), Jsons.toStr(item));
            return convertCartInfo2SkuInfo(item);
        }
    }

    /**
     * 从购物车中取得商品
     *
     * @param skuId
     * @param cartKey
     * @return
     */
    @Override
    public CartInfo getItemFromCart(Long skuId, String cartKey) {
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);
        String jsonStr = hashOps.get(skuId.toString());
        return Jsons.toObj(jsonStr, CartInfo.class);
    }

    /**
     * 将CartInfo转成SkuInfo
     *
     * @param item
     * @return
     */
    @Override
    public SkuInfo convertCartInfo2SkuInfo(CartInfo item) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setSkuName(item.getSkuName());
        skuInfo.setSkuDefaultImg(item.getImgUrl());
        skuInfo.setId(item.getSkuId());
        return skuInfo;
    }

    /**
     * 将SkuInfo转成CartInfo
     *
     * @param skuInfo
     * @return
     */
    public CartInfo convertSkuInfo2CartInfo(SkuInfo skuInfo) {
        CartInfo cartInfo = new CartInfo();
        cartInfo.setSkuNum(1);
        cartInfo.setSkuId(skuInfo.getId());
        cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
        cartInfo.setSkuName(skuInfo.getSkuName());
        cartInfo.setIsChecked(1);
        cartInfo.setSkuPrice(skuInfo.getPrice());
        cartInfo.setCartPrice(skuInfo.getPrice());
        cartInfo.setCreateTime(new Date());
        cartInfo.setUpdateTime(new Date());
        return cartInfo;
    }

    /**
     * 购物车操作键
     *
     * @return
     */
    @Override
    public String buildCartKey() {
        UserAuthInfo authInfo = AuthUtils.currentAuthInfo();
        return authInfo.getUserId() == null ?
                SysRedisConst.CART_KEY + authInfo.getUserTempId() :
                SysRedisConst.CART_KEY + authInfo.getUserId();
    }

    @Override
    public List<CartInfo> getCartList(String cartKey) {
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);
        List<CartInfo> cartInfoList = hashOps.values().stream()
                .map(jsonStr -> Jsons.toObj(jsonStr, CartInfo.class))
                .sorted((v, k) -> v.getCreateTime().compareTo(k.getCreateTime()))
                .collect(Collectors.toList());
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        executor.submit(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            // 如果传入已经查出来的CartInfo集合则存在时间差问题(此时另外调用删除购物车方法有可能因为此更新方法中的put操作导致无法删除)
            // 必须让更新方法自己从redis中查
            updateAllItemsPrice(cartKey);
            RequestContextHolder.resetRequestAttributes();
        });

        return cartInfoList;
    }

    @Override
    public void updateAllItemsPrice(String cartKey) {
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        hashOps.values()
                .stream()
                .parallel()
                .map(str -> Jsons.toObj(str, CartInfo.class))
                .forEach(item -> {
                    RequestContextHolder.setRequestAttributes(requestAttributes);
                    BigDecimal price = productFeignClient.getRealTimePrice(item.getSkuId()).getData();
                    item.setSkuPrice(price);
                    item.setCartPrice(item.getSkuPrice().multiply(new BigDecimal(item.getSkuNum().toString())));
                    item.setUpdateTime(new Date());
                    //更新redis中的价格
                    //判断是否还存在该key 防止删除购物车后又进行添加
                    if (hashOps.hasKey(item.getSkuId().toString())) {
                        hashOps.put(item.getSkuId().toString(), Jsons.toStr(item));
                    }
                    RequestContextHolder.resetRequestAttributes();
                });
    }

    @Override
    public List<CartInfo> getChecked(String cartKey) {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        CompletableFuture<List<CartInfo>> cartFuture = CompletableFuture.supplyAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<CartInfo> cartList = this.getCartList(cartKey);
            RequestContextHolder.resetRequestAttributes();
            return cartList;
        }, executor);
        CompletableFuture<List<String>> skuIdFuture = CompletableFuture.supplyAsync(() -> getCheckedItems(cartKey), executor);
        List<CartInfo> cartList = cartFuture.join();
        List<String> skuIds = skuIdFuture.join();
        return cartList.stream()
                .filter(cartInfo -> skuIds.contains(cartInfo.getSkuId().toString()))
                .collect(Collectors.toList());
    }

    @Override
    public void checkCart(Long skuId, Integer isChecked, String cartKey) {
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);
        String jsonStr = hashOps.get(skuId.toString());
        CartInfo cartInfo = Jsons.toObj(jsonStr, CartInfo.class);
        if (cartInfo != null) {
            cartInfo.setIsChecked(isChecked);
            cartInfo.setUpdateTime(new Date());
            hashOps.put(skuId.toString(), Jsons.toStr(cartInfo));
        }
    }

    @Override
    public void increaseItemNum(Long skuId, Integer increment, String cartKey) {
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);
        String jsonStr = hashOps.get(skuId.toString());
        CartInfo cartInfo = Jsons.toObj(jsonStr, CartInfo.class);
        if (cartInfo != null) {
            cartInfo.setSkuNum(cartInfo.getSkuNum() + increment);
            cartInfo.setUpdateTime(new Date());
            hashOps.put(skuId.toString(), Jsons.toStr(cartInfo));
        }
    }

    @Override
    public void deleteCartItem(Long skuId, String cartKey) {
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);
        hashOps.delete(skuId.toString());
    }

    @Override
    public void deleteChecked(String cartKey) {
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);
        List<String> list = getCheckedItems(cartKey);
        if (list.size() > 0) {
            hashOps.delete(list.toArray());
        }
    }

    @Override
    public List<String> getCheckedItems(String cartKey) {
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);
        return hashOps.values()
                .stream()
                .map(str -> Jsons.toObj(str, CartInfo.class))
                .filter(v -> v.getIsChecked() == 1)
                .map(v -> v.getSkuId().toString())
                .collect(Collectors.toList());
    }

    @Override
    public void tryMergeTempCart() {
        UserAuthInfo authInfo = AuthUtils.currentAuthInfo();
        if (authInfo.getUserId() != null && !StringUtils.isEmpty(authInfo.getUserTempId())) {
            //用户登陆了且存在临时购物车
            String tempCartKey = SysRedisConst.CART_KEY + authInfo.getUserTempId();
            List<CartInfo> cartList = getCartList(tempCartKey);
            if (cartList != null && cartList.size() > 0) {
                cartList.forEach(cartInfo ->
                {
                    addItemToCart(cartInfo.getSkuId(), cartInfo.getSkuNum(), SysRedisConst.CART_KEY + authInfo.getUserId());
                    redisTemplate.opsForHash().delete(tempCartKey, cartInfo.getSkuId().toString());
                });
            }
        }
    }
}
