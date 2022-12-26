package com.minswap.hrms.service.rank;

import com.minswap.hrms.repsotories.RankRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class RankServiceImplTest {
    @Mock
    RankRepository rankRepository;
    @InjectMocks
    RankServiceImpl rankServiceImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCheckRankExist() throws Exception {
        boolean result = rankServiceImpl.checkRankExist(Long.valueOf(1));
        Assert.assertEquals(false, result);
    }
}

