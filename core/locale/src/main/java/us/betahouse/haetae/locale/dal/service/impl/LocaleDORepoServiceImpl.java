package us.betahouse.haetae.locale.dal.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.betahouse.haetae.locale.dal.repo.LocaleDORepo;
import us.betahouse.haetae.locale.dal.service.LocaleDORepoService;
import us.betahouse.haetae.locale.idfactory.BizIdFactory;
import us.betahouse.haetae.locale.dal.model.LocaleDO;
import us.betahouse.haetae.locale.model.basic.LocaleBO;
import us.betahouse.util.utils.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class LocaleDORepoServiceImpl implements LocaleDORepoService {
    private final Logger LOGGER = LoggerFactory.getLogger(LocaleDORepoServiceImpl.class);
    @Autowired
    LocaleDORepo localeDORepo;
    /**
     * id工厂
     */
    @Autowired
    private BizIdFactory localBizFactory;

    @Override
    public List<LocaleBO> queryAllLocales() {
        List<LocaleDO> localeDOList = localeDORepo.findAll();
        return CollectionUtils.toStream(localeDOList)
                .filter(Objects::nonNull)
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<LocaleBO> queryLocalesByStatus(String Status) {
        List<LocaleDO> localeDOList = localeDORepo.findByStatus(Status);
        return CollectionUtils.toStream(localeDOList)
                .filter(Objects::nonNull)
                .map(this::convert)
                .collect(Collectors.toList());
    }


    /**
     * 场地DO2BO
     *
     * @param localeDO
     * @return
     */
    @SuppressWarnings("unchecked")
    private LocaleBO convert(LocaleDO localeDO) {
        if (localeDO == null) {
            return null;
        }
        LocaleBO localeBO = new LocaleBO();
        localeBO.setLocaleId(localeDO.getLocaleId());
        localeBO.setLocaleName(localeDO.getLocaleName());
        localeBO.setLocaleCode(localeDO.getLocaleCode());
        localeBO.setStatus(localeDO.getStatus());
        return localeBO;
    }

    private LocaleDO convert(LocaleBO localeBO) {
        if (localeBO == null) {
            return null;
        }
        LocaleDO LocaleDO = new LocaleDO();
        LocaleDO.setLocaleId(localeBO.getLocaleId());
        LocaleDO.setLocaleName(localeBO.getLocaleName());
        LocaleDO.setLocaleCode(localeBO.getLocaleCode());
        LocaleDO.setStatus(localeBO.getStatus());
        return LocaleDO;
    }

}