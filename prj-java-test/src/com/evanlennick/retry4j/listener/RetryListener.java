package com.evanlennick.retry4j.listener;

import com.evanlennick.retry4j.Status;

public interface RetryListener {

    void onEvent(Status status);

}
