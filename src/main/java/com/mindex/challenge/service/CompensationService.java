package com.mindex.challenge.service;

import com.mindex.challenge.data.Compensation;

public interface CompensationService {
    Compensation createOrUpdate(Compensation employee);
    Compensation read(String id);
}
