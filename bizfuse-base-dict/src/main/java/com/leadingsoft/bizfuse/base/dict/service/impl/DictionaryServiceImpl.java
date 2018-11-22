package com.leadingsoft.bizfuse.base.dict.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leadingsoft.bizfuse.base.dict.model.Dictionary;
import com.leadingsoft.bizfuse.base.dict.repository.DictionaryRepository;
import com.leadingsoft.bizfuse.base.dict.service.DictionaryService;
import com.leadingsoft.bizfuse.common.web.exception.CustomRuntimeException;

@Service
@Transactional
public class DictionaryServiceImpl implements DictionaryService {

    private Long latestLoadTime;

    @Autowired
    private DictionaryRepository dictionaryRepository;

    @Override
    public Dictionary createDictionary(final Dictionary dict) {
        final Long sameKeyCounts = this.dictionaryRepository.countByParentAndKey(dict.getParent(), dict.getKey());
        if (sameKeyCounts > 0) {
            throw new CustomRuntimeException("406", String.format("同级码表中Key[%s]已经存在.", dict.getKey()));
        }
        final int order = this.getNewDictionaryOrder(dict);
        dict.setSortIndex(order);
        this.dictionaryRepository.save(dict);
        return dict;
    }

    @Override
    public Dictionary updateDictionary(final Dictionary dict) {
        if (dict.getSortIndex() == -1) {
            final int order = this.getNewDictionaryOrder(dict);
            dict.setSortIndex(order);
        }
        this.dictionaryRepository.save(dict);
        return dict;
    }

    @Override
    public void deleteDictionary(final Long id) {
        final Dictionary dict = this.dictionaryRepository.findOne(id);
        if (dict == null) {
            return;
        }
        this.checkForDelete(dict);
        this.dictionaryRepository.delete(dict);
    }

    @Override
    public Dictionary discardDictionary(final Long id) {
        final Dictionary dict = this.dictionaryRepository.findOne(id);
        if (dict == null) {
            throw new CustomRuntimeException("404", "更新的数据不存在.");
        }
        dict.setDiscarded(true);
        this.dictionaryRepository.save(dict);
        return dict;
    }

    @Override
    public Dictionary changeDictionaryOrder(final Long id, final int upper) {
        final Dictionary model = this.dictionaryRepository.findOne(id);
        if (model == null) {
            throw new CustomRuntimeException("404", "更新的数据不存在.");
        }
        final List<Dictionary> brothers = this.getBrothers(model);
        final int changeStartIndex = brothers.indexOf(model);
        int changeEndIndex = changeStartIndex - upper;
        if (changeEndIndex < 0) {
            changeEndIndex = 0;
        }
        if (changeEndIndex >= (brothers.size() - 1)) {
            changeEndIndex = brothers.size() - 1;
        }
        if (changeStartIndex == changeEndIndex) {
            if (upper > 0) {
                throw new CustomRuntimeException("406", "已经到本级的第一行.");
            } else {
                throw new CustomRuntimeException("406", "已经到本级的最后一行.");
            }
        }
        final int offset = upper > 0 ? -1 : 1;
        for (int i = changeStartIndex; i != changeEndIndex; i += offset) {
            final Dictionary exchangeObj1 = brothers.get(i);
            final Dictionary exchangeObj2 = brothers.get(i + offset);
            final int exchangeObj1Order = exchangeObj1.getSortIndex();
            exchangeObj1.setSortIndex(exchangeObj2.getSortIndex());
            exchangeObj2.setSortIndex(exchangeObj1Order);
            brothers.set(i, exchangeObj2);
            brothers.set(i + offset, exchangeObj1);
            this.dictionaryRepository.save(exchangeObj1);
            this.dictionaryRepository.save(exchangeObj2);
        }
        return model;
    }

    private void checkForDelete(final Dictionary dict) {
        if (this.latestLoadTime == null) {
            return;
        }
        if (dict.getCreatedDate().getTime() < (this.latestLoadTime)) {
            throw new CustomRuntimeException("406", "码表数据已经同步到各客户端，不能删除");
        }
    }

    private List<Dictionary> getBrothers(final Dictionary model) {
        List<Dictionary> brothers = null;
        if (model.getParent() != null) {
            brothers = model.getParent().getChildren();
        } else {
            brothers = this.dictionaryRepository.findByCategoryAndParentIsNullOrderBySortIndexAsc(model.getCategory());
        }
        return brothers;
    }

    private int getNewDictionaryOrder(final Dictionary dict) {
        Dictionary maxIndexDict = null;
        if (dict.getParent() == null) {
            maxIndexDict =
                    this.dictionaryRepository
                            .findFirstByCategoryAndParentIsNullOrderBySortIndexDesc(dict.getCategory());
        } else {
            maxIndexDict = this.dictionaryRepository.findFirstByParentOrderBySortIndexDesc(dict.getParent());
        }

        if (maxIndexDict == null) {
            return 1;
        } else if (maxIndexDict.getSortIndex() == -1) {
            return 1;
        } else {
            return maxIndexDict.getSortIndex() + 1;
        }
    }
}
