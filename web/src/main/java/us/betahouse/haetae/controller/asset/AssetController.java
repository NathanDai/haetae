/*
 * betahouse.us
 * CopyRight (c) 2012 - 2019
 */
package us.betahouse.haetae.controller.asset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import us.betahouse.haetae.activity.dal.service.OrganizationRepoService;
import us.betahouse.haetae.activity.model.basic.OrganizationBO;
import us.betahouse.haetae.asset.dal.service.AssetRepoService;
import us.betahouse.haetae.asset.model.basic.AssetBO;
import us.betahouse.haetae.asset.model.basic.AssetRecordBO;
import us.betahouse.haetae.common.log.LoggerName;
import us.betahouse.haetae.common.session.CheckLogin;
import us.betahouse.haetae.common.template.RestOperateCallBack;
import us.betahouse.haetae.common.template.RestOperateTemplate;
import us.betahouse.haetae.model.asset.request.AssetRestRequest;
import us.betahouse.haetae.serviceimpl.asset.request.AssetManagerRequest;
import us.betahouse.haetae.serviceimpl.asset.request.builder.AssetManagerRequestBuilder;
import us.betahouse.haetae.serviceimpl.asset.service.AssetService;
import us.betahouse.haetae.serviceimpl.common.OperateContext;
import us.betahouse.haetae.utils.IPUtil;
import us.betahouse.haetae.utils.RestResultUtil;
import us.betahouse.util.common.Result;
import us.betahouse.util.enums.RestResultCode;
import us.betahouse.util.log.Log;
import us.betahouse.util.utils.AssertUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * 物资接口
 * @author guofan.cp
 * @version : AssetController.java 2019/01/23 9:06 guofan.cp
 */
@CrossOrigin
@RestController
@RequestMapping(value = "/asset")
public class AssetController {
    /**
     *日志实体
     */
    private final Logger LOGGER= LoggerFactory.getLogger(AssetController.class);

    @Autowired
    private AssetService assetService;

    @Autowired
    private OrganizationRepoService organizationRepoService;

    @Autowired
    private AssetRepoService assetRepoService;

    /**
     * 添加物资
     */
    @CheckLogin
    @PostMapping(value = "asset")
    @Log(loggerName = LoggerName.WEB_DIGEST)
    public Result<AssetBO> asset(AssetRestRequest request, HttpServletRequest httpServletRequest){
        return RestOperateTemplate.operate(LOGGER, "新增物资", request, new RestOperateCallBack<AssetBO>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, RestResultCode.ILLEGAL_PARAMETERS.getCode(), "请求体不能为空");
                AssertUtil.assertStringNotBlank(request.getAssetName(), RestResultCode.ILLEGAL_PARAMETERS.getCode(), "物资名不能为空");
                AssertUtil.assertStringNotBlank(request.getAssetType(), RestResultCode.ILLEGAL_PARAMETERS.getCode(), "物资类型不能为空");
                AssertUtil.assertStringNotBlank(request.getOrganizationName(), RestResultCode.ILLEGAL_PARAMETERS.getCode(), "物资归属组织不能为空");
                AssertUtil.assertNotNull(request.getAssetAmount(), RestResultCode.ILLEGAL_PARAMETERS.getCode(), "物资数量不能为空");
                AssertUtil.assertStringNotBlank(Integer.valueOf(request.getAssetAmount())>0?"1":"", RestResultCode.ILLEGAL_PARAMETERS.getCode(),"物资数量不能小于0");
            }

            @Override
            public Result<AssetBO> execute() {
                OperateContext context = new OperateContext();
                context.setOperateIP(IPUtil.getIpAddr(httpServletRequest));
                /**
                 * 通过组织名字查找组织id
                 */
                OrganizationBO organizationBo = organizationRepoService.queryOrganizationByName(request.getOrganizationName());
                AssertUtil.assertNotNull(organizationBo,RestResultCode.ILLEGAL_PARAMETERS.getCode(),"物资归属组织不存在");
                String organizationId=organizationBo.getOrganizationId();
                AssetManagerRequest assetManagerRequest=AssetManagerRequestBuilder.getInstance()
                        .withAssetName(request.getAssetName())
                        .withAmount(Integer.valueOf(request.getAssetAmount()))
                        //组织id在上面获得
                        .withOrginazationId(organizationId)
                        .withType(request.getAssetType())
                        .withAssetOrganizationName(request.getOrganizationName())
                        .withReamain(Integer.valueOf(request.getAssetAmount()))
                        .withStatus("可借")
                        .withDestory(0)
                        //以下是可选参数
                        //额外信息
                        .withExtInfo(request.getExtInfo())
                        .builder();
                AssetBO assetBo=assetService.create(assetManagerRequest,context);
                return RestResultUtil.buildSuccessResult(assetBo,"物资创建成功");
            }
        });
    }



    /**
     * 判断物资状态
     *
     * @param restRequest
     * @param httpServletRequest
     * @return
     */
    @CheckLogin
    @GetMapping("AssetStatus")
    @Log(loggerName = LoggerName.WEB_DIGEST)
    public Result<List<AssetRecordBO>> AssetStatus(AssetRestRequest restRequest, HttpServletRequest httpServletRequest){
        return RestOperateTemplate.operate(LOGGER, "判断物资状态", restRequest, new RestOperateCallBack<List<AssetRecordBO>>() {
            @Override
            public void before(){
                AssertUtil.assertNotNull(restRequest,RestResultCode.ILLEGAL_PARAMETERS.getCode(),"请求体不能为空");
                AssertUtil.assertNotNull(restRequest.getAssetId(),RestResultCode.ILLEGAL_PARAMETERS.getCode(),"物资ID不能为空");
                AssertUtil.assertStringNotBlank(restRequest.getAssetId(),RestResultCode.ILLEGAL_PARAMETERS.getCode(),"物资ID不存在");
            }
            @Override
            public Result<List<AssetRecordBO>> execute() {
                OperateContext context=new OperateContext();
                context.setOperateIP(IPUtil.getIpAddr(httpServletRequest));
                //根据物资id判断物资状态
                String assetStatusCode= assetRepoService.judgeStatusByAssetId(restRequest.getAssetId());
                AssetManagerRequest assetManagerRequest=AssetManagerRequestBuilder.getInstance()
                        .withAssetId(restRequest.getAssetId())
                        .withAssetStatusCode(assetStatusCode)
                        .builder();
                List<AssetRecordBO> assetRecordBOList=assetService.findRecodByAssetStatus(assetManagerRequest,context);
                return RestResultUtil.buildSuccessResult(assetRecordBOList, assetStatusCode);
            }
        });
    }
}
