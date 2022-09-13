package com.atguigu.gmall.cart.cart;

import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;

import java.util.List;

/**
 * @author Connor
 * @date 2022/9/8
 */
public interface CartService {
    /**
     * 购物车操作键
     *
     * @return
     */
    String buildCartKey();

    /**
     * 将商品添加到购物车
     * 并自动延期
     *
     * @param skuId
     * @param skuNum
     * @return
     */
    SkuInfo addToCart(Long skuId, Integer skuNum);

    /**
     * 将商品添加到购物车
     *
     * @param skuId
     * @param skuNum
     * @param cartKey
     * @return
     */
    SkuInfo addItemToCart(Long skuId, Integer skuNum, String cartKey);

    /**
     * 从购物车中取得商品
     *
     * @param skuId
     * @param cartKey
     * @return
     */
    CartInfo getItemFromCart(Long skuId, String cartKey);

    /**
     * 将CartInfo转成SkuInfo
     *
     * @param item
     * @return
     */
    SkuInfo convertCartInfo2SkuInfo(CartInfo item);

    /**
     * 获取购物车列表
     *
     * @param cartKey
     * @return
     */
    List<CartInfo> getCartList(String cartKey);

    /**
     * 是否选中物品
     *
     * @param skuId
     * @param isChecked
     * @param cartKey
     */
    void checkCart(Long skuId, Integer isChecked, String cartKey);

    /**
     * 购物车中物品数量增减
     *
     * @param skuId
     * @param increment
     * @param cartKey
     */
    void increaseItemNum(Long skuId, Integer increment, String cartKey);

    /**
     * 删除购物车中的物品
     *
     * @param skuId
     * @param cartKey
     */
    void deleteCart(Long skuId, String cartKey);

    /**
     * 删除购物车中选中的商品
     *
     * @param cartKey
     */
    void deleteChecked(String cartKey);

    /**
     * 获取购物车中选中的物品的skuId
     *
     * @param cartKey
     * @return
     */
    List<String> getCheckedItems(String cartKey);

    /**
     * 尝试合并临时购物车
     */
    void tryMergeTempCart();

    /**
     * 更新所有物品实时价格
     *
     * @param cartKey
     * @param cartInfoList
     */
    void updateAllItemsPrice(String cartKey, List<CartInfo> cartInfoList);

    /**
     * 获取购物车中的结算商品
     *
     * @param cartKey
     * @return
     */
    List<CartInfo> getChecked(String cartKey);
}
