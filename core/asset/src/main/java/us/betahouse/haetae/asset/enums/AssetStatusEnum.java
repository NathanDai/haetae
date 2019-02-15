/*
 * betahouse.us
 * CopyRight (c) 2012 - 2019
 */
package us.betahouse.haetae.asset.enums;

import org.apache.commons.lang.StringUtils;

/**
 * @author yiyuk.hxy
 * @version : AssetStatusEnum.java 2019/01/27 19:05 yiyuk.hxy
 */
public enum AssetStatusEnum {
    ASSET_LOAN("assetLoan", "可借"),
    ASSET_NOTLOAN("assetNotLoan", "不可借"),
    ASSET_ALLLOAN("assetAllLoam","全部借出"),
    ASSET_DISTORY("assetDistory", "全部损坏"),
    ASSET_TEMPNOTLOAN("assetTempNotLoan","暂时不可借"),
    ASSET_NOTEXISTENCE("assetNotExistence", "物资不存在");

    private String code;

    private String desc;

    AssetStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static AssetStatusEnum getByCode(String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        for (AssetStatusEnum assetStatusEnum : values()) {
            if (StringUtils.equals(assetStatusEnum.getCode(), code)) {
                return assetStatusEnum;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
