package com.epam.esm.service;

import com.epam.esm.repository.GiftsTagsDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GiftsTagsService {

    private final GiftsTagsDao giftsTagsDao;

    public void save(Integer giftId, Integer tagId) {
        giftsTagsDao.save(giftId, tagId);

    }


}
